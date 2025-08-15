package com.rockapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rockapp.dto.BasePromptQuestionDto;
import com.rockapp.entity.BasePromptQuestionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 多模态大模型对话提示问题表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Mapper
public interface BasePromptQuestionMapper extends BaseMapper<BasePromptQuestionEntity> {

    /**
     * 查询问题列表
     * @param page
     * @return
     */
    Page<BasePromptQuestionDto> getAllPromptQuestion(@Param("page") Page page);
	
}
