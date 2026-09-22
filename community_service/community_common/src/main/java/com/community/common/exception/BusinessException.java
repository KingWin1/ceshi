package com.community.common.exception;

/**
 * 业务异常：Service 层业务校验不通过时抛出，由全局异常处理器统一转换为失败响应
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
