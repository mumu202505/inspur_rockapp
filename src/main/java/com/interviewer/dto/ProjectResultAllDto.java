package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "查询项目/标段下所有岩性识别结果")
public class ProjectResultAllDto implements Serializable {

    @Schema(description = "识别日期")
    String fIdentifyDate;

    @Schema(description = "识别结果list")
    List<BaseLithologyRecognitionResultDto> resultDtoList;
}
