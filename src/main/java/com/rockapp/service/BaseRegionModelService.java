package com.rockapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.BaseRegionModelDto;
import com.rockapp.dto.GetBaseRegionModelDto;
import com.rockapp.entity.BaseRegionModelEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 模型信息表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
public interface BaseRegionModelService extends IService<BaseRegionModelEntity> {

    /**
     * 根据地区获取模型（岩性模型）
     */
    List<BaseRegionModelDto> getBaseRegionModel();

    /**
     * 根据地区获取模型（多模态模型）
     */
    List<BaseRegionModelDto> getRegionModel();


    /**
     * 获取在线云端模型
     * @return
     */
    List<BaseRegionModelDto> getModel();

    /**
     * 获取全部岩性类别
     * @return
     */
    String[] getAllModel();

}

