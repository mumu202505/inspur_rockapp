package com.interviewer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 岗位表
 * 
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-09-09 10:25:47
 */
@Data
@TableName("base_job")
public class BaseJobEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_UUID)
	private String id;
	/**
	 * 岗位名称
	 */
	@TableField(value = "f_job_name")
	private String jobName;
	/**
	 * 岗位类型
	 */
	@TableField(value = "f_job_type")
	private String jobType;
	/**
	 * 岗位要求
	 */
	@TableField(value = "f_job_require")
	private String jobRequire;
	/**
	 * 公司名称
	 */
	@TableField(value = "f_company_name")
	private String companyName;
	/**
	 * 公司简介
	 */
	@TableField(value = "f_company_synopsis")
	private String companySynopsis;
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
