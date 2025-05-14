//package com.rockapp.utils;
//
//import io.minio.*;
//import io.minio.errors.*;
//import io.minio.messages.Part;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MinioUploader {
//
//    private final MinioClient minioClient;
//
//    public MinioUploader(MinioClient minioClient) {
//        this.minioClient = minioClient;
//    }
//
//    public String uploadWithMultipart(MultipartFile file, String bucketName) throws Exception {
//        // 1. 初始化分片上传
//        InitiateMultipartUploadResponse initResponse = minioClient.initiateMultipartUpload(
//                InitiateMultipartUploadArgs.builder()
//                        .bucket(bucketName)
//                        .object(file.getOriginalFilename())
//                        .build());
//
//        String uploadId = initResponse.uploadId();
//        List<Part> parts = new ArrayList<>();
//
//        try (InputStream inputStream = file.getInputStream()) {
//            // 2. 分片上传
//            byte[] buffer = new byte[5 * 1024 * 1024]; // 5MB分片
//            int partNumber = 1;
//            int bytesRead;
//
//            while ((bytesRead = inputStream.read(buffer)) > 0) {
//                UploadPartResponse partResponse = minioClient.uploadPart(
//                        UploadPartArgs.builder()
//                                .bucket(bucketName)
//                                .object(file.getOriginalFilename())
//                                .uploadId(uploadId)
//                                .partNumber(partNumber)
//                                .stream(new ByteArrayInputStream(buffer, 0, bytesRead), bytesRead, -1)
//                                .build());
//
//                parts.add(new Part(partNumber, partResponse.etag()));
//                partNumber++;
//            }
//
//            // 3. 完成上传
//            minioClient.completeMultipartUpload(
//                    CompleteMultipartUploadArgs.builder()
//                            .bucket(bucketName)
//                            .object(file.getOriginalFilename())
//                            .uploadId(uploadId)
//                            .parts(parts)
//                            .build());
//
//            return "上传成功";
//        } catch (Exception e) {
//            // 4. 出错时中止上传
//            minioClient.abortMultipartUpload(
//                    AbortMultipartUploadArgs.builder()
//                            .bucket(bucketName)
//                            .object(file.getOriginalFilename())
//                            .uploadId(uploadId)
//                            .build());
//            throw e;
//        }
//    }
//}
