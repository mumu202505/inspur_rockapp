package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "新增智能问答入参")
public class SaveDetailDto implements Serializable {

    @Schema(description = "记录会话id")
    private String sessionId;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型选项")
    private String modelOption;

    @Schema(description = "会话内容")
    private List<DetailChatDto> chat;
}
