package com.interviewer.controller;

import com.interviewer.dto.param.SaveJobParam;
import com.interviewer.entity.BaseJobEntity;
import com.interviewer.service.BaseJobService;
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
 * 岗位管理
 *
 * @Author liyy
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
@Tag(name = "岗位管理(需鉴权)", description = "岗位管理接口") // 类级别分组
public class JobController {

    @Autowired
    private BaseJobService baseJobService;

    @PostMapping("/save")
    @Operation(summary = "添加/删除岗位")
    public ResultUtil save(SaveJobParam saveJobParam) {
        BaseJobEntity baseJobEntity = CommonBeanUtils.dtoTransfer(saveJobParam, BaseJobEntity.class);
        baseJobService.saveOrUpdate(baseJobEntity);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/delete")
    @Operation(summary = "删除岗位")
    public ResultUtil delete(String id) {
        baseJobService.removeById(id);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/list")
    @Operation(summary = "查询岗位列表")
    public ResultUtil list() {
        List<BaseJobEntity> list = baseJobService.list();
        List<SaveJobParam> saveJobParams = CommonBeanUtils.dtoListTransfer(list, SaveJobParam.class);
        return ResultUtil.success(saveJobParams);
    }

    @PostMapping("/get")
    @Operation(summary = "查询岗位详情")
    public ResultUtil get(String id) {
        BaseJobEntity baseJobEntity = baseJobService.getById(id);
        SaveJobParam saveJobParam = CommonBeanUtils.dtoTransfer(baseJobEntity, SaveJobParam.class);
        return ResultUtil.success(saveJobParam);
    }


}
