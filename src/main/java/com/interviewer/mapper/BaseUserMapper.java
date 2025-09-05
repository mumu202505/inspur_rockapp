package com.interviewer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.interviewer.entity.BaseUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Mapper
public interface BaseUserMapper extends BaseMapper<BaseUserEntity> {
	
}
