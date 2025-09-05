package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改密码入参
 */
@Data
@Schema(description = "修改密码入参实体")
public class UpdatePasswordDto {
    /**
     * 用户手机号
     */
    @Schema(description = "用户手机号")
    private String phone;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;
}
