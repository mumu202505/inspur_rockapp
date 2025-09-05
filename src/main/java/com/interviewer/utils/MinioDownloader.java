package com.interviewer.utils;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MinioDownloader {
    private final MinioClient minioClient;
    private final ExecutorService executor;

    public MinioDownloader(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.executor = Executors.newFixedThreadPool(8);
    }


    public void downloadParallel(String bucketName, String objectName, String localFilePath) throws Exception {
        // 检查存储桶是否存在
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            throw new IllegalArgumentException("存储桶不存在");
        }

        // 获取文件信息
        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
        long fileSize = stat.size();

        // 使用临时文件下载
        String tempFilePath = localFilePath + ".tmp";
        File tempFile = new File(tempFilePath);

        try {
            // 创建空文件
            try (RandomAccessFile file = new RandomAccessFile(tempFile, "rw")) {
                file.setLength(fileSize);
            }

            // 并行下载分块
            List<Future<?>> futures = new ArrayList<>();
            long chunkSize = 10 * 1024 * 1024; // 10MB/块
            AtomicInteger errorCount = new AtomicInteger(0);

            for (long offset = 0; offset < fileSize; offset += chunkSize) {
                long finalOffset = offset;
                futures.add(executor.submit(() -> {
                    try {
                        downloadChunk(bucketName, objectName, tempFilePath,
                                finalOffset, Math.min(finalOffset + chunkSize, fileSize));
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        throw new RuntimeException("分块下载失败: " + finalOffset, e);
                    }
                }));
            }

            // 等待所有分块完成
            for (Future<?> future : futures) {
                future.get();
            }

            // 检查错误
            if (errorCount.get() > 0) {
                throw new IOException(errorCount.get() + "个分块下载失败");
            }

            // 验证文件大小
            if (tempFile.length() != fileSize) {
                throw new IOException("下载文件大小不匹配");
            }

            // 重命名为正式文件
            Files.move(tempFile.toPath(), Paths.get(localFilePath),
                    StandardCopyOption.REPLACE_EXISTING);

        } finally {
            // 清理临时文件
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private void downloadChunk(String bucketName, String objectName, String filePath,
                               long offset, long end) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "rw");
             FileChannel channel = file.getChannel();
             InputStream stream = minioClient.getObject(
                     GetObjectArgs.builder()
                             .bucket(bucketName)
                             .object(objectName)
                             .offset(offset)
                             .length(end - offset)
                             .build())) {

            channel.position(offset);
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = stream.read(buffer)) != -1) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                while (byteBuffer.hasRemaining()) {
                    channel.write(byteBuffer);
                }
            }
            channel.force(true);
        }
    }

    // Helper method to clean MappedByteBuffer
    private void clean(MappedByteBuffer buffer) {
        if (buffer == null) return;
        try {
            Method cleaner = buffer.getClass().getMethod("cleaner");
            cleaner.setAccessible(true);
            Object clean = cleaner.invoke(buffer);
            if (clean != null) {
                Method cleanMethod = clean.getClass().getMethod("clean");
                cleanMethod.setAccessible(true);
                cleanMethod.invoke(clean);
            }
        } catch (Exception e) {
            // Fallback to System.gc() if reflection fails
            System.gc();
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
