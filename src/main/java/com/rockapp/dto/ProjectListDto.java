package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "查询项目返回实体")
public class ProjectListDto implements Serializable {

    /**
     * 父项目ID
     */
    @Schema(description = "父项目ID")
    private String fParentProjectId;
    /**
     * 父项目名称
     */
    @Schema(description = "父项目名称")
    private String fProjectSectionName;

    @Schema(description = "查询项目返回实体子项目")
    private List<ProjectChildrenDto> children;

}
