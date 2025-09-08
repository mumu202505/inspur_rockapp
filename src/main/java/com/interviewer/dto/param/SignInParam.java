package com.interviewer.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "注册用户入参")
public class SignInParam implements Serializable {

    @Schema(description = "姓名")
    public String userName;

    @Schema(description = "学院")
    public String college;

    @Schema(description = "专业")
    public String major;

    @Schema(description = "班级")
    public String schoolClass;

    @Schema(description = "学号")
    public String studentNumber;

    @Schema(description = "邮箱")
    public String mail;

    @Schema(description = "手机号")
    public String phone;

    @Schema(description = "手机号验证码")
    public String code;

    @Schema(description = "密码")
    public String password;

}
