package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "查看当前登录人下所有项目和标段返回结构")
public class UserProjectAllDTO {
    @Schema(description = "用户项目id")
    private String fid;
    @Schema(description = "标段/项目id")
    private String fprojectSectionId;
    @Schema(description = "父id")
    private String fparentProjectId;
    @Schema(description = "标段/项目 名称")
    private String fprojectSectionName;
    @Schema(description = "权限1管理员 2编辑  3查看")
    private Integer frole;
    private List<UserProjectAllDTO> children;
}
