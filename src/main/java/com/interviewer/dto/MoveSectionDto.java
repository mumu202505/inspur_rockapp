package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "移动标段入参Dto")
public class MoveSectionDto implements Serializable {
    @Schema(description = "标段id")
    private String fId;

    @Schema(description = "移动项目id")
    private String targetProjectId;

}
