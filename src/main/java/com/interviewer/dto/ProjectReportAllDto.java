package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "查询项目/标段下所有岩性识别报告")
public class ProjectReportAllDto implements Serializable {
    @Schema(description = "报告日期")
    String fReportDate;

    @Schema(description = "识别报告list")
    List<ReportAllDto> reportDtoList;
}
