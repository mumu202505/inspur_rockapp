package com.rockapp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rockapp.entity.BaseVersionHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * app版本记录表
 * 
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-04-27 09:50:32
 */
@Mapper
public interface BaseVersionHistoryMapper extends BaseMapper<BaseVersionHistoryEntity> {
	
}
