package com.rockapp.enums.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.rockapp.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuTypeEnum implements BaseEnum {

    CATALOG(0, "目录"),
    MENU(1, "菜单"),
    BUTTON(1, "按钮");

    private Integer code;
    private String text;

    @JsonCreator
    public static MenuTypeEnum getByCode(Integer code) {
        return BaseEnum.codeOf(MenuTypeEnum.class, code);
    }
}
