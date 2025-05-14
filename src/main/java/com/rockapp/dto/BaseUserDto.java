package com.rockapp.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "修改用户信息入参")
public class BaseUserDto {

    @Schema(description = "用户id")
    private String fId;

    @Schema(description = "用户名")
    private String fUserName;

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
     * 自动云端上传标志(1:开启 2:关闭)
     */
    @Schema(description = "自动云端上传标志(1:开启 2:关闭)")
    private Integer fAutomaticCloudUpload;
}
