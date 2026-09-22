package com.community.front.config;

import com.community.common.exception.BusinessException;
import com.community.common.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 前台全局异常处理器：统一将异常转换为失败响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：返回业务提示信息
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        return Result.error("40001", e.getMessage());
    }

    /**
     * 兜底异常：避免异常堆栈直接暴露给前端
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        e.printStackTrace();
        return Result.error("50000", "系统异常：" + e.getMessage());
    }
}
