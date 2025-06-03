package com.rockapp.controller;

import com.rockapp.core.exception.ServiceException;
import com.rockapp.dto.*;
import com.rockapp.enums.result.SysResultEnum;
import com.rockapp.service.BaseFeedbackService;
import com.rockapp.service.BaseRegionModelService;
import com.rockapp.service.BaseUserService;
import com.rockapp.service.RedisService;
import com.rockapp.utils.CommonBeanUtils;
import com.rockapp.utils.JwtUtils;
import com.rockapp.utils.ResultUtil;
import com.rockapp.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 令牌环自动更新
 *
 * @Author liyy
 * @PreAuthorize("hasAuthority('admin')")//只允许有admin角色的用户访问 hasAnyAuthority([auth1, auth2])
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
    @Autowired
    BaseFeedbackService baseFeedbackService;
    @Autowired
    BaseRegionModelService baseRegionModelService;

    /**
     * 更新令牌环信息
     *
     * @param request
     * @return
     */
    @GetMapping("refreshToken")
    @ResponseBody
    @Operation(summary = "令牌刷新")
    public ResultUtil refreshToken(HttpServletRequest request) {
        String role = null;
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority authority : authorities) {
            role = authority.getAuthority();

        }
        if (CommonController.isNullOrSpace(role)) {
            throw new ServiceException(SysResultEnum.WECHAT_ACCESS_TOKEN_FAIL);
        } else {
            String jwt = "";
            //一小时
//            jwt= JwtUtils.generateToken(role,userid,60*60*1000);
            CurrentlyLoggedInDto baseUserDto = CommonBeanUtils.dtoTransfer(UserUtil.getUser(), CurrentlyLoggedInDto.class);
            jwt = JwtUtils.generateTokenUser(baseUserDto, 60 * 60 * 1000);
            HashMap<String, String> m = new HashMap<>();
            m.put("access_token", jwt);
            return ResultUtil.success(m);
        }
    }


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
    public ResultUtil updateUser(@RequestBody BaseUserDto baseUserDto) {
        baseUserService.updateUser(baseUserDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/updatePassWord")
    @ResponseBody
    @Operation(summary = "根据原密码修改密码")
    public ResultUtil updatePassWord(@RequestBody BaseUserPassWordDto baseUserPassWordDto) {
        baseUserService.updatepsd(baseUserPassWordDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/updatePhone")
    @ResponseBody
    @Operation(summary = "绑定用户手机号")
    public ResultUtil updatePhone(@RequestBody UpdatePhoneDto updatePhoneDto) {
        baseUserService.updatePhone(updatePhoneDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/saveFeedback")
    @ResponseBody
    @Operation(summary = "保存用户反馈信息")
    public ResultUtil saveFeedback(@RequestBody BaseFeedbackDto baseFeedbackDto) {
        baseFeedbackService.saveFeedback(baseFeedbackDto);
        return ResultUtil.SUCCESS_NO_DATA;
    }

    @PostMapping("/getBaseRegionModel")
    @ResponseBody
    @Operation(summary = "获取模型")
    public ResultUtil getBaseRegionModel() {
        List<BaseRegionModelDto> baseRegionModel = baseRegionModelService.getBaseRegionModel();
        return ResultUtil.success(baseRegionModel);
    }

    @PostMapping("/getRegionModel")
    @ResponseBody
    @Operation(summary = "获取模型(多模态模型)")
    public ResultUtil getRegionModel() {
        List<BaseRegionModelDto> baseRegionModel = baseRegionModelService.getRegionModel();
        return ResultUtil.success(baseRegionModel);
    }

    @PostMapping("/getModel")
    @ResponseBody
    @Operation(summary = "获取在线云端模型")
    public ResultUtil getModel() {
        List<BaseRegionModelDto> baseRegionModel = baseRegionModelService.getModel();
        return ResultUtil.success(baseRegionModel);
    }

    @PostMapping("/getAllModel")
    @ResponseBody
    @Operation(summary = "获取全部岩性类别")
    public ResultUtil getAllModel() {
        String[] baseRegionModel = baseRegionModelService.getAllModel();
        return ResultUtil.success(baseRegionModel);
    }




}
