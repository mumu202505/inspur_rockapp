package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增项目入参
 */
@Data
@Schema(description = "新增项目入参实体")
public class UserProjectDto {

    /**
     * 项目/标段名称
     */
    @NotNull(message = "项目名称不能为空")
    @Schema(description = "项目/标段名称")
    private String fProjectSectionName;

    /**
     * 父项目id
     */
    @Schema(description = "父项目id")
    private String fParentProjectId;


}
