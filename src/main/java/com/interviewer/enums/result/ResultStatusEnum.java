package com.interviewer.enums.result;


import com.interviewer.core.IResultStatus;
import lombok.Getter;

@Getter
public enum ResultStatusEnum implements IResultStatus {
    SUCCESS(200, "操作成功"),
    ERROR_ARG(103, "参数错误"),
    ERROR_SERVICE(500, "服务器异常，请稍后重试..."),
    VALIDATE_ERROR(400, "数据校验错误"),
    UNAUTHORIZED(401, "认证失败"),
    FORBIDDEN(403, "无权限访问"),
    ISROLE(200, "权限通过"),
    ERROR_DELETE(501, "删除失败");

    private Integer code;
    private String msg;

    ResultStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
