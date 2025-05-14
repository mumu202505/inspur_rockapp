package com.rockapp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public interface BaseEnum {
    @JsonValue
    Integer getCode();

    static <T extends Enum<?> & BaseEnum> T codeOf(Class<T> enumClass, Integer code) {
        T[] enumConstants = enumClass.getEnumConstants();
        for (T t : enumConstants) {
            if (t.getCode() == code) {
                return t;
            }
        }
        return null;
    }
}
