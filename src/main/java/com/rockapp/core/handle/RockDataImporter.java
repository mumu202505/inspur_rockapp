package com.rockapp.core.handle;

import io.minio.*;
import io.minio.errors.*;
import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class RockDataImporter {

    // 配置信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static final String MINIO_ENDPOINT = "http://localhost:9005";
    private static final String MINIO_ACCESS_KEY = "U6hN0jW4Y7L3o5GpemxN";
    private static final String MINIO_SECRET_KEY = "KJLl5eS0stvK17w59jIKWC5tkT2YGagMTBVA5zP9";
    private static final String MINIO_BUCKET_NAME = "mytest";

    private static MinioClient minioClient;

    public static void main(String[] args) {
        // 初始化MinIO客户端
        minioClient = MinioClient.builder()
                .endpoint(MINIO_ENDPOINT)
                .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                .build();

        String filePath = "D:\\inspur\\文档相关\\岩性知识库4.0.docx";
        processRockDataFile(filePath);
    }

    public static void processRockDataFile(String filePath) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 1. 解析Word文档
            List<RockData> rockDataList = parseWordDocument(filePath);

            // 2. 处理图片并获取图片路径映射
            Map<String, List<String>> imageMap = extractAndUploadImages(filePath);

            // 3. 存入数据库
            for (RockData rockData : rockDataList) {
                saveRockData(conn, rockData, imageMap.getOrDefault(rockData.getRockName(), new ArrayList<>()));
            }

            System.out.println("成功导入 " + rockDataList.size() + " 条岩性数据");
        } catch (Exception e) {
            System.err.println("处理文件时出错:");
            e.printStackTrace();
        }
    }

    private static List<RockData> parseWordDocument(String filePath) throws Exception {
        List<RockData> rockDataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            RockData currentRock = null;
            String currentSection = null;
            StringBuilder contentBuilder = new StringBuilder();

            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText().trim();
                if (text.isEmpty()) continue;

                // 匹配岩性标题 (如 "1、二长花岗岩（Adamellite）")
                Matcher rockMatcher = Pattern.compile("^\\d+、(.+?)[（(](.+?)[）)]").matcher(text);
                if (rockMatcher.find()) {
                    // 保存上一个岩性数据
                    if (currentRock != null) {
                        if (currentSection != null) {
                            currentRock.setSectionContent(currentSection, contentBuilder.toString().trim());
                        }
                        rockDataList.add(currentRock);
                    }

                    // 创建新的岩性数据对象
                    currentRock = new RockData();
                    currentRock.setRockName(rockMatcher.group(1).trim());
                    currentRock.setEnglishName(rockMatcher.group(2).trim());
                    currentSection = null;
                    contentBuilder = new StringBuilder();
                    continue;
                }

                // 匹配章节标题 (如 "（1）元素矿物组成")
                Matcher sectionMatcher = Pattern.compile("[（(](\\d+)[）)](.+)").matcher(text);
                if (sectionMatcher.find()) {
                    // 保存上一个章节内容
                    if (currentRock != null && currentSection != null) {
                        currentRock.setSectionContent(currentSection, contentBuilder.toString().trim());
                    }

                    // 设置新章节
                    currentSection = parseSectionName(sectionMatcher.group(1), sectionMatcher.group(2).trim());
                    contentBuilder = new StringBuilder();
                    continue;
                }

                // 普通段落内容
                if (currentRock != null && currentSection != null) {
                    contentBuilder.append(text).append("\n");
                }
            }

            // 保存最后一个岩性数据
            if (currentRock != null) {
                if (currentSection != null) {
                    currentRock.setSectionContent(currentSection, contentBuilder.toString().trim());
                }
                rockDataList.add(currentRock);
            }
        }

        return rockDataList;
    }

    private static String parseSectionName(String sectionNum, String sectionTitle) {
        // 将章节编号转换为标准字段名
        switch (sectionNum) {
            case "1": return "elemental_composition";
            case "2": return "structure";
            case "3": return "physical_properties";
            case "4": return "genesis";
            case "5": return "common_uses";
            case "6": return "confusing_rocks";
            case "7": return "references";
            default: return sectionTitle;
        }
    }

    private static Map<String, List<String>> extractAndUploadImages(String filePath) throws Exception {
        Map<String, List<String>> imageMap = new HashMap<>();
        String currentRockName = null;

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            // 确保MinIO存储桶存在
            ensureBucketExists();

            // 首先建立岩性名称与图片的关联
            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText().trim();
                Matcher rockMatcher = Pattern.compile("^\\d+、(.+?)[（(]").matcher(text);
                if (rockMatcher.find()) {
                    currentRockName = rockMatcher.group(1).trim();
                }
            }

            // 上传图片并建立映射
            int imageIndex = 0;
            for (XWPFPictureData picture : doc.getAllPictures()) {
                String ext = picture.suggestFileExtension();
                String objectName = "rocks/" + UUID.randomUUID() + "." + ext;

                // 上传到MinIO
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(MINIO_BUCKET_NAME)
                                .object(objectName)
                                .stream(new ByteArrayInputStream(picture.getData()), picture.getData().length, -1)
                                .contentType(getContentType(ext))
                                .build()
                );

                // 关联到当前岩性
                if (currentRockName != null) {
                    String imageUrl = MINIO_BUCKET_NAME + "/" + objectName;
                    imageMap.computeIfAbsent(currentRockName, k -> new ArrayList<>()).add(imageUrl);
                }

                imageIndex++;
            }
        }

        return imageMap;
    }

    private static void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(MINIO_BUCKET_NAME)
                .build());

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(MINIO_BUCKET_NAME)
                    .build());
        }
    }

    private static void saveRockData(Connection conn, RockData rockData, List<String> imagePaths) throws SQLException {
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
            stmt.setString(10, ""); // 特征
            stmt.setString(11, ""); // 成份
            stmt.setString(12, imagePaths.get(0));
            stmt.setString(13, "system_import");
            stmt.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(15, 0);

            stmt.executeUpdate();
            System.out.println("已导入: " + rockData.getRockName());
        }
    }

    private static String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case "png": return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "gif": return "image/gif";
            case "bmp": return "image/bmp";
            default: return "application/octet-stream";
        }
    }

    // 岩性数据内部类
    static class RockData {
        private String rockName;
        private String englishName;
        private Map<String, String> sections = new HashMap<>();

        public void setRockName(String name) { this.rockName = name; }
        public String getRockName() { return rockName; }

        public void setEnglishName(String name) { this.englishName = name; }
        public String getEnglishName() { return englishName; }

        public void setSectionContent(String section, String content) {
            sections.put(section, content);
        }

        public String getSectionContent(String section) {
            return sections.getOrDefault(section, "");
        }
    }
}
