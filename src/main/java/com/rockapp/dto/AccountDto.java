package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "注册用户入参")
public class AccountDto {
    @Schema(description = "用户名")
    public String fUserName;
    @Schema(description = "密码")
    public String fPassword;
    @Schema(description = "昵称")
    public String fNickname;
}
