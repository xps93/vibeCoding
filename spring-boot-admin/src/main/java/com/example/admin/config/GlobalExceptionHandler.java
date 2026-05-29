package com.example.admin.config;

import com.example.admin.entity.Result;
import com.example.admin.exception.BusinessException;
import com.example.admin.service.ErrorCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * 全局异常处理器 — 所有异常通过错误码表（Redis缓存）统一返回中英文消息
 *
 * 语言解析优先级：请求参数 ?lang=  → Accept-Language头 → 默认中文
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ErrorCodeService errorCodeService;

    // ════════════════════════════════════════════════════════════
    // 错误码常量（与DB sys_error_code表同步）
    // ════════════════════════════════════════════════════════════

    /** 未知错误 */
    public static final int ERR_UNKNOWN = 1;
    /** 参数校验失败 */
    public static final int ERR_BAD_REQUEST = 400;
    /** 未登录或token已过期 */
    public static final int ERR_UNAUTHORIZED = 401;
    /** 权限不足 */
    public static final int ERR_FORBIDDEN = 403;
    /** 资源不存在 */
    public static final int ERR_NOT_FOUND = 404;
    /** 请求方式不支持 */
    public static final int ERR_METHOD_NOT_ALLOWED = 405;
    /** 服务限流 */
    public static final int ERR_TOO_MANY_REQUESTS = 429;
    /** 服务器内部错误 */
    public static final int ERR_INTERNAL = 500;
    /** 数据库异常 */
    public static final int ERR_DATABASE = 501;
    /** 第三方服务异常 */
    public static final int ERR_THIRD_PARTY = 502;

    // ──────────────── 业务异常 ────────────────

    /** 业务异常 — 从错误码表解析多语言消息 */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e, HttpServletRequest request) {
        String lang = resolveLang(request);
        int code = e.getCode();
        String msg = e.getMessage();
        // 如果业务异常自带了消息（覆盖模式），优先使用；否则查表
        if (msg == null || msg.isEmpty()) {
            msg = errorCodeService.getMessage(code, lang);
        }
        return Result.error(code, msg);
    }

    // ──────────────── 参数校验 ────────────────

    /** 参数校验异常（@Valid） */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleBindException(BindException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String detail = fieldError != null ? fieldError.getDefaultMessage() : "";
        String fullMsg = errorCodeService.getMessage(ERR_BAD_REQUEST, resolveLang(request));
        if (detail != null && !detail.isEmpty()) {
            fullMsg = fullMsg + ": " + detail;
        }
        return Result.error(ERR_BAD_REQUEST, fullMsg);
    }

    /** 缺少请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest request) {
        String msg = errorCodeService.getMessage(ERR_BAD_REQUEST, resolveLang(request));
        return Result.error(ERR_BAD_REQUEST, msg + ": " + e.getParameterName());
    }

    /** 参数类型转换异常 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String msg = errorCodeService.getMessage(ERR_BAD_REQUEST, resolveLang(request));
        return Result.error(ERR_BAD_REQUEST, msg + ": " + e.getName());
    }

    // ──────────────── 权限 ────────────────

    /** 权限不足 */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return Result.error(ERR_FORBIDDEN, errorCodeService.getMessage(ERR_FORBIDDEN, resolveLang(request)));
    }

    // ──────────────── 数据库 ────────────────

    /** 数据库异常 */
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleDatabaseException(Exception e, HttpServletRequest request) {
        log.error("数据库异常 [{}] {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ERR_DATABASE, errorCodeService.getMessage(ERR_DATABASE, resolveLang(request)));
    }

    // ──────────────── 运行时异常 ────────────────

    /** 运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常 [{}] {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ERR_INTERNAL, errorCodeService.getMessage(ERR_INTERNAL, resolveLang(request)));
    }

    // ──────────────── 兜底 ────────────────

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Exception e, HttpServletRequest request) {
        log.error("未知异常 [{}] {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ERR_UNKNOWN, errorCodeService.getMessage(ERR_UNKNOWN, resolveLang(request)));
    }

    // ──────────────── 私有方法 ────────────────

    /**
     * 解析请求语言：请求参数 lang → Accept-Language头 → 默认zh
     */
    private String resolveLang(HttpServletRequest request) {
        // 1. 请求参数 ?lang=en
        String langParam = request.getParameter("lang");
        if (langParam != null && (langParam.startsWith("en") || langParam.startsWith("zh"))) {
            return langParam.startsWith("en") ? "en" : "zh";
        }
        // 2. Accept-Language 头
        String acceptLang = request.getHeader("Accept-Language");
        if (acceptLang != null) {
            if (acceptLang.toLowerCase().contains("zh")) return "zh";
            if (acceptLang.toLowerCase().contains("en")) return "en";
        }
        // 3. 默认中文
        return "zh";
    }
}
