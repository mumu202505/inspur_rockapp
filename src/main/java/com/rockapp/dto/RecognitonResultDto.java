package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "筛选查询岩性识别结果")
public class RecognitonResultDto implements Serializable {
    @Schema(description = "是否查看本人结果：不传查看全部  1为查看自己")
    private Integer fIsAll;

    @Schema(description = "userID")
    private String fUserId;

    @Schema(description = "项目ID")
    private String fProjectSectionId;

    @Schema(description = "标段ID")
    private String fSectionId;

//    @Schema(description = "地区")
//    private String result;

    @Schema(description = "省")
    private String fProvinces;

    @Schema(description = "市")
    private String fCities;

    @Schema(description = "区/县")
    private String fDistrictsCounties;

    @Schema(description = "详细位置")
    private String fDetailedLocation;

    @Schema(description = "开始日期")
    private String timeStart;

    @Schema(description = "结束日期")
    private String timeEnd;

    @Schema(description = "岩性类别")
    private String fClassName;

    @Schema(description = "拍摄场景")
    private String fScene;

    @Schema(description = "开始大里程")
    private double fMileageMaxStart;

    @Schema(description = "结束大里程")
    private double fMileageMaxEnd;

    @Schema(description = "开始小里程")
    private double fMileageMinStart;

    @Schema(description = "结束小里程")
    private double fMileageMinEnd;

    @Schema(description = "开始海拔")
    private String fElevationStart;

    @Schema(description = "结束海拔")
    private String fElevationEnd;

    @Schema(description = "开始经度")
    private String fLongitudeStart;

    @Schema(description = "结束经度")
    private String fLongitudeEnd;

    @Schema(description = "开始纬度")
    private String fLatitudeStart;

    @Schema(description = "结束纬度")
    private String fLatitudeEnd;

}
