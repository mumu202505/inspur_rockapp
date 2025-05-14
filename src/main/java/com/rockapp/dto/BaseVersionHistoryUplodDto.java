package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "apk/模型版本上传入参")
public class BaseVersionHistoryUplodDto implements Serializable {
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
    /**
     * 下载地址
     */
    @Schema(description = "下载地址")
    private String fVersionUrl;
    /**
     * 1:apk版本 2:模型版本
     */
    @Schema(description = "1:apk版本 2:模型版本")
    private Integer fVersionType;
}
