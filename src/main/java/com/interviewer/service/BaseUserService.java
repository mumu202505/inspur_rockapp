package com.interviewer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interviewer.dto.*;
import com.interviewer.dto.param.SignInParam;
import com.interviewer.entity.BaseUserEntity;
import com.interviewer.vo.TokenVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
public interface BaseUserService extends IService<BaseUserEntity> {

    /**
     * 用户注册
     */
    void insertUser(BaseUserEntity baseUserEntity);

    /**
     * 修改密码
     * @param baseUserPassWordDto
     * @return
     */
    void updatepsd(BaseUserPassWordDto baseUserPassWordDto);

    /**
     * 获取当前登录用户信息
     * @return
     */
    CurrentlyLoggedInDto getUserInfo();

    /**
     * 修改用户信息
     * @param baseUserDto
     */
    void updateUser(BaseUserDto baseUserDto);

    /**
     * 绑定手机号
     * @param updatePhoneDto
     */
    void updatePhone(UpdatePhoneDto updatePhoneDto);

    /**
     * 用户登录
     * @param account
     * @return
     */
    TokenVO login(AccountLoginDto account, HttpServletRequest httpServletRequest);

    /**
     * 用户注册
     * @param signInParam
     */
    void sign(SignInParam signInParam);

    /**
     * 根据手机验证码修改密码
     * @param updatePasswordDto
     */
    void getBackPsd(UpdatePasswordDto updatePasswordDto);

    /**
     * 发送短信
     * @param phone
     */
    void senTextMessage(String phone);

}

