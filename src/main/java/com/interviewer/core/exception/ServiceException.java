package com.interviewer.core.exception;

import com.interviewer.core.IResultStatus;
import com.interviewer.enums.result.ResultStatusEnum;
import com.interviewer.utils.ResultUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {
    private Integer code;

    /**
     * 传入resultUtil，返回异常信息
     *
     * @param resultUtil
     */
    public ServiceException(ResultUtil resultUtil) {
        super(resultUtil.getMsg());
        this.code = resultUtil.getCode();
    }

    /**
     * 传入枚举错误信息，返回异常信息
     *
     * @param iResultStatusVO
     */
    public ServiceException(IResultStatus iResultStatusVO) {
        super(iResultStatusVO.getMsg());
        this.code = iResultStatusVO.getCode();
    }

    /**
     * 传入code, msg,返回异常信息
     *
     * @param code
     * @param msg
     */
    public ServiceException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public ServiceException(String msg) {
        super(msg);
        this.code = ResultStatusEnum.ERROR_SERVICE.getCode();
    }

}
