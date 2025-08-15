package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@Schema(description = "岩性识别报告实体关联结果")
public class ReportResultAllDto implements Serializable {

    @Schema(description = "岩性名称")
    String fClassName;

    @Schema(description = "岩性识别结果")
    List<BaseLithologyRecognitionResultDto> resultDtoList;

    // 构造方法
    public ReportResultAllDto() {
    }

    public ReportResultAllDto(String fClassName, List<BaseLithologyRecognitionResultDto> resultDtoList) {
        this.fClassName = fClassName;
        this.resultDtoList = resultDtoList;
    }

    // Getter 和 Setter
    public String getFClassName() {
        return fClassName;
    }

    public void setFClassName(String fClassName) {
        this.fClassName = fClassName;
    }

    public List<BaseLithologyRecognitionResultDto> getResultDtoList() {
        return resultDtoList;
    }

    public void setResultDtoList(List<BaseLithologyRecognitionResultDto> resultDtoList) {
        this.resultDtoList = resultDtoList;
    }
}
