package com.interviewer.service;

import com.interviewer.dto.DistrictNode;

import java.util.List;

public interface UniversalityService {
    /**
     * 获取全国省市区列表
     * @return
     */
    List<DistrictNode> getbuildTree();
}
