package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseUserPassWordDto implements Serializable {

//    @Schema(description = "用户id")
//    private String fId;
//
//    @Schema(description = "用户账号")
//    private String fUserName;

    @Schema(description = "原密码")
    private String fOldPassWord;

    @Schema(description = "新密码")
    private String fPassWord;
}
