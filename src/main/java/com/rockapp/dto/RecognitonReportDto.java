package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
@Data
@Schema(description = "筛选查询岩性识别报告")
public class RecognitonReportDto implements Serializable {
    @Schema(description = "是否查看本人结果：不传查看全部  1为查看自己")
    private Integer fIsAll;

    @Schema(description = "本人id")
    private String fUserId;

    @Schema(description = "项目/标段ID")
    private String fProjectSectionId;

    @Schema(description = "工程名称")
    private String fProjectSectionName;

    @Schema(description = "报告标题")
    private String fReportName;

    @Schema(description = "报告人姓名")
    private String fReportNikeName;

    @Schema(description = "开始日期")
    private String timeStart;

    @Schema(description = "结束日期")
    private String timeEnd;

}
