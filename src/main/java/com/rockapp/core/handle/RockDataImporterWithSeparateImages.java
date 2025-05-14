package com.rockapp.core.handle;

import com.rockapp.dto.RockData;
import io.minio.*;
import io.minio.errors.*;
import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class RockDataImporterWithSeparateImages {

    // 数据库配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    // MinIO配置
    private static final String MINIO_ENDPOINT = "http://localhost:9005";
    private static final String MINIO_ACCESS_KEY = "U6hN0jW4Y7L3o5GpemxN";
    private static final String MINIO_SECRET_KEY = "KJLl5eS0stvK17w59jIKWC5tkT2YGagMTBVA5zP9";
    private static final String MINIO_BUCKET_NAME = "mytest";

    private static MinioClient minioClient;

    public static void main(String[] args) {
        // 初始化MinIO客户端
        try {
            minioClient = MinioClient.builder()
                    .endpoint(MINIO_ENDPOINT)
                    .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                    .build();

            String filePath = "D:\\inspur\\文档相关\\岩性知识库4.0.docx";
            processRockDataFile(filePath);
        } catch (Exception e) {
            System.err.println("初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void processRockDataFile(String filePath) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 1. 解析Word文档并提取图片
            RockDocumentParser parser = new RockDocumentParser(filePath, minioClient);
            List<RockData> rockDataList = parser.parse();

            // 2. 存入数据库
            for (RockData rockData : rockDataList) {
                saveRockData(conn, rockData);
                System.out.println("成功导入: " + rockData.getRockName());
            }
        } catch (Exception e) {
            System.err.println("处理文件时出错:");
            e.printStackTrace();
        }
    }

    private static void saveRockData(Connection conn, RockData rockData) throws SQLException {
        String sql = "INSERT INTO base_sync_lithology_knowledge (" +
                "f_id, f_class_name, f_elemental_minera, f_structural_const, " +
                "f_physical_propert, f_factorial_analys, f_common_use, " +
                "f_confounding_lith, f_references, f_feature, f_component, " +
                "f_image_paths, f_create_user_name, f_create_time, f_is_delete) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, rockData.getRockName());
            stmt.setString(3, rockData.getSectionContent("elemental_composition"));
            stmt.setString(4, rockData.getSectionContent("structure"));
            stmt.setString(5, rockData.getSectionContent("physical_properties"));
            stmt.setString(6, rockData.getSectionContent("genesis"));
            stmt.setString(7, rockData.getSectionContent("common_uses"));
            stmt.setString(8, rockData.getSectionContent("confusing_rocks"));
            stmt.setString(9, rockData.getSectionContent("references"));
            stmt.setString(10, rockData.getFeature());
            stmt.setString(11, rockData.getComponent());
            stmt.setString(12, String.join(";", rockData.getImagePath()));
            stmt.setString(13, "system_import");
            stmt.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(15, 0);

            stmt.executeUpdate();
        }
    }
}
