package com.interviewer.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "注册用户入参")
public class SignInParam implements Serializable {
    @Schema(description = "登录账号")
    public String userName;

    @Schema(description = "密码")
    public String password;

    @Schema(description = "姓名")
    public String name;

    @Schema(description = "学号")
    public String studentNumber;

    @Schema(description = "手机号")
    public String phone;

    @Schema(description = "邮箱")
    public String mail;

}
