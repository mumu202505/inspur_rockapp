package com.rockapp.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "岩性识别结果入参实体")
public class BaseLithologyRecognitionResultDto {
    /**
     * 识别结果ID
     */
    @Schema(description = "识别结果ID")
    private String fId;

    /**
     * 岩性识别结果类型名称
     */
    @Schema(description = "岩性识别结果类型名称")
    private String fClassName;
    /**
     * 拍摄场景
     */
    @Schema(description = "拍摄场景")
    private String fScene;
    /**
     * 识别日期
     */
    @Schema(description = "识别日期")
    private String fIdentifyDate;
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
     * 详细位置
     */
    @Schema(description = "详细位置")
    private String fDetailedLocation;
    /**
     * 里程
     */
    @Schema(description = "里程代码")
    private String fMileageCode;
    /**
     * 大里程
     */
    @Schema(description = "大里程")
    private BigDecimal fMileageMax;
    /**
     * 里程
     */
    @Schema(description = "小里程")
    private BigDecimal fMileageMin;
    /**
     * 海拔
     */
    @Schema(description = "海拔")
    private BigDecimal fElevation;
    /**
     * 经度
     */
    @Schema(description = "经度")
    private BigDecimal fLongitude;
    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private BigDecimal fLatitude;
    /**
     * 原图MinIO URL
     */
    @Schema(description = "原图MinIO URL")
    private String fImageUrl;
    /**
     * 模型版本标识
     */
    @Schema(description = "模型版本标识")
    private String fModelId;
    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private String fProjectSectionId;
    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String fProjectName;
    /**
     * 标段id
     */
    @Schema(description = "标段id")
    private String fSectionId;
    /**
     * 标段名称
     */
    @Schema(description = "标段名称")
    private String fSectionName;
    /**
     * 备注信息
     */
    @Schema(description = "备注信息")
    private String fRemarkInformation;
    /**
     * 反馈信息
     */
    @Schema(description = "反馈信息")
    private String fFeedbackInformation;
    /**
     * 岩性识别结果类型置信度
     */
    @Schema(description = "岩性识别结果类型置信度")
    private BigDecimal fClassConfidence;

}
