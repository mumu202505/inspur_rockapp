package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * app版本记录表
 *
 * @author liyy
 * @email 32226691@qq.com
 * @date 2025-04-27 09:50:32
 */
@Data
@TableName("base_version_history")
public class BaseVersionHistoryEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * app版本记录id
     */
    @TableId(value = "f_id", type = IdType.ASSIGN_ID)
    private String fId;
    /**
     * 标题
     */
    private String fVersionTitle;
    /**
     * 版本号
     */
    private Integer fVersionCode;
    /**
     * 记录名称
     */
    private String fVersionName;
    /**
     * 记录内容
     */
    private String fVersionContenet;
    /**
     * 下载地址
     */
    private String fVersionUrl;
    /**
     * 1:apk 2:岩性识别模型 3.智能问答模型
     */
    private Integer fVersionType;
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
