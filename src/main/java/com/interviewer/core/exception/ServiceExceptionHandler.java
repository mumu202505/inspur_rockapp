package com.interviewer.core.exception;

import com.interviewer.core.IResultStatus;
import com.interviewer.enums.result.ResultStatusEnum;
import com.interviewer.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ServiceExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResultUtil handle(Exception e) {
        log.error("##系统异常##", e);
        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            return ResultUtil.error(new IResultStatus() {
                @Override
                public Integer getCode() {
                    return serviceException.getCode();
                }

                @Override
                public String getMsg() {
                    return serviceException.getMessage();
                }
            });
        } else if (e instanceof IllegalArgumentException) {
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException) e;
            return ResultUtil.error(new IResultStatus() {
                @Override
                public Integer getCode() {
                    return ResultStatusEnum.VALIDATE_ERROR.getCode();
                }

                @Override
                public String getMsg() {
                    return illegalArgumentException.getMessage();
                }
            });
        } else {
            return ResultUtil.ERROR_SERVICE;
        }
    }

    // 数据校验异常
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResultUtil handleValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder builder = new StringBuilder();
        for (FieldError error : fieldErrors) {
            builder.append("字段" + error.getField() + error.getDefaultMessage());
            return ResultUtil.error(new IResultStatus() {
                @Override
                public Integer getCode() {
                    return ResultStatusEnum.VALIDATE_ERROR.getCode();
                }

                @Override
                public String getMsg() {
                    return builder.toString();
                }
            });
        }
        return ResultUtil.VALIDATE_ERROR;
//        return new ResultUtil(ResultStatusEnum.VALIDATE_ERROR, builder.toString());
    }
}
