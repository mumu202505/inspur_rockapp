package com.rockapp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rockapp.dto.BaseVersionHistoryUplodDto;
import com.rockapp.dto.SaveDetailDto;
import com.rockapp.service.BaseIntelligentAnswerDetailService;
import com.rockapp.service.BasePromptQuestionService;
import com.rockapp.service.BaseVersionHistoryService;
import com.rockapp.utils.ResultUtil;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "智能问答", description = "智能问话相关操作") // 类级别分组
public class AiController {

    @Autowired
    BasePromptQuestionService basePromptQuestionService;

    @Autowired
    BaseIntelligentAnswerDetailService baseIntelligentAnswerDetailService;

    @Autowired
    BaseVersionHistoryService baseVersionHistoryService;

    @PostMapping("/getAll")
    @ResponseBody
    @Operation(summary = "获取智能问答为题列表")
    public ResultUtil getAllPromptQuestion(@RequestParam(value = "current", required = false) Integer current,
                                           @RequestParam(value = "size", required = false) Integer size) {
        return ResultUtil.success(basePromptQuestionService.getAllPromptQuestion(new Page<>(current == null ? 1 : current, size == null ? 5 : size)));
    }

    @PostMapping("/saveQuestion")
    @ResponseBody
    @Operation(summary = "新增问题")
    public ResultUtil saveQuestion(@RequestParam("current") String question) {
        basePromptQuestionService.saveQuestion(question);
        return ResultUtil.SUCCESS_NO_DATA;
    }


    @PostMapping("/saveDetail")
    @ResponseBody
    @Operation(summary = "保存问答对话")
    public ResultUtil saveDetail(@RequestBody SaveDetailDto saveDetailDto) {
        baseIntelligentAnswerDetailService.saveDetail(saveDetailDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/getAppVersion")
    @ResponseBody
    @Operation(summary = "查看最新版本信息1:apk 2:模型版本 3：ai问答模型")
    public ResultUtil getAppVersion(@RequestParam(value = "type",required = false) String type) {
        return ResultUtil.success(baseVersionHistoryService.getAppVersion(type));
    }

    @GetMapping("/getAllAiVersion")
    @ResponseBody
    @Operation(summary = "查看全部AI问答模型")
    public ResultUtil getAllAiVersion() {
        return ResultUtil.success(baseVersionHistoryService.getAllAiVersion());
    }

    @PostMapping(value = "/uploadApp",headers = "content-type=multipart/form-data")
    @ResponseBody
    @Operation(summary = "上传模型/Apk-1:apk 2:模型版本")
    public ResultUtil uploadApp(@RequestParam("file") MultipartFile file,
                                @RequestParam("1:apk 2:模型版本") Integer fVersionType,
                                @RequestParam("模型名称") String fVersionName,
                                @RequestParam("记录内容") String fVersionContenet,
                                @RequestParam("版本") String fVersionCode) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        BaseVersionHistoryUplodDto baseVersionHistoryUplodDto = new BaseVersionHistoryUplodDto();
        baseVersionHistoryUplodDto.setFVersionCode(fVersionCode);
        baseVersionHistoryUplodDto.setFVersionName(fVersionName);
        baseVersionHistoryUplodDto.setFVersionContenet(fVersionContenet);
        baseVersionHistoryUplodDto.setFVersionType(fVersionType);
        baseVersionHistoryService.uploadApp(file,baseVersionHistoryUplodDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

}
