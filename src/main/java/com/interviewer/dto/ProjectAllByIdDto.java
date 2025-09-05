package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "根据项目id查询所有项目信息（包含报告和结果）返回结构")
public class ProjectAllByIdDto implements Serializable {
    //标段/项目ID
    @Schema(description = "标段/项目id")
    private String fprojectSectionId;

    //标段/项目名称
    @Schema(description = "标段/项目 名称")
    private String fprojectSectionName;

    //报告信息(如果有关联的结果则展示)
    @Schema(description = "识别报告list")
    List<ReportAllDto> reportDtoList;

    //结果信息(无关联报告的岩性识别结果)
    @Schema(description = "结果信息List")
    private List<ReportResultAllDto> resultDtoList;
}
