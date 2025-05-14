package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "移除/新增-项目成员入参")
public class DeleteUserProjectDto {
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private List<String> fUserId;
    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private String fId;
    /**
     * 分享人id
     */
    @Schema(description = "分享人id")
    private String fShareUserId;
}
