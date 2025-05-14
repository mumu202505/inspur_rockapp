package com.rockapp.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "岩性识别结果入参实体")
public class BaseLithologyRecognitionReportDto {

    @Schema(description = "报告id")
    private String fId;
    /**
     * 岩性识别报告标题
     */
    @Schema(description = "岩性识别报告标题")
    private String fReportName;
    /**
     * 工程名称
     */
    @Schema(description = "工程名称")
    private String fProjectSectionName;

    /**
     * 项目/标段id
     */
    @Schema(description = "项目/标段id")
    private String fProjectSectionId;
    /**
     * 报告人名称
     */
    @Schema(description = "报告人名称")
    private String fReportNikeName;
    /**
     * 报告日期
     */
    @Schema(description = "报告日期")
    private String fReportDate;
    /**
     * 报告内容格式
     */
    @Schema(description = "报告内容格式")
    private String fReportContentFormat;
    /**
     * 关联岩性识别结果id
     */
    @Schema(description = "关联岩性识别结果id,多个用逗号隔开")
    private String fLithologyRecognitionResultId;
}
