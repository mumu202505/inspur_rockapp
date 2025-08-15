package com.rockapp.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockapp.core.exception.ServiceException;
import com.rockapp.dto.BaseAgreementDto;
import com.rockapp.dto.DistrictNode;
import com.rockapp.entity.BaseAgreementEntity;
import com.rockapp.entity.BaseSyncLithologyKnowledgeEntity;
import com.rockapp.entity.BaseVersionHistoryEntity;
import com.rockapp.service.*;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.HttpRequestUtil;
import com.rockapp.utils.OpenAiUtils;
import com.rockapp.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    BaseSyncLithologyKnowledgeService baseSyncLithologyKnowledgeService;
    @Autowired
    GaoDeDistrictService gaoDeDistrictService;
    @Autowired
    BaseAgreementService baseAgreementService;
    @Autowired
    BaseVersionHistoryService baseVersionHistoryService;
    @Autowired
    LithologyKnowledgeConverter lithologyKnowledgeConverter;

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

    @GetMapping("/getSyncLithologyKnowledge")
    @Operation(summary = "获取岩性知识库列表")
    public ResultUtil getSyncLithologyKnowledge(@RequestParam("nameList") List<String> nameList) {
        ArrayList<JSONArray> objects1 = new ArrayList<>();
        List<BaseSyncLithologyKnowledgeEntity> list = baseSyncLithologyKnowledgeService.list(Wrappers.<BaseSyncLithologyKnowledgeEntity>lambdaQuery()
                .in(BaseSyncLithologyKnowledgeEntity::getFClassName, nameList));
        list.forEach(item -> {
            JSONArray objects = lithologyKnowledgeConverter.convertToStructuredFormat(item);
            objects1.add(objects);
        });

        return ResultUtil.success(objects1);
    }

    @GetMapping("/getAgreement")
    @ResponseBody
    @Operation(summary = "查看协议")
    public ResultUtil getAgreement(@RequestParam("type") String type) {
        BaseAgreementEntity agreement = baseAgreementService.getOne(Wrappers.<BaseAgreementEntity>lambdaQuery()
                .eq(BaseAgreementEntity::getFType, type));
        BaseAgreementDto baseAgreementDto = CommonBeanUtils.dtoTransfer(agreement, BaseAgreementDto.class);
        return ResultUtil.success(baseAgreementDto);
    }

    @GetMapping("/getAppVersion")
    @ResponseBody
    @Operation(summary = "查看版本信息1:apk 2:岩性识别")
    public ResultUtil getAppVersion(@RequestParam("type") Integer type,
                                    @RequestParam(value = "current", required = false) Integer current,
                                  @RequestParam(value = "size", required = false) Integer size) {
        Page<BaseVersionHistoryEntity> objectPage = new Page<>(current == null ? 1 : current, size == null ? 10 : size );
        Page<BaseVersionHistoryEntity> page = baseVersionHistoryService.page(objectPage, Wrappers.<BaseVersionHistoryEntity>lambdaQuery()
                .eq(BaseVersionHistoryEntity::getFVersionType, type)
                .orderByDesc(BaseVersionHistoryEntity::getFVersionCode));
        return ResultUtil.success(page);
    }


    @Operation(summary = "岩性识别接口")
    @PostMapping(value = "/yxsb")
    public ResultUtil yxsb(@RequestBody String base64) throws IOException {
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("image", base64);
        String baseStr = objectMapper.writeValueAsString(bodys);
        try {
            String request = new HttpRequestUtil().doPostRequest(yxsbUrl, baseStr, null);
            Map<String, Object> stringObjectMap = objectMapper.readValue(request, new TypeReference<Map<String, Object>>() {
            });
            return ResultUtil.success(stringObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("调用失败");
        }
    }

    @Operation(summary = "转换word文件为pdf")
    @PostMapping(value = "/convert", produces = MediaType.APPLICATION_PDF_VALUE,headers = "content-type=multipart/form-data")
    public void convertWordToPdf(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
         baseVersionHistoryService.convertWordToPdf(file,response);
    }



    @Autowired
    private OpenAiUtils openAiUtils;

    @Operation(summary = "获取openAi返回值")
    @PostMapping("/auth/ai")
    public ResultUtil getAiRespondBody(@RequestBody String content) throws Exception {
        String respContent =openAiUtils.getAiRespondBody(content);
        return ResultUtil.success(respContent);
    }


}
