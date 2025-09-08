package com.interviewer.controller;

import com.interviewer.dto.UpdatePhoneDto;
import com.interviewer.dto.param.BaseUserParam;
import com.interviewer.service.BaseUserService;
import com.interviewer.service.RedisService;
import com.interviewer.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 令牌环自动更新
 *
 * @Author liyy
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "用户管理(需鉴权)", description = "令牌刷新/用户信息获取/用户信息修改等") // 类级别分组
public class AuthController {
    private final RedisService redisService;
    @Autowired
    BaseUserService baseUserService;

    @PostMapping("/logout")
    @Operation(summary = "退出/注销用户")
    public ResultUtil logout() {
        baseUserService.logout();
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("getUserInfo")
    @ResponseBody
    @Operation(summary = "获取当前登录用户")
    public ResultUtil getUserInfo() {
        return ResultUtil.success(baseUserService.getUserInfo());
    }


    @PostMapping("/updateUser")
    @ResponseBody
    @Operation(summary = "修改用户基本信息")
    public ResultUtil updateUser(@RequestBody BaseUserParam baseUserParam) {
        baseUserService.updateUser(baseUserParam);
        return ResultUtil.SUCCESS_NO_DATA;
    }


    @PostMapping("/updatePhone")
    @ResponseBody
    @Operation(summary = "绑定用户手机号")
    public ResultUtil updatePhone(@RequestBody UpdatePhoneDto updatePhoneDto) {
        baseUserService.updatePhone(updatePhoneDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

}

