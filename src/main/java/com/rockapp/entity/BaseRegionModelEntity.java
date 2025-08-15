package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 模型信息表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Data
@TableName("base_region_model")
public class BaseRegionModelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 模型id
	 */
	@TableId(value = "f_id", type = IdType.ASSIGN_ID)
	private String fId;
	/**
	 * 模型版本标识(如DeepSeek-R1)
	 */
	private String fModelVersionIdentification;
	/**
	 * 模型版本号(如v1.0)
	 */
	private String fModelVersionNumber;
	/**
	 * 模型描述
	 */
	private String fModelDescribe;
	/**
	 * 模型类型(1:通用 2:定制化)
	 */
	private Integer fModelType;
	/**
	 * 模型模式(1:岩性模型 2:多模态大模型)
	 */
	private Integer fModelMode;
	/**
	 * 模型部署侧(1:移动端 2:云端)
	 */
	private Integer fModelDeploy;
	/**
	 * 模型对应可识别岩性类别汇总(JSON格式)
	 */
	private String fModelClass;
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
