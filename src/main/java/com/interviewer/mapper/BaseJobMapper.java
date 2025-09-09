package com.interviewer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.interviewer.entity.BaseJobEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位表
 * 
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-09-09 10:25:47
 */
@Mapper
public interface BaseJobMapper extends BaseMapper<BaseJobEntity> {
	
}
