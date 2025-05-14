package com.rockapp.mapper;

import com.rockapp.dto.DistrictNode;
import com.rockapp.dto.SysDistrictGd;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UniversalityMapper {
    /**
     * 获取全国省市区列表
     * @return
     */
    List<SysDistrictGd> getbuildTree();
}
