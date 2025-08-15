package com.rockapp.utils;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.lang.UUID;
import com.rockapp.core.exception.ServiceException;
import com.rockapp.enums.result.SysResultEnum;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MinIOUtils {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 查看存储bucket是否存在
     *
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     *
     * @return Boolean
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除存储bucket
     *
     * @return Boolean
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllBuckets() {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            return buckets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 文件上传
     *
     * @param file 文件
     * @return Boolean
     */
    public String upload(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 校验文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException("文件名不能为空");
        }

        // 获取文件扩展名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 白名单校验
        Set<String> allowedExtensions = new HashSet<>(Arrays.asList(
                "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx","apk"
        ));

        if (!allowedExtensions.contains(fileExtension)) {
            throw new ServiceException(SysResultEnum.PROMPT_FILE_EXISTED);
        }

        // Content-Type校验
        String contentType = file.getContentType();
        if (contentType == null || contentType.startsWith("application/x-")
                || contentType.startsWith("text/x-")) {
            throw new ServiceException(SysResultEnum.PROMPT_FILE_EXISTED);
        }

        try {
             //文件内容校验
            if (isExecutableScript(file.getInputStream())) {
                throw new ServiceException(SysResultEnum.PROMPT_FILE_CONTENT_EXISTED);
            }

            // 生成新文件名
            String fileName = generateUUIDWithoutDashes() + "." + fileExtension;
            String objectName = formatTodayDate("yyyy-MM/dd") + "/" + fileName;

            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build();

            minioClient.putObject(objectArgs);
            return objectName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(SysResultEnum.PROMPT_FILE_RED_EXISTED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(SysResultEnum.PROMPT_FILE_PUT_EXISTED);
        }
    }

    private boolean isExecutableScript(InputStream is) throws IOException {
        // 包装原始流以确保支持 mark/reset
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }

        byte[] fileHead = new byte[20];
        try {
            is.mark(21);  // 标记位置，允许最多读取21字节后reset

            int bytesRead = is.read(fileHead);
            is.reset();   // 重置流位置

            if (bytesRead < 20) {
                return false;  // 文件太小，不可能是可执行脚本
            }

            // 转换为字符串时指定字符集，避免平台依赖问题
            String headStr = new String(fileHead, StandardCharsets.UTF_8);

            // 检查常见脚本文件签名
            return headStr.contains("<?php") ||   // PHP
                    headStr.contains("<script") ||  // JavaScript/HTML
                    headStr.contains("<%") ||       // JSP/ASP
                    headStr.startsWith("#!");      // Unix shebang
        } catch (IOException e) {
            // 如果reset失败，关闭原始流并抛出异常
            try {
                is.close();
            } catch (IOException ignored) {}
            throw new IOException("Failed to check file content", e);
        }
    }

    /**
     * 预览图片
     *
     * @param fileName
     * @return
     */
    public String preview(String fileName) {
        // 查看文件地址
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(bucketName).object(fileName).method(Method.GET).build();
        try {
            String url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名称
     * @param res      response
     * @return Boolean
     */
    public void download(String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                // 设置强制下载不打开
                // res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects() {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items;
    }

    /**
     * 删除
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public boolean remove(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // 封装生成不带横杠UUID的方法
    public static String generateUUIDWithoutDashes() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    // 封装格式化今天日期的方法
    public static String formatTodayDate(String format) {
        // 定义日期格式
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        // 获取今天的日期
        Date today = new Date();

        // 格式化日期并返回
        return formatter.format(today);
    }

}

