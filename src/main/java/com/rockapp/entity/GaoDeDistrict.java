package com.rockapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "base_district")
public class GaoDeDistrict implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//主键ID

    @TableField(value = "ad_code")
    private String adCode;//区域编码

    @TableField(value = "name")
    private String name;//名称

    @TableField(value = "level")
    private String level;//等级  国家(country) 省(province) 市(city) 区/县(district)

    @TableField(value = "city_code")
    private String cityCode;//城市代码

    @TableField(value = "parent_code")
    private String parentCode;//父类代码

    @TableField(value = "parent_name")
    private String parentName;//父类名称

    @TableField(value = "full_name")
    private String fullName;//全称

    @TableField(value = "longitude")
    private String longitude;//经度

    @TableField(value = "latitude")
    private String latitude;//纬度

    @TableField(value = "gmt_create")
    private Date gmtCreate;//创建时间

    @TableField(value = "gmt_modify")
    private Date gmtModify;//最后修改时间

    public GaoDeDistrict() {
    }

    @Override
    public String toString() {
        return "GaoDeDistrict{" +
                "id=" + id +
                ", adCode='" + adCode + '\'' +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", parentName='" + parentName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", gmtCreate='" + gmtCreate + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}
