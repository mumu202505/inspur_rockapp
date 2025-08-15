package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "智能问答问题列表返回格式")
public class BasePromptQuestionDto implements Serializable {

    @Schema(description = "提示问题id")
    private String fId;

    @Schema(description = "提示问题文本")
    private String fPromptContent;
}
