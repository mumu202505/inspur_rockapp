package com.rockapp.enums.status;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.rockapp.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除状态枚举
 *
 * @author liyy
 */
@Getter
@AllArgsConstructor
public enum DeletedEnum implements BaseEnum {
    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private Integer code;
    private String text;

    @JsonCreator
    public static DeletedEnum getByCode(Integer code) {
        return BaseEnum.codeOf(DeletedEnum.class, code);
    }
}
