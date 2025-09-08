package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "绑定手机号入参实体")
public class UpdatePhoneDto {
    /**
     * 绑定手机号
     */
    @Schema(description = "绑定手机号")
    private String fPhone;
    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;
}
