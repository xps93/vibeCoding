package com.example.admin.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果实体类，继承HashMap用于存储响应数据
 */
public class Result extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /** 状态码键名 */
    public static final String CODE = "code";
    /** 消息键名 */
    public static final String MSG = "msg";
    /** 数据键名 */
    public static final String DATA = "data";

    /**
     * 默认构造方法，设置code为200，msg为"success"
     */
    public Result() {
        put(CODE, 200);
        put(MSG, "success");
    }

    /**
     * 返回成功结果
     */
    public static Result success() {
        return new Result();
    }

    /**
     * 返回成功结果（带数据）
     * @param data 返回的数据对象
     */
    public static Result success(Object data) {
        Result r = new Result();
        r.put(DATA, data);
        return r;
    }

    /**
     * 返回错误结果（指定状态码和消息）
     * @param code 错误状态码
     * @param msg 错误消息
     */
    public static Result error(int code, String msg) {
        Result r = new Result();
        r.put(CODE, code);
        r.put(MSG, msg);
        return r;
    }

    /**
     * 返回错误结果（状态码为500）
     * @param msg 错误消息
     */
    public static Result error(String msg) {
        return error(500, msg);
    }

    /**
     * 重写put方法，返回Result类型以支持链式调用
     */
    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 返回成功结果（指定消息和数据）
     * @param msg 成功消息
     * @param data 返回的数据对象
     */
    public static Result success(String msg, Object data) {
        Result r = new Result();
        r.put(MSG, msg);
        r.put(DATA, data);
        return r;
    }

    /**
     * 返回简单成功Map（code=200, msg="success"）
     */
    public static Map<String, Object> ok() {
        Map<String, Object> m = new HashMap<>();
        m.put("code", 200);
        m.put("msg", "success");
        return m;
    }
}
