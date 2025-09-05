package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "修改项目入参实体")
public class SaveUserProjectDto {

    /**
     * 项目id
     */
    @NotNull(message = "项目ID不能为空")
    @Schema(description = "项目id")
    private String fId;

    /**
     * 项目/标段名称
     */
    @NotNull(message = "项目名称不能为空")
    @Schema(description = "项目/标段名称")
    private String fProjectSectionName;
}
