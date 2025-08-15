package com.rockapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rockapp.dto.*;
import com.rockapp.entity.BaseUserEntity;
import com.rockapp.vo.TokenVO;
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
    void insertUser(AccountDto account);

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
     * 用户登出
     * @return
     */
    void logout();

    /**
     * 用户注册
     * @param account
     */
    void sign(AccountDto account);

    /**
     * 根据手机验证码修改密码
     * @param updatePasswordDto
     */
    void getBackPsd(UpdatePasswordDto updatePasswordDto);

}

