package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "修改用户权限入参实体")
public class UserProjectListDto implements Serializable {
    @Schema(description = "项目/标段id")
    private String fId;
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String fUserId;
    /**
     * 角色(1:管理员权限 2:编辑权限 3:查看权限)
     */
    @Schema(description = "角色(1:管理员权限 2:编辑权限 3:查看权限)")
    private Integer fRole;
    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String fUserName;
    /**
     * 用户昵称
     */
    @Schema(description = "昵称")
    private String fNickname;
    /**
     * 头像
     */
    @Schema(description = "头像")
    private String fHeadPortrait;
}
