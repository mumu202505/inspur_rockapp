package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 岩性识别报告表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Data
@TableName("base_lithology_recognition_report")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseLithologyRecognitionReportEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 岩性识别报告id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 岩性识别报告标题
	 */
	private String fReportName;
	/**
	 * 项目/标段id
	 */
	private String fProjectSectionId;
	/**
	 * 工程名称
	 */
	private String fProjectSectionName;
	/**
	 * 报告人名称
	 */
	private String fReportNikeName;
	/**
	 * 报告日期
	 */
	private String fReportDate;
	/**
	 * 创建人id
	 */
	@TableField(fill = FieldFill.INSERT)
	private String fCreateUserId;
	/**
	 * 创建人名称
	 */
	@TableField(fill = FieldFill.INSERT)
	private String fCreateUserName;
	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date fCreateTime;
	/**
	 * 修改人id
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private String fLastModifyUserId;
	/**
	 * 修改人名称
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private String fLastModifyUserName;
	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date fLastModifyTime;
	/**
	 * 报告内容格式
	 */
	private String fReportContentFormat;
	/**
	 * 关联岩性识别结果id
	 */
	private String fLithologyRecognitionResultId;
	/**
	 * 备注信息
	 */
	private String fRemarkInformation;
	/**
	 * 反馈信息
	 */
	private String fFeedbackInformation;
	/**
	 * 是否删除1删除0正常
	 */
	@TableLogic
	private Integer fIsDelete;

}
