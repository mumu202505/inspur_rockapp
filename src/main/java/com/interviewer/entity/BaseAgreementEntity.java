package com.interviewer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 协议信息表
 * 
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-09-09 14:15:25
 */
@Data
@TableName("base_agreement")
public class BaseAgreementEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_UUID)
	private String id;
	/**
	 * 信息名称
	 */
	@TableField(value = "f_agreement_name")
	private String agreementName;
	/**
	 * 信息类型0帮助中心1关于我们2版本信息
	 */
	@TableField(value = "f_agreement_type")
	private Integer agreementType;
	/**
	 * 信息内容
	 */
	@TableField(value = "f_agreement_content")
	private String agreementContent;
	/**
	 * 创建人
	 */
	@TableField(value = "f_create_user_name",fill = FieldFill.INSERT)
	private String createUserName;
	/**
	 * 创建时间
	 */
	@TableField(value = "f_create_time",fill = FieldFill.INSERT)
	private Date createTime;
	/**
	 * 修改人
	 */
	@TableField(value = "f_last_modify_user_name",fill = FieldFill.INSERT_UPDATE)
	private String lastModifyUserName;
	/**
	 * 修改时间
	 */
	@TableField(value = "f_last_modify_time",fill = FieldFill.INSERT_UPDATE)
	private Date lastModifyTime;
	/**
	 * 是否删除
	 */
	@TableField("f_is_delete")
	@TableLogic
	private Integer isDelete;

}
