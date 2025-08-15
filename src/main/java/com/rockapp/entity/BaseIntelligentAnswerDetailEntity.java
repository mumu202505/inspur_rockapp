package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 多模态大模型对话消息表
 *
 * @author liyy
 * @date 2025-04-15 10:29:39
 */
@Data
@TableName("base_intelligent_answer_detail")
public class BaseIntelligentAnswerDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 多模态大模型对话消息id
     */
    @TableId(value = "f_id", type = IdType.ASSIGN_ID)
    private String fId;
    /**
     * 多模态大模型对话记录id
     */
    private String fSessionLogId;
    /**
     * 角色(1:user 2:assistant 3:system)
     */
    private Integer fRole;
    /**
     * 文本内容
     */
    private String fContent;
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
    private Date fLastModifyTime;
    /**
     * 点赞(1:点赞 2:取消/未点赞)
     */
    private Integer fLike;

    /**
     * 点踩(1:点踩 2:取消/未点踩)
     */
    private Integer fNotLike;

    /**
     * 反馈信息
     */
    private String fFeedbackInformation;

    /**
     * 模型选型
     */
    private String fModelOption;

    /**
     * 模型名称
     */
    private String fModelName;

    /**
     * 是否删除1删除0正常
     */
    @TableLogic
    private Integer fIsDelete;

}
