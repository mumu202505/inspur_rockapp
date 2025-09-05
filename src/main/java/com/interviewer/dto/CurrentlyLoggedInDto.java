package com.interviewer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "获取当前登录用户返回数据")
public class CurrentlyLoggedInDto {
    @Schema(description = "用户id")
    private String id;

    @Schema(description = "用户名")
    private String userName;
    /**
     * 绑定手机号
       */
    @Schema(description = "绑定手机号")
    private String phone;
    /**
     * 头像MinIO URL
     */
    @Schema(description = "头像MinIO URL")
    private String headPortrait;
    /**
     * 昵称
     */
    @Schema(description = "名字")
    private String name;
    /**
     * 角色(1:普通用户 2:管理员)
       */
    @Schema(description = " 角色(1:普通用户 2:管理员)")
    private Integer role;
    /**
     * 时间
     */
    @Schema(description = "时间")
    private Date loginTime;
    /**
     * ip
     */
    @Schema(description = "ip地址")
    private String loginIp;
}
