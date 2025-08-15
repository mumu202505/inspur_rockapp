package com.rockapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.SaveDetailDto;
import com.rockapp.entity.BaseIntelligentAnswerDetailEntity;

/**
 * 多模态大模型对话消息表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
public interface BaseIntelligentAnswerDetailService extends IService<BaseIntelligentAnswerDetailEntity> {

    /**
     * 记录会话
     * @param saveDetailDto
     */
    void saveDetail(SaveDetailDto saveDetailDto);
}

