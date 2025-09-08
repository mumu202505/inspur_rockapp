package com.interviewer.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserPassWordParam implements Serializable {


    @Schema(description = "原密码")
    private String oldPassWord;

    @Schema(description = "新密码")
    private String passWord;
}
