package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录入参")
public class AccountLoginDto {
    @Schema(description = "用户名/手机号/学号")
    public String userName;
    @Schema(description = "密码")
    public String password;
}
