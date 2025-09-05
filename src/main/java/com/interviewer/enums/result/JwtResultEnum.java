package com.interviewer.enums.result;


import com.interviewer.core.IResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtResultEnum implements IResultStatus {
    JWT_SIGN_ERROR(10101, "非法jwt签名"),
    // jwtToken解析失败
    JWT_TOKEN_ERROR(10102, "登录失效，请重新登录"),
    JWT_GET_CLAIM_ERROR(10103, "jwt获取内容信息失败"),
    JWT_GET_HEADER_ERROR(10104, "jwt获取头部信息失败"),
    JWT_GET_TOKEN_ERROR(10105, "获取token失败"),
    ;
    private Integer code;
    private String msg;
}
