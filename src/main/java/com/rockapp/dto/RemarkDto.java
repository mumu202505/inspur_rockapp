package com.rockapp.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加识别结果/报告-备注、反馈入参")
public class RemarkDto {
    /**
     * 岩性识别结果/报告id
     */
    @Schema(description = "岩性识别结果/报告id")
    private String fId;
    /**
     * 项目/标段id
     */
    @Schema(description = "项目/标段id")
    private String fProjectSectionId;
    /**
     * 备注信息
     */
    @Schema(description = "备注信息")
    private String fRemarkInformation;
    /**
     * 反馈信息
     */
    @Schema(description = "反馈信息")
    private String fFeedbackInformation;
}
