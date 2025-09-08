package com.interviewer.controller;

import com.interviewer.dto.param.AccountLoginParam;
import com.interviewer.dto.param.SignInParam;
import com.interviewer.dto.param.UpdatePasswordParam;
import com.interviewer.dto.param.UserPassWordParam;
import com.interviewer.service.BaseUserService;
import com.interviewer.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author liyy
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关操作") // 类级别分组
public class PublicController {
    @Autowired
    private BaseUserService baseUserService;


    @PostMapping("/login")
    @ResponseBody
    @Operation(summary = "用户登录")
    public ResultUtil login(@RequestBody AccountLoginParam account, HttpServletRequest httpServletRequest) {
        return ResultUtil.success(baseUserService.login(account,httpServletRequest));
    }


    @PostMapping("/sign")
    @ResponseBody
    @Operation(summary = "用户注册")
    public ResultUtil sign(@RequestBody SignInParam signInParam) {
        baseUserService.sign(signInParam);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/getBackPsd")
    @ResponseBody
    @Operation(summary = "找回密码/根据验证码修改密码")
    public ResultUtil getBackPsd(
            @RequestBody UpdatePasswordParam updatePasswordParam) {
        baseUserService.getBackPsd(updatePasswordParam);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/updatePassWord")
    @ResponseBody
    @Operation(summary = "根据原密码修改密码")
    public ResultUtil updatePassWord(@RequestBody UserPassWordParam userPassWordParam) {
        baseUserService.updatepsd(userPassWordParam);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @GetMapping("/senTextMessage")
    @ResponseBody
    @Operation(summary = "发送短信")
    public ResultUtil senTextMessage(@RequestParam("phone")String phone) {
        baseUserService.senTextMessage(phone);
        return ResultUtil.success("验证码已发送!");
    }


}
