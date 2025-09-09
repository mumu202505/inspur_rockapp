package com.interviewer.controller;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.interviewer.dto.param.SaveAgreementParam;
import com.interviewer.entity.BaseAgreementEntity;
import com.interviewer.service.BaseAgreementService;
import com.interviewer.utils.CommonBeanUtils;
import com.interviewer.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 协议信息管理
 *
 * @Author liyy
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/agreement")
@RequiredArgsConstructor
@Tag(name = "协议信息管理(需鉴权)", description = "协议信息管理接口")
public class AgreementController {

    @Autowired
    private BaseAgreementService agreementService;

    @PostMapping("/save")
    @Operation(summary = "添加/删除协议")
    public ResultUtil save(SaveAgreementParam saveAgreementParam) {
        BaseAgreementEntity baseAgreementEntity = CommonBeanUtils.dtoTransfer(saveAgreementParam, BaseAgreementEntity.class);
        agreementService.saveOrUpdate(baseAgreementEntity);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/delete")
    @Operation(summary = "删除协议")
    public ResultUtil delete(String id) {
        agreementService.removeById(id);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/list")
    @Operation(summary = "获取协议列表")
    public ResultUtil list(Integer type) {
        if (ObjectUtils.isNotEmpty(type)) {
            List<BaseAgreementEntity> list = agreementService.list(Wrappers.<BaseAgreementEntity>query().lambda().eq(BaseAgreementEntity::getAgreementType, type));
            return ResultUtil.success(CommonBeanUtils.dtoListTransfer(list, SaveAgreementParam.class));
        } else {
            return ResultUtil.success(CommonBeanUtils.dtoListTransfer(agreementService.list(), SaveAgreementParam.class));
        }
    }


}
