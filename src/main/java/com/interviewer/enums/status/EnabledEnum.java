package com.interviewer.enums.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.interviewer.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 启用、禁用状态枚举
 *
 * @author liyy
 */
@Getter
@AllArgsConstructor
public enum EnabledEnum implements BaseEnum {
    ENABLE(0, "启用"),
    DISABLE(1, "禁用");

    private Integer code;
    private String text;

    @JsonCreator
    public static DeletedEnum getByCode(Integer code) {
        return BaseEnum.codeOf(DeletedEnum.class, code);
    }
}
