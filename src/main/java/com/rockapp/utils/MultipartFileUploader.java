package com.rockapp.utils;

import com.rockapp.core.exception.ServiceException;
import com.rockapp.dto.FileInitDto;
import com.rockapp.enums.result.RedisResultEnum;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class MultipartFileUploader {
    private final Jedis jedis;
    private final MinioClient minioClient;
    private final int chunkSize; // 分片大小，默认1MB
    private final long uploadExpireTime; // 上传信息在Redis中的过期时间


    public MultipartFileUploader() {
        this.jedis = new Jedis("127.0.0.1", 6379);
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9005")
                .credentials("157sfN5Nd5DGXStBpj17", "FMA9ogJ5IJ15tX2iVIT8SMArTjHvuk80R9TVqqYh")
                .build();
        this.chunkSize = 1024 * 1024; // 1MB
        this.uploadExpireTime = TimeUnit.HOURS.toSeconds(24); // 24小时
    }

    public String initUpload(FileInitDto fileInitDto) {
        String uploadId = "upload_" + UUID.randomUUID().toString();

        Map<String, String> uploadInfo = new HashMap<>();
        uploadInfo.put("fileMd5", fileInitDto.getFileMd5());
        uploadInfo.put("fileSize", String.valueOf(fileInitDto.getFileSize()));
        uploadInfo.put("totalChunks", fileInitDto.getTotalChunks());
        uploadInfo.put("uploadedChunks", "0");
        uploadInfo.put("originalFilename", "");

        jedis.hmset("upload:" + uploadId, uploadInfo);
        jedis.expire("upload:" + uploadId, uploadExpireTime);

        return uploadId;
    }

    /**
     * 上传文件分片
     *
     * @param uploadId         上传ID
     * @param chunkIndex       分片索引
     * @param chunkData        分片数据
     * @param originalFilename 原始文件名
     * @return 是否成功
     */
    public boolean uploadChunk(String uploadId, int chunkIndex, byte[] chunkData, String originalFilename) {
        String redisKey = "upload:" + uploadId;

        if (!jedis.exists(redisKey)) {
            throw new ServiceException(RedisResultEnum.REDIS_NOT_ERROR);
        }

        // 如果是第一个分片，保存原始文件名
        if (chunkIndex == 0) {
            jedis.hset(redisKey, "originalFilename", originalFilename);
        }

        String chunkKey = "chunk:" + uploadId + ":" + chunkIndex;
        jedis.set(chunkKey.getBytes(), chunkData);
        jedis.expire(chunkKey, uploadExpireTime);

        jedis.hincrBy(redisKey, "uploadedChunks", 1);

        return true;
    }

    /**
     * 合并文件并上传到MinIO
     *
     * @param uploadId 上传ID
     * @return 存储的对象名称（包含路径）
     */
    public String completeUpload(String uploadId)
            throws IOException, NoSuchAlgorithmException, MinioException, InvalidKeyException {
        String redisKey = "upload:" + uploadId;
        Map<String, String> uploadInfo = jedis.hgetAll(redisKey);

        if (uploadInfo.isEmpty()) {
            throw new RuntimeException("Upload session not found or expired");
        }

        String fileMd5 = uploadInfo.get("fileMd5");
        long fileSize = Long.parseLong(uploadInfo.get("fileSize"));
        int totalChunks = Integer.parseInt(uploadInfo.get("totalChunks"));
        int uploadedChunks = Integer.parseInt(uploadInfo.get("uploadedChunks"));
        String originalFilename = uploadInfo.get("originalFilename");

        if (uploadedChunks < totalChunks) {
            throw new RuntimeException("Not all chunks have been uploaded");
        }

        // 生成存储路径
        String objectName = generateObjectName(originalFilename);
        File tempFile = File.createTempFile("minio_upload_", ".tmp");

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");

            for (int i = 0; i < totalChunks; i++) {
                String chunkKey = "chunk:" + uploadId + ":" + i;
                byte[] chunkData = jedis.get(chunkKey.getBytes());

                if (chunkData == null) {
                    throw new RuntimeException("Missing chunk: " + i);
                }
                fos.write(chunkData);
                md5Digest.update(chunkData);
                jedis.del(chunkKey);
            }
            // 转为 InputStream
            InputStream inputStream = new FileInputStream(tempFile);
            String mergedFileMd5 = getFileMD5(tempFile);

            if (!mergedFileMd5.equals(fileMd5)) {
                throw new RuntimeException("MD5校验失败");
            }
            //上传至minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("mytest")
                            .stream(inputStream, tempFile.length(), -1)
                            .object(objectName)
                            .build());
            jedis.del(redisKey);
            return objectName;
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * 检查并恢复上传
     *
     * @param fileMd5  文件MD5
     * @param fileSize 文件大小
     * @return 存在的uploadId或新创建的uploadId
     */
    public String checkOrInitUpload(String fileMd5, long fileSize) {
        // 查找已有的上传记录
        Set<String> keys = jedis.keys("upload:*");
        for (String key : keys) {
            Map<String, String> info = jedis.hgetAll(key);
            if (fileMd5.equals(info.get("fileMd5")) &&
                    fileSize == Long.parseLong(info.get("fileSize"))) {
                // 返回现有uploadId
                return key.substring(key.indexOf(':') + 1);
            }
        }
        // 没有找到，创建新上传
        FileInitDto fileInitDto = new FileInitDto();
        fileInitDto.setFileMd5(fileMd5);
        fileInitDto.setFileSize(String.valueOf(fileSize));
        fileInitDto.setTotalChunks(String.valueOf((fileSize + chunkSize - 1) / chunkSize));
        return initUpload(fileInitDto);
    }

    /**
     * 生成存储对象名称
     *
     * @param originalFilename 原始文件名
     * @return 存储路径
     */
    private String generateObjectName(String originalFilename) {
        // 使用日期目录+UUID+原始文件名扩展名
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "uploads/" + dateDir + "/" + UserUtil.getUser().getFUserName() + "/" + UUID.randomUUID() + fileExt;
    }

    /**
     * 计算MultipartFile的MD5
     *
     * @param file 要计算的文件
     * @return MD5字符串
     */
    public static String calculateFileMd5(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return DigestUtils.md5Hex(is);
        }
    }

    /**
     * 处理MultipartFile分片上传
     *
     * @param uploadId   上传ID
     * @param chunkIndex 分片索引
     * @param file       分片文件
     * @return 是否成功
     */
    public boolean uploadMultipartChunk(String uploadId, int chunkIndex, MultipartFile file) throws IOException {
        byte[] chunkData = file.getBytes();
        return uploadChunk(uploadId, chunkIndex, chunkData, file.getOriginalFilename());
    }

    /**
     * 获取上传进度
     *
     * @param uploadId 上传ID
     * @return 上传进度信息
     */
    public Map<String, String> getUploadProgress(String uploadId) {
        String redisKey = "upload:" + uploadId;
        return jedis.hgetAll(redisKey);
    }

    //自测计算MD5
    public static String calculateMD5(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        byte[] md5Bytes = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : md5Bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //正式
    public static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hash = digest.digest();
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
