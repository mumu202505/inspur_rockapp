package com.rockapp.utils;

import com.rockapp.core.IResultStatus;
import com.rockapp.enums.result.ResultStatusEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings({"unchecked", "rawtypes"})
@Data
public class ResultUtil<T> implements Serializable {

    public static final ResultUtil SUCCESS_NO_DATA = new ResultUtil(ResultStatusEnum.SUCCESS, null);
    public static final ResultUtil ERROR_ARG = new ResultUtil(ResultStatusEnum.ERROR_ARG, null);
    public static final ResultUtil ERROR_SERVICE = new ResultUtil(ResultStatusEnum.ERROR_SERVICE, null);
    public static final ResultUtil VALIDATE_ERROR = new ResultUtil(ResultStatusEnum.VALIDATE_ERROR, null);
    public static final ResultUtil ISROLE = new ResultUtil(ResultStatusEnum.ISROLE, null);

    private Integer code;
    private String msg;
    private T data;

    public ResultUtil() {

    }

    public ResultUtil(IResultStatus resultStatusVO, T data) {
        this.code = resultStatusVO.getCode();
        this.msg = resultStatusVO.getMsg();
        this.data = data;
//        this.data = data != null ? data : (T) Collections.emptyMap(); // 返回空Map {}
    }

    public static <T> ResultUtil<T> success(T data) {
        return new ResultUtil<>(ResultStatusEnum.SUCCESS, data);
    }

    public static <T> ResultUtil<T> error(IResultStatus resultStatusVo) {
        return new ResultUtil<>(resultStatusVo, null);
    }
}