package com.rockapp.dto;

import lombok.Data;

/**
 * 国家省市区实体类
 */
@Data
public class SysDistrictGd {
    private Long id;
    private String adCode;
    private String name;
    private String level;
    private String cityCode;
    private String parentCode;
    private String parentName;
    private String fullName;
    private String longitude;
    private String latitude;
}
