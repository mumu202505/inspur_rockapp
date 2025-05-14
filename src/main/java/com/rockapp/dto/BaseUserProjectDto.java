package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "根据项目id返回标段信息")
public class BaseUserProjectDto implements Serializable {
    /**
     * 项目id
     */
    private String fId;
    /**
     * 父项目id
     */
    private String fParentProjectId;
    /**
     * 项目/标段id
     */
    private String fProjectSectionId;
    /**
     * 项目/标段名称
     */
    private String fProjectSectionName;
}
