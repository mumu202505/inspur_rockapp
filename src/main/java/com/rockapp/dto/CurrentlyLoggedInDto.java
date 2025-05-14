package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "获取当前登录用户返回数据")
public class CurrentlyLoggedInDto {
    @Schema(description = "用户id")
    private String fId;

    @Schema(description = "用户名")
    private String fUserName;
    /**
     * 绑定手机号
       */
    @Schema(description = "绑定手机号")
    private String fPhone;
    /**
     * 头像MinIO URL
     */
    @Schema(description = "头像MinIO URL")
    private String fHeadPortrait;
    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String fNickname;
    /**
     * 自动云端上传标志(1:开启 2:关闭)
       */
    @Schema(description = "自动云端上传标志(1:开启 2:关闭)")
    private Integer fAutomaticCloudUpload;
    /**
     * 角色(1:普通用户 2:管理员)
       */
    @Schema(description = " 角色(1:普通用户 2:管理员)")
    private Integer fRole;
    /**
     * 省
     */
    @Schema(description = "省")
    private String fProvinces;
    /**
     * 市
     */
    @Schema(description = "市")
    private String fCities;
    /**
     * 区/县
     */
    @Schema(description = "区/县")
    private String fDistrictsCounties;
    /**
     * 模型标签配置
     */
    @Schema(description = "模型标签配置")
    private String fModelLabelConfig;
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
