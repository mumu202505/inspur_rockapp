package com.interviewer.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
@Data
@Schema(description = "保存/修改岗位参数")
public class SaveJobParam implements Serializable {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;
    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称")
    private String jobName;
    /**
     * 岗位类型
     */
    @Schema(description = "岗位类型0公共1自有")
    private String jobType;
    /**
     * 岗位要求
     */
    @Schema(description = "岗位要求")
    private String jobRequire;
    /**
     * 公司名称
     */
    @Schema(description = "公司名称")
    private String companyName;
    /**
     * 公司简介
     */
    @Schema(description = "公司简介")
    private String companySynopsis;
}
