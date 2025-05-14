package com.rockapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
@Data
@Schema(description = "获取模型及模型对应可识别岩性入参")
public class GetBaseRegionModelDto implements Serializable {
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
}
