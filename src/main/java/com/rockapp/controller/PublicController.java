package com.rockapp.controller;

import com.google.common.net.HttpHeaders;
import com.rockapp.dto.AccountDto;
import com.rockapp.dto.AccountLoginDto;
import com.rockapp.dto.UpdatePasswordDto;
import com.rockapp.service.BaseUserService;
import com.rockapp.service.UniversalityService;
import com.rockapp.utils.AesUtil;
import com.rockapp.utils.ResultUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author liyy
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关操作") // 类级别分组
public class PublicController {
    @Autowired
    private BaseUserService baseUserService;
    @Autowired
    private UniversalityService universalityService;


    @PostMapping("/login")
    @ResponseBody
    @Operation(summary = "用户登录")
    public ResultUtil login(@RequestBody AccountLoginDto account, HttpServletRequest httpServletRequest) {
        return ResultUtil.success(baseUserService.login(account,httpServletRequest));
    }


    @PostMapping("/sign")
    @ResponseBody
    @Operation(summary = "用户注册")
    public ResultUtil sign(@RequestBody AccountDto account) {
        baseUserService.sign(account);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/getBackPsd")
    @ResponseBody
    @Operation(summary = "找回密码/根据验证码修改密码")
    public ResultUtil getBackPsd(
            @RequestBody UpdatePasswordDto updatePasswordDto) {
        baseUserService.getBackPsd(updatePasswordDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/senTextMessage")
    @ResponseBody
    @Operation(summary = "发送短信")
    public ResultUtil senTextMessage(@RequestParam("phone")String phone) {
        universalityService.senTextMessage(phone);
        return ResultUtil.success("验证码已发送!");
    }


    @Resource
    private MinioClient minioClient;
    @GetMapping("/download")
    @Operation(summary = "文件下载接口（支持多线程分片下载和断点续传）")
    public void downloadFile(
            @RequestParam String objectName,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            @RequestParam(value = "threads", defaultValue = "4") int threadCount,
            HttpServletResponse response) throws Exception {

        final String bucketName = "mytest";
        final int maxRetryAttempts = 3; // 最大重试次数
        final long retryDelayMs = 1000; // 重试延迟时间(毫秒)

        // 获取文件信息
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

        long fileSize = stat.size();

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + objectName + "\"");
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));

        // 如果不支持多线程下载或文件较小，使用单线程下载
        if (fileSize <= 10 * 1024 * 1024 || threadCount <= 1) {
            downloadSingleThread(bucketName, objectName, fileSize, response, maxRetryAttempts, retryDelayMs);
            return;
        }

        // 计算每个线程下载的块大小
        long chunkSize = fileSize / threadCount;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Void>> futures = new ArrayList<>();
        PipedOutputStream[] posArray = new PipedOutputStream[threadCount];
        PipedInputStream[] pisArray = new PipedInputStream[threadCount];

        try {
            // 创建管道流数组
            for (int i = 0; i < threadCount; i++) {
                posArray[i] = new PipedOutputStream();
                pisArray[i] = new PipedInputStream(posArray[i], 1024 * 1024); // 1MB缓冲区
            }

            // 启动下载线程
            for (int i = 0; i < threadCount; i++) {
                final int threadIndex = i;
                final long start = i * chunkSize;
                final long end = (i == threadCount - 1) ? fileSize - 1 : start + chunkSize - 1;
                final PipedOutputStream pos = posArray[i];

                futures.add(executor.submit(() -> {
                    int retryCount = 0;
                    while (retryCount <= maxRetryAttempts) {
                        try {
                            downloadChunk(bucketName, objectName, start, end, pos, maxRetryAttempts, retryDelayMs);
                            return null;
                        } catch (Exception e) {
                            retryCount++;
                            if (retryCount > maxRetryAttempts) {
                                throw new RuntimeException("下载分片失败，超过最大重试次数: " + start + "-" + end, e);
                            }
                            try {
                                Thread.sleep(retryDelayMs * retryCount); // 指数退避
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("下载分片被中断: " + start + "-" + end, ie);
                            }
                        } finally {
                            if (retryCount > maxRetryAttempts || Thread.currentThread().isInterrupted()) {
                                pos.close();
                            }
                        }
                    }
                    return null;
                }));
            }

            // 合并下载的数据流
            try (OutputStream outputStream = response.getOutputStream()) {
                SequenceInputStream sis = new SequenceInputStream(Collections.enumeration(Arrays.asList(pisArray)));
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = sis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            // 等待所有线程完成
            for (Future<Void> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdown();
            // 确保所有管道流关闭
            for (PipedInputStream pis : pisArray) {
                try { pis.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    private void downloadSingleThread(String bucketName, String objectName, long fileSize,
                                      HttpServletResponse response, int maxRetryAttempts, long retryDelayMs) throws Exception {
        int retryCount = 0;
        while (retryCount <= maxRetryAttempts) {
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                return; // 下载成功，退出循环
            } catch (Exception e) {
                retryCount++;
                if (retryCount > maxRetryAttempts) {
                    throw new Exception("单线程下载失败，超过最大重试次数", e);
                }
                Thread.sleep(retryDelayMs * retryCount); // 指数退避
            }
        }
    }

    private void downloadChunk(String bucketName, String objectName, long start, long end,
                               OutputStream outputStream, int maxRetryAttempts, long retryDelayMs) {
        int retryCount = 0;
        while (retryCount <= maxRetryAttempts) {
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .offset(start)
                            .length(end - start + 1)
                            .build())) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                return; // 下载成功，退出循环
            } catch (Exception e) {
                retryCount++;
                if (retryCount > maxRetryAttempts) {
                    throw new RuntimeException("下载分片失败，超过最大重试次数: " + start + "-" + end, e);
                }
                try {
                    Thread.sleep(retryDelayMs * retryCount); // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("下载分片被中断: " + start + "-" + end, ie);
                }
            }
        }
    }

    public static void main(String[] args) {
        String km1IIxJo6noJ2c6V = AesUtil.encrypt("QQwhl521@", "km1IIxJo6noJ2c6V");
        System.out.println(km1IIxJo6noJ2c6V);
        String km1IIxJo6noJ2c6V1 = AesUtil.decrypt(km1IIxJo6noJ2c6V);
        System.out.println(km1IIxJo6noJ2c6V1);
    }

}
