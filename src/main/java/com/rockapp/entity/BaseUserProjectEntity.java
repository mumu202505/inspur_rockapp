package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户项目表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Data
@TableName("base_user_project")
public class BaseUserProjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户项目id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 项目/标段id
	 */
	private String fProjectSectionId;
	/**
	 * 父项目id
	 */
	private String fParentProjectId;
	/**
	 * 项目/标段名称
	 */
	private String fProjectSectionName;
	/**
	 * 用户id
	 */
	private String fUserId;
	/**
	 * 角色(1:管理员权限 2:编辑权限 3:查看权限)
	 */
	private Integer fRole;
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
	 * 是否删除1删除0正常
	 */
	@TableLogic
	private Integer fIsDelete;

}
