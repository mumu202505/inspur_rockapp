package com.rockapp.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "查询模型返回数据")
public class BaseRegionModelDto implements Serializable {
    /**
     * 模型id
     */
    private String fId;
    /**
     * 模型版本标识(如DeepSeek-R1)
     */
    private String fModelVersionIdentification;
    /**
     * 模型版本号(如v1.0)
     */
    private String fModelVersionNumber;
    /**
     * 模型描述
     */
    private String fModelDescribe;
    /**
     * 模型类型(1:通用 2:定制化)
     */
    private Integer fModelType;
    /**
     * 模型模式(1:岩性模型 2:多模态大模型)
     */
    private Integer fModelMode;
    /**
     * 模型部署侧(1:移动端 2:云端)
     */
    private Integer fModelDeploy;
    /**
     * 模型对应可识别岩性类别汇总
     */
    private String[] fModelClass;
    /**
     * 省
     */
    private String fProvinces;
    /**
     * 市
     */
    private String fCities;
    /**
     * 区/县
     */
    private String fDistrictsCounties;

}
