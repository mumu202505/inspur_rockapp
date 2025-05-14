package com.rockapp.controller;

import com.rockapp.dto.FileInitDto;
import com.rockapp.utils.MinIOUtils;
import com.rockapp.utils.MultipartFileUploader;
import com.rockapp.utils.ResultUtil;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/file")
@Tag(name = "文件上传", description = "文件上传相关操作")
public class FileUploadController {
    //    @Autowired
//    FileUploadService fileUploadService;
//
//
//    @GetMapping("/checkChunks")
//    @Operation(summary = "根据MD5获取已上传的文件块编号")
//    public ResultUtil getUploadedChunks(@RequestParam String fileMd5) {
//        Set<Integer> uploadedChunks = fileUploadService.getUploadedChunks(fileMd5);
//        return ResultUtil.success(uploadedChunks);
//    }
//
//
//    @PostMapping("/uploadChunk")
//    @Operation(summary = "上传分片")
//    public ResultUtil uploadChunk(
//            @Parameter(description = "分片文件", required = true)
//            @RequestParam MultipartFile file,
//
//            @Parameter(description = "分片编号", required = true)
//            @RequestParam("chunkNum") int chunkNum,
//
//            @Parameter(description = "文件MD5值", required = true)
//            @RequestParam("fileMd5") String fileMd5,
//
//            @Parameter(description = "文件名", required = true)
//            @RequestParam("fileName") String fileName,
//
//            @Parameter(description = "总片数", required = true)
//            @RequestParam("totalChunks") int totalchunks
//        ) throws Exception {
//        String s = fileUploadService.uploadChunk(file, chunkNum, fileMd5, fileName, totalchunks);
//        return ResultUtil.success(s);
//    }
//
//    @PostMapping("/mergeChunks")
//    @Operation(summary = "合并分片")
//    public ResultUtil mergeChunks(
//            @RequestParam String fileMd5,
//            @RequestParam String fileName) throws Exception {
//        String mergedPath = fileUploadService.mergeChunks(fileMd5, fileName);
//        return ResultUtil.success("文件合并成功：" + mergedPath);
//    }
    private final MultipartFileUploader fileUploader;
    @Autowired
    private MinIOUtils minioUtil;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.url}")
    private String minIoUrl;

    public FileUploadController(MultipartFileUploader fileUploader) {
        this.fileUploader = fileUploader;
    }

    @Operation(summary = "初始化上传")
    @PostMapping("/init")
    public ResultUtil initUpload(FileInitDto fileInitDto) {
        return ResultUtil.success(fileUploader.initUpload(fileInitDto));
    }

    @Operation(summary = "上传分片")
    @PostMapping(value = "/chunk", headers = "content-type=multipart/form-data")
    public ResultUtil uploadChunk(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("file") MultipartFile file) throws IOException {
        fileUploader.uploadMultipartChunk(uploadId, chunkIndex, file);
        return ResultUtil.success("分块上传成功");
    }

    @PostMapping("/complete")
    @Operation(summary = "合并分片")
    public ResultUtil completeUpload(@RequestParam("uploadId") String uploadId) throws Exception {
        String objectName = fileUploader.completeUpload(uploadId);
        return ResultUtil.success(objectName);
    }

    @GetMapping("/progress")
    @Operation(summary = "获取上传进度")
    public ResultUtil getProgress(@RequestParam("uploadId") String uploadId) {
        return ResultUtil.success(fileUploader.getUploadProgress(uploadId));
    }

    @Operation(summary = "断点续传")
    @PostMapping(value = "/check-or-init")
    public ResultUtil checkOrInitUpload(
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("fileSize") long fileSize,
            @RequestParam(value = "uploadId", required = false) String existingUploadId) {
        if (existingUploadId != null) {
            // 验证现有uploadId是否匹配
            Map<String, String> progress = fileUploader.getUploadProgress(existingUploadId);
            if (!progress.isEmpty() && fileMd5.equals(progress.get("fileMd5"))) {
                return ResultUtil.success(existingUploadId);
            }
        }
        return ResultUtil.success(fileUploader.checkOrInitUpload(fileMd5, fileSize));
    }

    @Operation(summary = "文件上传返回url")
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public ResultUtil upload(@RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String objectName = minioUtil.upload(file);
        if (null != objectName) {
            return ResultUtil.success(minIoUrl + "/" + bucketName + "/" + objectName);
        }
        return ResultUtil.SUCCESS_NO_DATA;
    }



//    @GetMapping("/test111")
//    @Operation(summary = "测试分片上传")
//    public ResultUtil test111() {
//        String filePath = "D:\\20250422150828.jpg"; // 替换为你的文件路径
//        File file = new File(filePath);
//        try {
//            // 计算MD5
//            String md5 = calculateMD5(file);
//            System.out.println("文件MD5值: " + md5);
//            //初始化
//            FileInitDto fileInitDto = new FileInitDto();
//            fileInitDto.setFileMd5(md5);
//            fileInitDto.setTotalChunks("4");
//            fileInitDto.setFileSize(file.length() + "");
//            String s = fileUploader.initUpload(fileInitDto);
//            // 文件分片并上传
//            splitAndUploadFile(file, s);
//        } catch (NoSuchAlgorithmException e) {
//            System.err.println("MD5算法不可用: " + e.getMessage());
//        } catch (IOException e) {
//            System.err.println("IO错误: " + e.getMessage());
//        }
//        return ResultUtil.success("合并上传成功" );
//    }

    // 计算文件的MD5值
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

    // 文件分片并上传（每片1MB）
    public void splitAndUploadFile(File file, String uploadId) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + file.getAbsolutePath());
        }
        // 1MB = 1024 * 1024 bytes
        int chunkSize = 1 * 1024 * 1024;
        byte[] buffer = new byte[chunkSize];
        int partNumber = 0;
        try (InputStream is = new FileInputStream(file)) {
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                // 如果是最后一次读取，可能不满1MB，需要创建正确大小的数组
                byte[] actualChunkData;
                if (bytesRead < chunkSize) {
                    actualChunkData = new byte[bytesRead];
                    System.arraycopy(buffer, 0, actualChunkData, 0, bytesRead);
                } else {
                    actualChunkData = buffer;
                }
                // 调用上传方法
                boolean success = fileUploader.uploadChunk(uploadId, partNumber, actualChunkData, file.getName());
                if (!success) {
                    throw new IOException("上传分片失败: " + partNumber);
                }
                partNumber++;
                System.out.println("已上传分片: " + partNumber + " (" + bytesRead + " bytes)");
            }
            //全部上传完调用合并
            fileUploader.completeUpload(uploadId);
        } catch (MinioException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        System.out.println("文件分片上传完成，共 " + partNumber + " 个分片");
    }

}
