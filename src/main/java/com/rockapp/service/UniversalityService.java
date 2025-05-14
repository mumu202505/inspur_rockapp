package com.rockapp.service;

import com.rockapp.dto.DistrictNode;

import java.util.List;

public interface UniversalityService {
    /**
     * 获取全国省市区列表
     * @return
     */
    List<DistrictNode> getbuildTree();

    /**
     * 发送短信
     */
    void senTextMessage(String phoen);
}
