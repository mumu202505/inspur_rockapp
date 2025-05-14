package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "岩性识别报告实体")
public class ReportAllDto implements Serializable {
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
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String fProjectName;
    /**
     * 标段名称
     */
    @Schema(description = "标段名称")
    private String fSectionName;
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

    /**
     * 反馈
     */
    @Schema(description = "反馈")
    private String fFeedbackInformation;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String fRemarkInformation;


    /**
     * 关联岩性识别结果
     */
    @Schema(description = "关联岩性识别结果List")
//    private List<BaseLithologyRecognitionResultDto> resultDtoList;
    private List<ReportResultAllDto> resultDtoList;
}
