package com.rockapp.enums.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.rockapp.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否类型枚举
 */
@Getter
@AllArgsConstructor
public enum YesOrNoEnum implements BaseEnum {
    YES(0, "否"),
    NO(1, "是");

    private Integer code;
    private String text;

    @JsonCreator
    public static YesOrNoEnum getByCode(Integer code) {
        return BaseEnum.codeOf(YesOrNoEnum.class, code);
    }
}
