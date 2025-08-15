package com.rockapp.controller;

import com.rockapp.dto.BaseLithologyRecognitionReportDto;
import com.rockapp.dto.MoveProjectResultDto;
import com.rockapp.dto.RecognitonReportDto;
import com.rockapp.dto.RemarkDto;
import com.rockapp.mapper.BaseLithologyRecognitionResultMapper;
import com.rockapp.mapper.BaseUserMapper;
import com.rockapp.mapper.BaseUserProjectMapper;
import com.rockapp.service.BaseLithologyRecognitionReportService;
import com.rockapp.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 岩性识别报告
 *
 * @Author liyy
 * @PreAuthorize("hasAuthority('admin')")//只允许有admin角色的用户访问 hasAnyAuthority([auth1, auth2])
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/recognitionReport")
@RequiredArgsConstructor
@Tag(name = "岩性识别报告", description = "岩性识别报告相关操作") // 类级别分组
public class RecognitionReportController {
    @Autowired
    BaseLithologyRecognitionReportService recognitionReportService;
    @Autowired
    BaseLithologyRecognitionResultMapper baseLithologyRecognitionResultMapper;
    @Autowired
    private BaseUserMapper baseUserMapper;
    @Autowired
    private BaseUserProjectMapper baseUserProjectMapper;

    @PostMapping("/save")
    @ResponseBody
    @Operation(summary = "保存岩性识别报告")
    public ResultUtil saveRecognitionResult(
            @RequestBody BaseLithologyRecognitionReportDto recognitionReportDto) {
        //保存岩性识别结果
        recognitionReportService.saveRecognitionResult(recognitionReportDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/saveRemark")
    @ResponseBody
    @Operation(summary = "添加识别报告备注、反馈")
    public ResultUtil saveRemark(
            @RequestBody RemarkDto remarkDto) {
        recognitionReportService.saveRemark(remarkDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/getById")
    @ResponseBody
    @Operation(summary = "根据id获取报告详情")
    public ResultUtil getRole(@RequestParam("recognitionresultid") String recognitionReportid) {
        return ResultUtil.success(recognitionReportService.getReportById(recognitionReportid));
    }

    @PostMapping("/getProjectReport")
    @ResponseBody
    @Operation(summary = "查询项目/标段下所有报告")
    public ResultUtil getProjectReport(@RequestParam(value = "fId") String fId,
                                       @RequestParam(value = "current", required = false) Integer current,
                                       @RequestParam(value = "size", required = false) Integer size) {
        return ResultUtil.success(recognitionReportService.getProjectReport(fId, current, size));
    }

    @PostMapping("/moveProjectReport")
    @ResponseBody
    @Operation(summary = "移动识别报告")
    public ResultUtil moveProjectReport(@RequestBody MoveProjectResultDto moveProjectResultDto) {
        recognitionReportService.moveProjectReport(moveProjectResultDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/deleteProjectReport")
    @ResponseBody
    @Operation(summary = "删除识别报告")
    public ResultUtil deleteProjectReport(@RequestParam(value = "fId", required = true) String fId) {
        recognitionReportService.deleteProjectReport(fId);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/getReport")
    @ResponseBody
    @Operation(summary = "筛选查询识别报告")
    public ResultUtil getReport(@RequestBody RecognitonReportDto recognitonReportDto,
                                @RequestParam(value = "fId") String fId,
                                @RequestParam(value = "current", required = false) Integer current,
                                @RequestParam(value = "size", required = false) Integer size) {
        return ResultUtil.success(recognitionReportService.getProjectReportTest(recognitonReportDto,fId,current,size));
    }


}
