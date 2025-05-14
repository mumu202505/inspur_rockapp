package com.rockapp.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "查看协议返回数据格式")
public class BaseAgreementDto implements Serializable {
    /**
     * 信息id
     */
    private String fId;
    /**
     * 协议类型
     */
    private String fType;
    /**
     * 协议内容
     */
    private String fContent;
    /**
     * 当前版本号
     */
    private String fCurrentVersion;
    /**
     * 最新版本号
     */
    private String fLastVersion;
}
