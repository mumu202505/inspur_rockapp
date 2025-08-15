package com.rockapp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.BasePromptQuestionDto;
import com.rockapp.entity.BasePromptQuestionEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 多模态大模型对话提示问题表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
public interface BasePromptQuestionService extends IService<BasePromptQuestionEntity> {


    /**
     * 获取问题列表
     * @param page
     * @return
     */
    Page<BasePromptQuestionDto> getAllPromptQuestion(Page page);

    /**
     * 新增问题
     * @param question
     */
    void saveQuestion(@RequestParam("current") String question);
}

