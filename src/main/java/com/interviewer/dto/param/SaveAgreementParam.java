package com.interviewer.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
@Data
@Schema(description = "保存协议参数")
public class SaveAgreementParam implements Serializable {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 信息名称
     */
    private String agreementName;
    /**
     * 信息类型0帮助中心1关于我们2版本信息
     */
    private Integer agreementType;
    /**
     * 信息内容
     */
    private String agreementContent;
}
