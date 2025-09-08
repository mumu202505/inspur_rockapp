package com.interviewer.controller;

import com.interviewer.utils.MinIOUtils;
import com.interviewer.utils.MultipartFileUploader;
import com.interviewer.utils.ResultUtil;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 *
 * @Author liyy
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/file")
@Tag(name = "文件管理", description = "文件管理相关接口") // 类级别分组
public class FileUploadController {

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

    @Operation(summary = "文件上传返回url")
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public ResultUtil upload(@RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, io.minio.errors.ServerException {
        String objectName = minioUtil.upload(file);
        if (null != objectName) {
            return ResultUtil.success(minIoUrl + "/" + bucketName + "/" + objectName);
        }
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @Operation(summary = "删除文件")
    @GetMapping(value = "/delete")
    public ResultUtil delete(String fileName) {
        boolean remove = minioUtil.remove(fileName);
        if ( remove){
            return ResultUtil.SUCCESS_NO_DATA;
        }else {
            return ResultUtil.ERROR_DELETE;
        }
    }

    @Operation(summary = "下载文件")
    @GetMapping(value = "/download")
    public void download(String fileName, HttpServletResponse res) {
        minioUtil.download(fileName,res);
    }


}
