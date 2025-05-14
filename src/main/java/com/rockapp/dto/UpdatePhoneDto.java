package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "绑定手机号入参实体")
public class UpdatePhoneDto {
//    /**
//     * 用户id
//     */
//    @Schema(description = "用户id")
//    private String fId;
//    /**
//     * 用户账号
//     */
//    @Schema(description = "用户账号")
//    private String fName;
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
