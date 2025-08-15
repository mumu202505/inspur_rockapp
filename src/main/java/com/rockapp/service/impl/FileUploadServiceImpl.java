package com.rockapp.service.impl;


import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rockapp.core.exception.ServiceException;
import com.rockapp.entity.BaseFileUploadDetailEntity;
import com.rockapp.mapper.BaseFileUploadDetailMapper;
import com.rockapp.service.FileUploadService;
import com.rockapp.utils.MinIOUtils;
import com.rockapp.utils.ResultUtil;
import com.rockapp.utils.UserUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.rockapp.utils.MinIOUtils.formatTodayDate;

@Service
@Slf4j
@Transactional
public class FileUploadServiceImpl implements FileUploadService {


    @Autowired
    private BaseFileUploadDetailMapper fileUploadDetailMapper;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinIOUtils minioUtil;


    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * 上传文件块
     *
     * @param chunk       文件块
     * @param chunkNumber 当前块的编号
     * @param md5         文件的MD5值
     */
    @Override
    public String uploadChunk(MultipartFile chunk, int chunkNumber, String md5, String fileName, int totalchunks) throws Exception {
        //检查分片是否上传
        BaseFileUploadDetailEntity baseFileUploadDetailEntity = fileUploadDetailMapper.selectOne(Wrappers.<BaseFileUploadDetailEntity>query().lambda()
                .eq(BaseFileUploadDetailEntity::getFMd5, md5));
        if (ObjectUtils.isNotEmpty(baseFileUploadDetailEntity)) {
            String fHasBeenUploaded = baseFileUploadDetailEntity.getFHasBeenUploaded();
            if (fHasBeenUploaded.equals(chunkNumber + "")) {
                return "该分片已上传" + chunkNumber;
            }
        }
        // 将文件块上传到MinIO
        String chunkName = UserUtil.getUser().getFUserName() + "/" + md5 + "-" + chunkNumber;
        try (InputStream inputStream = chunk.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(chunkName)
                            .stream(inputStream, chunk.getSize(), -1)
                            .contentType(chunk.getContentType())
                            .build()
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // 更新数据库
        BaseFileUploadDetailEntity fileUploadDetail = fileUploadDetailMapper.selectOne(Wrappers.<BaseFileUploadDetailEntity>query().lambda()
                .eq(BaseFileUploadDetailEntity::getFMd5, md5));
        if (fileUploadDetail == null) {
            // 如果文件上传详细信息不存在，则创建新的记录
            fileUploadDetail = new BaseFileUploadDetailEntity();
            fileUploadDetail.setFUsername(UserUtil.getUser().getFUserName());
            fileUploadDetail.setFMd5(md5);
            fileUploadDetail.setFHasBeenUploaded(String.valueOf(chunkNumber));
            fileUploadDetail.setFTotalChunks(1);
            fileUploadDetailMapper.insert(fileUploadDetail);
        } else {
            // 如果文件上传详细信息已存在，则更新记录
            Set<Integer> uploadedChunks = new HashSet<>();
            if (fileUploadDetail.getFHasBeenUploaded() != null) {
                // 将已上传的块编号从字符串转换为集合
                uploadedChunks = Arrays.stream(fileUploadDetail.getFHasBeenUploaded().split(":"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());
            }
            // 添加当前已上传的块编号
            uploadedChunks.add(chunkNumber);

            // 更新已上传的块编号字符串
            String updatedChunks = uploadedChunks.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(":"));
            fileUploadDetail.setFHasBeenUploaded(updatedChunks);
            fileUploadDetail.setFTotalChunks(uploadedChunks.size());
            fileUploadDetailMapper.update(fileUploadDetail, Wrappers.<BaseFileUploadDetailEntity>query().lambda());
            if (fileUploadDetail.getFTotalChunks() == totalchunks) {
                return mergeChunks(md5, md5);
            }
        }
        return null;
    }


    /**
     * 根据MD5获取已上传的文件块编号
     *
     * @param md5 文件的MD5值
     * @return 已上传的文件块编号
     */
    @Override
    public Set<Integer> getUploadedChunks(String md5) {
        BaseFileUploadDetailEntity detail = fileUploadDetailMapper.selectOne(Wrappers.<BaseFileUploadDetailEntity>query().lambda()
                .eq(BaseFileUploadDetailEntity::getFMd5, md5));
        if (detail == null || detail.getFHasBeenUploaded() == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(detail.getFHasBeenUploaded().split(":"))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }


    /**
     * 合并文件块
     *
     * @param md5      文件的MD5值
     * @param fileName 文件名
     * @return 合并结果
     */
    @Override
    public String mergeChunks(String md5, String fileName) throws Exception {
        CopyOnWriteArrayList<ComposeSource> sourceObjectList = new CopyOnWriteArrayList<>();
        // 从数据库中获取文件上传详细信息
        BaseFileUploadDetailEntity fileUploadDetail = fileUploadDetailMapper.selectOne(Wrappers.<BaseFileUploadDetailEntity>query().lambda()
                .eq(BaseFileUploadDetailEntity::getFMd5, md5));
        if (fileUploadDetail == null) {
            throw new Exception("File not found");
        }
        // 获取所有已上传的块编号
        String[] chunkNames = fileUploadDetail.getFHasBeenUploaded().split(":");

        for (String chunkName : chunkNames) {
            try {
                // 验证资源是否存在,释放流,避免资源占用
                try (GetObjectResponse response = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(UserUtil.getUser().getFUserName() + "/" + md5 + "-" + chunkName)
                                .build());) {
                    // 添加到合并列表
                    sourceObjectList.add(ComposeSource.builder().bucket(bucketName).object(UserUtil.getUser().getFUserName() + "/" + md5 + "-" + chunkName).build());
                } catch (Exception e) {
                    // 抛出异常,后续处理
                    throw new ServiceException("Chunk not found");
                }
            } catch (Exception e) {
                // 处理MinIO资源不存在的情况
                try {
                    List<DeleteObject> deleteObjects = new ArrayList<>();
                    // 列出所有匹配前缀的文件并添加到删除列表
                    Iterable<Result<Item>> findMD5Files = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName)
                            .prefix(md5)
                            .recursive(true)
                            .build());
                    for (Result<Item> findMD5File : findMD5Files) {
                        Item item = findMD5File.get();
                        deleteObjects.add(new DeleteObject(item.objectName()));
                    }
                    deleteObjects.add(new DeleteObject(fileUploadDetail.getFileName()));
                    // 执行批量删除
                    Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                            RemoveObjectsArgs.builder()
                                    .bucket(bucketName)
                                    .objects(deleteObjects)
                                    .build()
                    );
                    for (Result<DeleteError> result : results) {
                        DeleteError error = result.get();
                        log.error("Error in deleting object {}; {}", error.objectName(), error.message());
                    }
                    // 从数据库中删除文件上传详细信息
                    fileUploadDetailMapper.delete(Wrappers.<BaseFileUploadDetailEntity>query().lambda().eq(BaseFileUploadDetailEntity::getFMd5, md5));
                } catch (Exception deleteError) {
                    log.error(deleteError.getMessage());
                }
                throw new ServiceException("操作失败，请重试！");
            }
        }

        // 合并文件块
        String objectName = formatTodayDate("yyyy-MM/dd") + "/" + fileName;
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .sources(sourceObjectList)
                        .build()
        );

        // 生成文件的预签名URL
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );

        // 更新数据库
        fileUploadDetail.setFIsUploaded(1);
        fileUploadDetail.setFUrl(url);
        fileUploadDetail.setFileName(fileName);
        fileUploadDetailMapper.update(fileUploadDetail, Wrappers.<BaseFileUploadDetailEntity>query().lambda());
        // todo 之前对应的md5文件的url变更

        return url;
    }

    @Operation(summary = "小文件上传返回url")
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public ResultUtil upload(@RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String objectName = minioUtil.upload(file);
        if (null != objectName) {
            return ResultUtil.success("http://172.31.128.115:8999" + "/" + bucketName + "/" + objectName);
        }
        return ResultUtil.ERROR_ARG;
    }


}

