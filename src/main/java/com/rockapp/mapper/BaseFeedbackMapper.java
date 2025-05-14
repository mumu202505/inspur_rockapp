package com.rockapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rockapp.entity.BaseFeedbackEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户反馈表
 * 
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-04-28 10:30:45
 */
@Mapper
public interface BaseFeedbackMapper extends BaseMapper<BaseFeedbackEntity> {
	
}
