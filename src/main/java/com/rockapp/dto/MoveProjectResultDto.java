package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "移动岩性识别结果/报告入参")
public class MoveProjectResultDto implements Serializable {
    //识别结果id
    @Schema(description = "识别结果/识别报告id")
    private String fId;

    //当前项目id
//    @Schema(description = "当前项目id")
//    private String fCurrentProjectId;

    //移动项目id
    @Schema(description = "目标项目id")
    private String fTargetProjectId;

    //当前标段id
//    @Schema(description = "当前标段id")
//    private String fCurrentSectionId;

    //移动标段id
    @Schema(description = "目标标段id")
    private String fTargetSectionId;
}
