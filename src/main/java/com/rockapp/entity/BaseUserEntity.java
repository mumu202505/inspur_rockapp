package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockapp.enums.status.EnabledEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Data
@TableName("base_user")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 用户名
	 */
	private String fUserName;
	/**
	 * 密码
	 */
	private String fPassword;
	/**
	 * 绑定手机号
	 */
	private String fPhone;
	/**
	 * 头像MinIO URL
	 */
	private String fHeadPortrait;
	/**
	 * 昵称
	 */
	private String fNickname;
	/**
	 * 自动云端上传标志(1:开启 2:关闭)
	 */
	private Integer fAutomaticCloudUpload;
	/**
	 * 角色(1:普通用户 2:管理员)
	 */
	private Integer fRole;
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
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date fCreateTime;
	/**
	 * 修改人id
	 */
	@TableField(fill = FieldFill.UPDATE)
	private String fLastModifyUserId;
	/**
	 * 修改人名称
	 */
	@TableField(fill = FieldFill.UPDATE)
	private String fLastModifyUserName;
	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private Date fLastModifyTime;
	/**
	 * Md5密码盐
	 */
	private String fSalt;
	/**
	 * 模型标签配置
	 */
	private String fModelLabelConfig;
	/**
	 * 是否启用 0启用 1禁用
	 */
	private Integer fIsEnabled;

}
