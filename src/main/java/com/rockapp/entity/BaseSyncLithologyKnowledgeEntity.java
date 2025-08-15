package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 岩性知识库表
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2025-04-21 09:24:53
 */
@Data
@TableName("base_sync_lithology_knowledge")
public class BaseSyncLithologyKnowledgeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 岩性知识库id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 岩性类型
	 */
	private String fClassName;
	/**
	 * 元素矿物组成
	 */
	private String fElementalMinera;
	/**
	 * 结构构造
	 */
	private String fStructuralConst;
	/**
	 * 物理性质
	 */
	private String fPhysicalPropert;
	/**
	 * 成因分析
	 */
	private String fFactorialAnalys;
	/**
	 * 常见用途
	 */
	private String fCommonUse;
	/**
	 * 易混淆岩性
	 */
	private String fConfoundingLith;
	/**
	 * 参考文献
	 */
	private String fReferences;
	/**
	 * 特征
	 */
	private String fFeature;
	/**
	 * 成份
	 */
	private String fComponent;
	/**
	 * 存储图片路径
	 */
	private String fImagePaths;
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
