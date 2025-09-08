package com.interviewer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewer.dto.DistrictNode;
import com.interviewer.service.GaoDeDistrictService;
import com.interviewer.service.UniversalityService;
import com.interviewer.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/universality")
@RequiredArgsConstructor
@Tag(name = "通用请求接口", description = "国家省市区") // 类级别分组
public class UniversalityController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${yxsb.url}")
    private String yxsbUrl;
    @Autowired
    UniversalityService universalityService;
    @Autowired
    GaoDeDistrictService gaoDeDistrictService;

    @PostMapping("/get")
    @ResponseBody
    @Operation(summary = "获取国家省市区列表")
    public ResultUtil get() {
        List<DistrictNode> districtNodes = universalityService.getbuildTree();
        return ResultUtil.success(districtNodes);
    }

    @PostMapping("/update")
    @ResponseBody
    @Operation(summary = "刷新国家省市区列表")
    public ResultUtil update() throws Exception {
        gaoDeDistrictService.getGaoDeDistrict();
        return ResultUtil.SUCCESS_NO_DATA;
    }
}
