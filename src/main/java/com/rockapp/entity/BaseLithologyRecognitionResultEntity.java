package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 岩性识别结果表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Data
@TableName("base_lithology_recognition_result")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseLithologyRecognitionResultEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 岩性识别结果id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 岩性识别结果类型名称
	 */
	private String fClassName;
	/**
	 * 模型id
	 */
	private String fModelId;
	/**
	 * 岩性识别结果类型置信度
	 */
	private BigDecimal fClassConfidence;
	/**
	 * 拍摄场景
	 */
	private String fScene;
	/**
	 * 识别日期
	 */
	private String fIdentifyDate;
	/**
	 * 省
	 */
	private String fProvinces;
	/**
	 * 市
	 */
	private String fCities;
	/**
	 * 区/县
	 */
	private String fDistrictsCounties;
	/**
	 * 详细位置
	 */
	private String fDetailedLocation;
	/**
	 * 里程
	 */
	private String fMileageCode;
	/**
	 * 大里程
	 */
	private BigDecimal fMileageMax;
	/**
	 * 小里程
	 */
	private BigDecimal fMileageMin;
	/**
	 * 海拔
	 */
	private BigDecimal fElevation;
	/**
	 * 经度
	 */
	private BigDecimal fLongitude;
	/**
	 * 纬度
	 */
	private BigDecimal fLatitude;
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
	 * 修改时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date fLastModifyTime;
	/**
	 * 原图MinIO URL
	 */
	private String fImageUrl;
	/**
	 * 模型版本标识
	 */
	private String fModelVersion;
	/**
	 * 项目id
	 */
	private String fProjectSectionId;
	/**
	 * 标段id
	 */
	private String fSectionId;
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
