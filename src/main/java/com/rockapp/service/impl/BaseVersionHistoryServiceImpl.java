package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rockapp.dto.BaseVersionHistoryUplodDto;
import com.rockapp.entity.BaseVersionHistoryEntity;
import com.rockapp.mapper.BaseVersionHistoryMapper;
import com.rockapp.service.BaseVersionHistoryService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.MinIOUtils;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service("baseVersionHistoryService")
public class BaseVersionHistoryServiceImpl extends ServiceImpl<BaseVersionHistoryMapper, BaseVersionHistoryEntity> implements BaseVersionHistoryService {

    @Autowired
    BaseVersionHistoryMapper baseVersionHistoryMapper;

    @Autowired
    private MinIOUtils minioUtil;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.url}")
    private String minIoUrl;

    @Override
    public BaseVersionHistoryEntity getAppVersion(String type) {
        BaseVersionHistoryEntity baseVersionHistoryEntity = baseVersionHistoryMapper.selectOne(Wrappers.<BaseVersionHistoryEntity>lambdaQuery()
                .eq(BaseVersionHistoryEntity::getFVersionType, type)
                .orderByDesc(BaseVersionHistoryEntity::getFVersionCode) // 按字段降序
                .last("LIMIT 1"));
        return baseVersionHistoryEntity;
    }

    @Override
    public List<BaseVersionHistoryEntity> getAllAiVersion() {
        List<BaseVersionHistoryEntity> baseVersionHistoryEntities = baseVersionHistoryMapper.selectList(Wrappers.<BaseVersionHistoryEntity>lambdaQuery()
                .eq(BaseVersionHistoryEntity::getFVersionType, 3));
        return baseVersionHistoryEntities;
    }

    @Override
    @Transactional
    @Async
    public void uploadApp(MultipartFile file, BaseVersionHistoryUplodDto versionHistoryUplodDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        try {
            String objectName = minioUtil.upload(file);
            if (null != objectName) {
                String url = minIoUrl + "/" + bucketName + "/" + objectName;
                BaseVersionHistoryEntity baseVersionHistoryEntity = CommonBeanUtils.dtoTransfer(versionHistoryUplodDto, BaseVersionHistoryEntity.class);
                baseVersionHistoryEntity.setFVersionUrl(url);
                baseVersionHistoryMapper.insert(baseVersionHistoryEntity);
            }else {
                throw new SecurityException("上传失败");
            }
//        } catch (Exception e) {
//            throw new SecurityException("上传失败");
//        }
    }
}