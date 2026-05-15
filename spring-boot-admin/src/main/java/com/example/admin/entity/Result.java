package com.example.admin.entity;

import java.util.HashMap;
import java.util.Map;

public class Result extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String DATA = "data";

    public Result() {
        put(CODE, 200);
        put(MSG, "success");
    }

    public static Result success() {
        return new Result();
    }

    public static Result success(Object data) {
        Result r = new Result();
        r.put(DATA, data);
        return r;
    }

    public static Result error(int code, String msg) {
        Result r = new Result();
        r.put(CODE, code);
        r.put(MSG, msg);
        return r;
    }

    public static Result error(String msg) {
        return error(500, msg);
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static Result success(String msg, Object data) {
        Result r = new Result();
        r.put(MSG, msg);
        r.put(DATA, data);
        return r;
    }

    public static Map<String, Object> ok() {
        Map<String, Object> m = new HashMap<>();
        m.put("code", 200);
        m.put("msg", "success");
        return m;
    }
}
