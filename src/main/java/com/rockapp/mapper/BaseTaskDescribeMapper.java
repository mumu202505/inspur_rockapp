package com.rockapp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rockapp.entity.BaseTaskDescribeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务描述表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Mapper
public interface BaseTaskDescribeMapper extends BaseMapper<BaseTaskDescribeEntity> {
	
}
