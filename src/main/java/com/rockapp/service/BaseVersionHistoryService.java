package com.rockapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.BaseVersionHistoryUplodDto;
import com.rockapp.entity.BaseVersionHistoryEntity;
import io.minio.errors.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * app版本记录表
 *
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-04-27 09:50:32
 */
public interface BaseVersionHistoryService extends IService<BaseVersionHistoryEntity> {

    /**
     * 查看最新APK或模型版本
     * @param type
     * @return
     */
    BaseVersionHistoryEntity getAppVersion(String type);

    /**
     * 查看全部AI问答模型
     * @return
     */
    List<BaseVersionHistoryEntity> getAllAiVersion();

    /**
     * 上传APK/模型
     * @param file
     */
    void uploadApp(MultipartFile file, BaseVersionHistoryUplodDto versionHistoryUplodDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}

