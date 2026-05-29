package com.example.admin.service;

import com.example.admin.entity.SysErrorCode;
import com.example.admin.mapper.SysErrorCodeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 错误码服务 — DB持久化 + Redis缓存 + 启动预热 + 中英文双语
 *
 * Redis结构：Hash，key=sys:error_codes，field=code，value=JSON{zhMsg,enMsg,module}
 * 获取流程：Redis Hash → DB(回源) → 默认英文兜底
 */
@Service
public class ErrorCodeService {

    private static final Logger log = LoggerFactory.getLogger(ErrorCodeService.class);
    private static final String REDIS_KEY = "sys:error_codes";

    @Autowired
    private SysErrorCodeMapper errorCodeMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 项目启动时从DB加载全部错误码到Redis
     */
    @PostConstruct
    public void init() {
        try {
            List<SysErrorCode> list = errorCodeMapper.selectAll();
            if (list == null || list.isEmpty()) {
                log.warn("错误码表为空，跳过缓存预热");
                return;
            }
            Map<String, String> cacheMap = new HashMap<>();
            for (SysErrorCode ec : list) {
                String json = objectMapper.writeValueAsString(toCacheValue(ec));
                cacheMap.put(String.valueOf(ec.getCode()), json);
            }
            redisTemplate.opsForHash().putAll(REDIS_KEY, cacheMap);
            log.info("错误码缓存预热完成，共加载 {} 条错误码", list.size());
        } catch (Exception e) {
            log.error("错误码缓存预热失败", e);
        }
    }

    /**
     * 根据错误码获取消息（中文）
     */
    public String getZhMsg(int code) {
        return getMsg(code, "zhMsg");
    }

    /**
     * 根据错误码获取消息（英文）
     */
    public String getEnMsg(int code) {
        return getMsg(code, "enMsg");
    }

    /**
     * 根据错误码和语言获取消息
     * @param code 错误码
     * @param lang zh 或 en
     */
    public String getMessage(int code, String lang) {
        if ("en".equalsIgnoreCase(lang)) {
            return getEnMsg(code);
        }
        return getZhMsg(code); // 默认中文
    }

    /**
     * 获取完整错误码实体（含中英文）
     */
    public SysErrorCode getErrorCode(int code) {
        // 先从Redis取
        try {
            Object cached = redisTemplate.opsForHash().get(REDIS_KEY, String.valueOf(code));
            if (cached != null) {
                return fromCacheValue(objectMapper.readValue(cached.toString(), Map.class));
            }
        } catch (Exception e) {
            log.warn("从Redis读取错误码失败 code={}", code, e);
        }
        // 回源DB
        SysErrorCode ec = errorCodeMapper.selectByCode(code);
        if (ec != null) {
            try {
                redisTemplate.opsForHash().put(REDIS_KEY, String.valueOf(code),
                        objectMapper.writeValueAsString(toCacheValue(ec)));
            } catch (Exception ex) {
                log.warn("回写错误码缓存失败 code={}", code, ex);
            }
        }
        return ec;
    }

    /**
     * 新增/更新错误码 — 写DB并同步Redis
     */
    public void save(SysErrorCode ec) {
        SysErrorCode existing = errorCodeMapper.selectByCode(ec.getCode());
        if (existing != null) {
            errorCodeMapper.update(ec);
        } else {
            if (ec.getCreateTime() == null) {
                ec.setCreateTime(java.time.LocalDateTime.now());
            }
            errorCodeMapper.insert(ec);
        }
        try {
            redisTemplate.opsForHash().put(REDIS_KEY, String.valueOf(ec.getCode()),
                    objectMapper.writeValueAsString(toCacheValue(ec)));
        } catch (Exception e) {
            log.warn("同步错误码缓存失败 code={}", ec.getCode(), e);
        }
    }

    /**
     * 删除错误码 — 删DB并同步Redis
     */
    public void delete(Integer code) {
        errorCodeMapper.deleteByCode(code);
        try {
            redisTemplate.opsForHash().delete(REDIS_KEY, String.valueOf(code));
        } catch (Exception e) {
            log.warn("删除错误码缓存失败 code={}", code, e);
        }
    }

    /**
     * 手动刷新全部缓存
     */
    public void refreshCache() {
        try {
            redisTemplate.delete(REDIS_KEY);
        } catch (Exception e) {
            log.warn("清空错误码缓存失败", e);
        }
        init();
    }

    /**
     * 获取全部错误码列表
     */
    public List<SysErrorCode> listAll() {
        return errorCodeMapper.selectAll();
    }

    // ──────────────── 内部方法 ────────────────

    private String getMsg(int code, String field) {
        try {
            Object cached = redisTemplate.opsForHash().get(REDIS_KEY, String.valueOf(code));
            if (cached != null) {
                Map map = objectMapper.readValue(cached.toString(), Map.class);
                Object msg = map.get(field);
                if (msg != null) return msg.toString();
            }
        } catch (Exception e) {
            log.warn("从Redis读取错误码消息失败 code={}", code, e);
        }
        // 回源DB
        SysErrorCode ec = errorCodeMapper.selectByCode(code);
        if (ec != null) {
            // 异步回写缓存
            try {
                redisTemplate.opsForHash().put(REDIS_KEY, String.valueOf(code),
                        objectMapper.writeValueAsString(toCacheValue(ec)));
            } catch (Exception ignored) {}
            return "zhMsg".equals(field) ? ec.getZhMsg() : ec.getEnMsg();
        }
        return "zhMsg".equals(field) ? "系统内部错误" : "Internal Server Error";
    }

    private Map<String, String> toCacheValue(SysErrorCode ec) {
        Map<String, String> m = new HashMap<>();
        m.put("zhMsg", ec.getZhMsg() != null ? ec.getZhMsg() : "");
        m.put("enMsg", ec.getEnMsg() != null ? ec.getEnMsg() : "");
        m.put("module", ec.getModule() != null ? ec.getModule() : "");
        return m;
    }

    @SuppressWarnings("unchecked")
    private SysErrorCode fromCacheValue(Map<String, Object> map) {
        SysErrorCode ec = new SysErrorCode();
        ec.setZhMsg((String) map.getOrDefault("zhMsg", ""));
        ec.setEnMsg((String) map.getOrDefault("enMsg", ""));
        ec.setModule((String) map.getOrDefault("module", ""));
        return ec;
    }
}
