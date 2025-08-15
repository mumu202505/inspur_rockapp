package com.rockapp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rockapp.core.exception.ServiceException;
import com.rockapp.dto.BasePromptQuestionDto;
import com.rockapp.entity.BasePromptQuestionEntity;
import com.rockapp.enums.result.SysResultEnum;
import com.rockapp.mapper.BasePromptQuestionMapper;
import com.rockapp.service.BasePromptQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("basePromptQuestionService")
@Transactional
public class BasePromptQuestionServiceImpl extends ServiceImpl<BasePromptQuestionMapper, BasePromptQuestionEntity> implements BasePromptQuestionService {

    @Autowired
    BasePromptQuestionMapper basePromptQuestionMapper;

    @Override
    public Page<BasePromptQuestionDto> getAllPromptQuestion(Page page) {
        return basePromptQuestionMapper.getAllPromptQuestion(page);
    }

    @Override
    public void saveQuestion(String question) {
        BasePromptQuestionEntity promptQuestion = basePromptQuestionMapper.selectOne(Wrappers.<BasePromptQuestionEntity>lambdaQuery()
                .eq(BasePromptQuestionEntity::getFPromptContent, question));
        if (ObjectUtils.isNotEmpty(promptQuestion)) {
            throw new ServiceException(SysResultEnum.PROMPT_QUESTION_NOT_SAVE);
        }
        BasePromptQuestionEntity basePromptQuestionEntity = new BasePromptQuestionEntity();
        basePromptQuestionEntity.setFPromptContent(question);
        basePromptQuestionMapper.insert(basePromptQuestionEntity);

    }
}