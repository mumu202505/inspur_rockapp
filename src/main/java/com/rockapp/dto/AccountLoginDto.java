package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录入参")
public class AccountLoginDto {
    @Schema(description = "用户名")
    public String fUserName;
    @Schema(description = "密码")
    public String fPassword;
}
