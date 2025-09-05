package com.interviewer.vo;

import com.interviewer.dto.CurrentlyLoggedInDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TokenVO {
    /**
     * 访问token
     */
    private String access_token;

    /**
     * access_token失效时间，单位秒（6小时）
     */
    private Integer expires_in;

    /**
     * 用户信息
     */
    private CurrentlyLoggedInDto user_info;

}
