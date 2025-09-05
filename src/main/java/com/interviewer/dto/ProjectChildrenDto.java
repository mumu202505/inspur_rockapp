package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "查询项目返回实体子项目")
public class ProjectChildrenDto implements Serializable {


    /**
     * 项目/标段ID
     */
    @Schema(description = "项目/标段ID")
    private String fParentProjectId;


    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String fProjectSectionName;
}
