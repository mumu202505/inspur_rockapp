package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 多模态大模型对话提示问题表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Data
@TableName("base_prompt_question")
public class BasePromptQuestionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 提示问题id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 提示问题文本
	 */
	private String fPromptContent;
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
