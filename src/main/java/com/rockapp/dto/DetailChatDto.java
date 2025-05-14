package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "会话内容入参")
public class DetailChatDto implements Serializable {
    @Schema(description = "角色(1:user 2:assistant 3:system)")
    private Integer fRole;

    @Schema(description = "会话内容")
    private String fContent;

    @Schema(description = "0无 1点赞 2点踩")
    private Integer status;
}
