package com.interviewer.core.handle;

import cn.hutool.core.lang.UUID;
import com.interviewer.dto.RockData;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RockDocumentParser {
    private final String filePath;
    private final MinioClient minioClient;
    private String currentRockName;
    private List<XWPFPictureData> allPictures;
    private int currentPictureIndex = 0;

    public RockDocumentParser(String filePath, MinioClient minioClient) {
        this.filePath = filePath;
        this.minioClient = minioClient;
    }

    public List<RockData> parse() throws Exception {
        List<RockData> rockDataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            // 获取文档中所有图片
            this.allPictures = doc.getAllPictures();

            RockData currentRock = null;
            String currentSection = null;
            StringBuilder contentBuilder = new StringBuilder();

            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText().trim();
                if (text.isEmpty()) continue;

                // 匹配岩性标题
                Matcher rockMatcher = Pattern.compile("^\\d+、(.+?)[（(](.+?)[）)]").matcher(text);
                if (rockMatcher.find()) {
                    // 保存上一个岩性数据
                    if (currentRock != null) {
                        if (currentSection != null) {
                            currentRock.setSectionContent(currentSection, contentBuilder.toString().trim());
                        }
                        // 分配图片给当前岩性
                        assignImage(currentRock);
                        rockDataList.add(currentRock);
                    }

                    // 创建新的岩性数据对象
                    currentRock = new RockData();
                    currentRock.setRockName(rockMatcher.group(1).trim());
                    currentRock.setEnglishName(rockMatcher.group(2).trim());
                    this.currentRockName = currentRock.getRockName();
                    currentSection = null;
                    contentBuilder = new StringBuilder();
                    continue;
                }

                // 匹配章节标题
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
                assignImage(currentRock);
                rockDataList.add(currentRock);
            }
        }

        return rockDataList;
    }

    private void assignImage(RockData rockData) throws Exception {
        if (currentPictureIndex < allPictures.size()) {
            XWPFPictureData picture = allPictures.get(currentPictureIndex++);
            String ext = picture.suggestFileExtension();
            String objectName = "rocks/" + rockData.getRockName().replaceAll("\\s+", "-") +
                    "-" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;

            // 上传到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("mytest")
                            .object(objectName)
                            .stream(new ByteArrayInputStream(picture.getData()),
                                    picture.getData().length,
                                    -1)
                            .contentType(getContentType(ext))
                            .build()
            );

            String imageUrl = "http://localhost:9005" + "/" + "mytest" + "/" + objectName;
            rockData.setImagePath(imageUrl);
        }
    }

    private String parseSectionName(String sectionNum, String sectionTitle) {
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

    private String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case "png": return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "gif": return "image/gif";
            case "bmp": return "image/bmp";
            default: return "application/octet-stream";
        }
    }
}
