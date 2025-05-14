package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "保存用户反馈入参")
public class BaseFeedbackDto implements Serializable {
    /**
     * app版本
     */
    @Schema(description = "app版本")
    private String fAppVersion;
    /**
     * 反馈信息
     */
    @Schema(description = "fContent")
    private String fContent;
}
