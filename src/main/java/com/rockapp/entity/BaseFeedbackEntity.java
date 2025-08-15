package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈表
 * 
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-04-28 10:30:45
 */
@Data
@TableName("base_feedback")
public class BaseFeedbackEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户返回记录id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 用户id
	 */
	private String fUserId;
	/**
	 * 用户昵称
	 */
	private String fNikename;
	/**
	 * app版本
	 */
	private String fAppVersion;
	/**
	 * 反馈信息
	 */
	private String fContent;
	/**
	 * 创建人id
	 */
	private String fCreateUserId;
	/**
	 * 创建人名称
	 */
	private String fCreateUserName;
	/**
	 * 创建时间
	 */
	private Date fCreateTime;
	/**
	 * 修改人id
	 */
	private String fLastModifyUserId;
	/**
	 * 修改人名称
	 */
	private String fLastModifyUserName;
	/**
	 * 修改时间
	 */
	private Date fLastModifyTime;
	/**
	 * 是否删除1删除0正常
	 */
	@TableLogic
	private Integer fIsDelete;

}
