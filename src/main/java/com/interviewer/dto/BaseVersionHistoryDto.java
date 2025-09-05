package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "查看版本信息返回实体类")
public class BaseVersionHistoryDto implements Serializable {
    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private String fVersionCode;
    /**
     * 记录名称
     */
    @Schema(description = "记录名称")
    private String fVersionName;
    /**
     * 记录内容
     */
    @Schema(description = "记录内容")
    private String fVersionContenet;
}
