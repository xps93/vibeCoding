package com.example.admin.exception;

/**
 * 业务异常 — 携带错误码，由GlobalExceptionHandler统一解析为多语言消息
 *
 * 使用方式：
 *   throw new BusinessException(1001);                           // 仅错误码
 *   throw new BusinessException(1001, "用户名不能为空");           // 覆盖默认消息
 *   throw new BusinessException(1001, "用户名不能为空", cause);    // 带原始异常
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code) {
        super();
        this.code = code;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
