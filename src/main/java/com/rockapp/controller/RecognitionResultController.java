package com.rockapp.controller;

import com.rockapp.dto.*;
import com.rockapp.service.BaseLithologyRecognitionResultService;
import com.rockapp.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 岩性识别结果
 *
 * @Author liyy
 * @PreAuthorize("hasAuthority('admin')")//只允许有admin角色的用户访问 hasAnyAuthority([auth1, auth2])
 */
@Slf4j
@CrossOrigin
@RestController
//@PreAuthorize("hasAnyAuthority('2')")
@RequestMapping("/recognitionResult")
@RequiredArgsConstructor
@Tag(name = "岩性识别结果", description = "岩性识别结果相关操作")
public class RecognitionResultController {

    @Autowired
    BaseLithologyRecognitionResultService recognitionResultService;

    @PostMapping("/save")
    @ResponseBody
    @Operation(summary = "保存岩性识别结果")
    public ResultUtil saveRecognitionResult(@RequestBody BaseLithologyRecognitionResultDto recognitionResultDto) {
        return ResultUtil.success(recognitionResultService.saveRecognitionResult(recognitionResultDto));
    }

    @PostMapping("/saveRemark")
    @ResponseBody
    @Operation(summary = "添加识别结果备注、反馈")
    public ResultUtil saveRemark(@RequestBody RemarkDto remarkDto) {
        recognitionResultService.saveRemark(remarkDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/getProjectResult")
    @ResponseBody
    @Operation(summary = "查询项目/标段下所有结果")
    public ResultUtil getProjectResult(@RequestParam(value = "fId") String fId,
                                       @RequestParam(value = "current", required = false) Integer current,
                                       @RequestParam(value = "size", required = false) Integer size) {
        return ResultUtil.success(recognitionResultService.getProjectResult(fId, current, size));
    }

    @GetMapping("/getById")
    @ResponseBody
    @Operation(summary = "根据id获取识别结果详情")
    public ResultUtil getRole(@RequestParam("recognitionresultid") String recognitionresultid) {
        return ResultUtil.success(recognitionResultService.getResultById(recognitionresultid));
    }

    @PostMapping("/moveProjectResult")
    @ResponseBody
    @Operation(summary = "移动识别结果")
    public ResultUtil moveProjectResult(@RequestBody MoveProjectResultDto moveProjectResultDto) {
        recognitionResultService.moveProjectResult(moveProjectResultDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/deleteProjectResult")
    @ResponseBody
    @Operation(summary = "删除识别结果")
    public ResultUtil deleteProjectResult(@RequestParam(value = "fId", required = true) String fId) {
        recognitionResultService.deleteProjectResult(fId);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/getResult")
    @ResponseBody
    @Operation(summary = "筛选查询识别结果")
    public ResultUtil getResult(@RequestBody RecognitonResultDto recognitionresult,
                                @RequestParam(value = "fId") String fId,
                                @RequestParam(value = "current", required = false) Integer current,
                                @RequestParam(value = "size", required = false) Integer size) {
        return ResultUtil.success(recognitionResultService.getResultNew(recognitionresult, fId, current, size));
    }

}
