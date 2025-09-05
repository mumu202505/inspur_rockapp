package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "找回密码入参")
public class GerUserPassWordDto implements Serializable {
    @Schema(description = "用户id")
    private String fUserId;

    @Schema(description = "验证码")
    private String fCode;
}
