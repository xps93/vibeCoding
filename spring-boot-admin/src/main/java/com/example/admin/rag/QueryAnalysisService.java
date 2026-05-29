package com.example.admin.rag;

import com.example.admin.rag.dto.PropertySearchRequest;
import com.example.admin.service.model.ModelProvider;
import com.example.admin.service.model.ModelProviderFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;

/**
 * LLM 查询分析服务 — 大模型将自然语言转为结构化筛选条件。
 * <p>
 * 特性：
 * <ul>
 *   <li>Redis 缓存：用户查询 → 结构化 JSON，24h TTL，减少 LLM 重复调用</li>
 *   <li>Token 精简：优化提示词、预处理输入、限制输出长度</li>
 *   <li>容错降级：LLM 失败 → 正则 {@link QueryParser}</li>
 * </ul>
 * </p>
 */
@Service
public class QueryAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(QueryAnalysisService.class);

    private static final String ANALYSIS_MODEL = "deepseek-chat";
    private static final int MAX_TOKENS = 200;
    private static final double TEMPERATURE = 0.0;
    private static final String CACHE_PREFIX = "rag:query:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    /** 精简后的系统提示词（~200 tokens），用缩写字段名减少输出 token */
    private static final String SYSTEM_PROMPT =
            "你是房产查询分析器。提取筛选条件，返回纯JSON，无解释。字段(均可省略):\n" +
            "c:城市 d:区 b:商圈 m:小区\n" +
            "r:室数 t:厅数 w:卫数\n" +
            "pl:最低总价(万) ph:最高总价(万)\n" +
            "al:最小面积(㎡) ah:最大面积(㎡)\n" +
            "o:朝向 g:装修(豪装/精装/简装/毛坯)\n" +
            "f:楼层(高楼层/中楼层/低楼层) e:有电梯?bool\n" +
            "p:建筑类型(板楼/塔楼/板塔结合)\n" +
            "tag:标签(逗号分隔) y:最早建成年\n" +
            "hy:权属(满五唯一/满两年等)\n" +
            "例: 北京朝阳500万内三室一厅豪装 → {\"c\":\"北京\",\"d\":\"朝阳区\",\"r\":3,\"t\":1,\"ph\":500,\"g\":\"豪装\"}";

    /** 输入预处理：要去掉的口语化前缀 */
    private static final String[] FILLER_PREFIXES = {
        "我想", "我要", "帮我", "请", "能不能", "可以", "帮我找", "找一下",
        "给我", "想要", "想找", "找一个", "有没有", "哪里"
    };

    @Autowired
    private ModelProviderFactory providerFactory;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析自然语言查询，提取结构化筛选条件。
     * 优先从 Redis 缓存命中，否则调用 LLM。
     */
    public void analyze(String query, PropertySearchRequest request) {
        if (query == null || query.trim().isEmpty()) return;

        String normalized = preprocess(query);
        log.info("[RAG查询分析] 输入: {} (预处理: {})", query, normalized);

        // 1. 先查 Redis 缓存
        String cacheKey = cacheKey(normalized);
        String cached = getCache(cacheKey);
        if (cached != null) {
            log.info("[RAG查询分析] Redis命中 key={} value={}", cacheKey, cached);
            populateFromJson(cached, request);
            logParsedFields(request);
            return;
        }

        log.info("[RAG查询分析] Redis未命中 key={}，调用LLM", cacheKey);

        // 2. 调用 LLM
        try {
            long start = System.currentTimeMillis();
            String json = callLLM(normalized);
            long elapsed = System.currentTimeMillis() - start;
            log.info("[RAG查询分析] LLM返回 ({}ms): {}", elapsed, json);

            // 3. 写入缓存
            setCache(cacheKey, json);
            log.info("[RAG查询分析] 写入Redis key={} ttl=24h", cacheKey);

            populateFromJson(json, request);
            logParsedFields(request);
        } catch (Exception e) {
            log.warn("[RAG查询分析] LLM失败，降级正则: {}", e.getMessage());
            QueryParser.parse(query, request);
            logParsedFields(request);
        }
    }

    // ════════════════ 输入预处理（节省 Token） ════════════════

    /** 去除口语化前缀和多余空白，截断过长输入 */
    String preprocess(String query) {
        String q = query.trim();

        // 去口语前缀：循环去除多个匹配的前缀
        boolean changed;
        do {
            changed = false;
            for (String prefix : FILLER_PREFIXES) {
                if (q.startsWith(prefix)) {
                    q = q.substring(prefix.length()).trim();
                    changed = true;
                    break;
                }
            }
        } while (changed);

        // 限长 150 字符，超长查询做 LLM 分析意义不大
        if (q.length() > 150) {
            q = q.substring(0, 150);
        }
        return q;
    }

    // ════════════════ Redis 缓存 ════════════════

    private String cacheKey(String query) {
        return CACHE_PREFIX + md5(query);
    }

    private String getCache(String key) {
        if (redisTemplate == null) return null;
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("[RAG查询分析] 读取缓存失败: {}", e.getMessage());
            return null;
        }
    }

    private void setCache(String key, String json) {
        if (redisTemplate == null) return;
        try {
            redisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (Exception e) {
            log.warn("[RAG查询分析] 写入缓存失败: {}", e.getMessage());
        }
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }

    // ════════════════ LLM 调用 ════════════════

    private String callLLM(String userQuery) {
        ModelProvider provider = providerFactory.getProvider(null);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", SYSTEM_PROMPT);
        messages.add(sysMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userQuery);
        messages.add(userMsg);

        return provider.chatSync(messages, ANALYSIS_MODEL, TEMPERATURE, MAX_TOKENS);
    }

    // ════════════════ JSON 解析与字段映射 ════════════════

    /**
     * 将 LLM JSON 解析并填充到 PropertySearchRequest。
     * 支持缩写字段名（如 c→city, r→bedrooms）和全称。
     */
    @SuppressWarnings("unchecked")
    private void populateFromJson(String raw, PropertySearchRequest request) {
        String json = raw.trim();
        // 去除 markdown 代码块
        if (json.startsWith("```")) {
            int start = json.indexOf("\n");
            int end = json.lastIndexOf("```");
            if (start > 0 && end > start) {
                json = json.substring(start, end).trim();
            }
        }

        Map<String, Object> map;
        try {
            map = objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.warn("[RAG查询分析] JSON解析失败: {} raw={}", e.getMessage(), json);
            return;
        }
        if (map == null || map.isEmpty()) return;

        // 缩写 + 全称映射
        setStrIfNull(request, map, "c", "city");
        setStrIfNull(request, map, "d", "district");
        setStrIfNull(request, map, "b", "bizcircle");
        setStrIfNull(request, map, "m", "community");
        setStrIfNull(request, map, "o", "orientation");
        setStrIfNull(request, map, "g", "decoration");
        setStrIfNull(request, map, "f", "floorLevel");
        setStrIfNull(request, map, "p", "buildingType");
        setStrIfNull(request, map, "tag", "tags");
        setStrIfNull(request, map, "hy", "householdYears");
        // 全称兼容
        setStrIfNull(request, map, "city", "city");
        setStrIfNull(request, map, "district", "district");
        setStrIfNull(request, map, "bizcircle", "bizcircle");
        setStrIfNull(request, map, "community", "community");
        setStrIfNull(request, map, "orientation", "orientation");
        setStrIfNull(request, map, "decoration", "decoration");
        setStrIfNull(request, map, "floorLevel", "floorLevel");
        setStrIfNull(request, map, "buildingType", "buildingType");
        setStrIfNull(request, map, "tags", "tags");
        setStrIfNull(request, map, "householdYears", "householdYears");

        setIntIfNull(request, map, "r", "bedrooms");
        setIntIfNull(request, map, "t", "livingRooms");
        setIntIfNull(request, map, "w", "bathrooms");
        setIntIfNull(request, map, "y", "minBuildYear");
        setIntIfNull(request, map, "bedrooms", "bedrooms");
        setIntIfNull(request, map, "livingRooms", "livingRooms");
        setIntIfNull(request, map, "bathrooms", "bathrooms");
        setIntIfNull(request, map, "minBuildYear", "minBuildYear");

        setDblIfNull(request, map, "pl", "minPrice");
        setDblIfNull(request, map, "ph", "maxPrice");
        setDblIfNull(request, map, "al", "minArea");
        setDblIfNull(request, map, "ah", "maxArea");
        setDblIfNull(request, map, "minPrice", "minPrice");
        setDblIfNull(request, map, "maxPrice", "maxPrice");
        setDblIfNull(request, map, "minArea", "minArea");
        setDblIfNull(request, map, "maxArea", "maxArea");

        setBoolIfNull(request, map, "e", "hasElevator");
        setBoolIfNull(request, map, "hasElevator", "hasElevator");
    }

    // ════════════════ 字段设置 ════════════════

    private void setStrIfNull(PropertySearchRequest req, Map<String, Object> map, String key, String field) {
        Object val = map.get(key);
        if (val == null) return;
        if (isAlreadySet(req, field)) return;
        try {
            req.getClass().getMethod("set" + capitalize(field), String.class)
                    .invoke(req, String.valueOf(val));
        } catch (Exception e) {
            log.warn("[RAG查询分析] 设置{}失败: {}", field, e.getMessage());
        }
    }

    private void setIntIfNull(PropertySearchRequest req, Map<String, Object> map, String key, String field) {
        Object val = map.get(key);
        if (val == null) return;
        if (isAlreadySet(req, field)) return;
        try {
            int iv = val instanceof Number ? ((Number) val).intValue() : Integer.parseInt(String.valueOf(val));
            req.getClass().getMethod("set" + capitalize(field), Integer.class).invoke(req, iv);
        } catch (Exception e) {
            log.warn("[RAG查询分析] 设置{}失败: {}", field, e.getMessage());
        }
    }

    private void setDblIfNull(PropertySearchRequest req, Map<String, Object> map, String key, String field) {
        Object val = map.get(key);
        if (val == null) return;
        if (isAlreadySet(req, field)) return;
        try {
            double dv = val instanceof Number ? ((Number) val).doubleValue() : Double.parseDouble(String.valueOf(val));
            req.getClass().getMethod("set" + capitalize(field), Double.class).invoke(req, dv);
        } catch (Exception e) {
            log.warn("[RAG查询分析] 设置{}失败: {}", field, e.getMessage());
        }
    }

    private void setBoolIfNull(PropertySearchRequest req, Map<String, Object> map, String key, String field) {
        Object val = map.get(key);
        if (val == null) return;
        if (isAlreadySet(req, field)) return;
        try {
            boolean bv = val instanceof Boolean ? (Boolean) val : Boolean.parseBoolean(String.valueOf(val));
            req.getClass().getMethod("set" + capitalize(field), Boolean.class).invoke(req, bv);
        } catch (Exception e) {
            log.warn("[RAG查询分析] 设置{}失败: {}", field, e.getMessage());
        }
    }

    private boolean isAlreadySet(PropertySearchRequest req, String field) {
        try {
            String getter = "get" + capitalize(field);
            return req.getClass().getMethod(getter).invoke(req) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ════════════════ 日志 ════════════════

    private void logParsedFields(PropertySearchRequest req) {
        StringBuilder sb = new StringBuilder("[RAG查询分析] 提取: ");
        boolean has = false;
        if (req.getCity() != null) { sb.append("city=").append(req.getCity()).append(" "); has = true; }
        if (req.getDistrict() != null) { sb.append("district=").append(req.getDistrict()).append(" "); has = true; }
        if (req.getBizcircle() != null) { sb.append("bizcircle=").append(req.getBizcircle()).append(" "); has = true; }
        if (req.getCommunity() != null) { sb.append("community=").append(req.getCommunity()).append(" "); has = true; }
        if (req.getBedrooms() != null) { sb.append("bedrooms=").append(req.getBedrooms()).append(" "); has = true; }
        if (req.getLivingRooms() != null) { sb.append("livingRooms=").append(req.getLivingRooms()).append(" "); has = true; }
        if (req.getBathrooms() != null) { sb.append("bathrooms=").append(req.getBathrooms()).append(" "); has = true; }
        if (req.getMinPrice() != null) { sb.append("minPrice=").append(req.getMinPrice()).append(" "); has = true; }
        if (req.getMaxPrice() != null) { sb.append("maxPrice=").append(req.getMaxPrice()).append(" "); has = true; }
        if (req.getMinArea() != null) { sb.append("minArea=").append(req.getMinArea()).append(" "); has = true; }
        if (req.getMaxArea() != null) { sb.append("maxArea=").append(req.getMaxArea()).append(" "); has = true; }
        if (req.getOrientation() != null) { sb.append("orientation=").append(req.getOrientation()).append(" "); has = true; }
        if (req.getDecoration() != null) { sb.append("decoration=").append(req.getDecoration()).append(" "); has = true; }
        if (req.getFloorLevel() != null) { sb.append("floorLevel=").append(req.getFloorLevel()).append(" "); has = true; }
        if (req.getHasElevator() != null) { sb.append("hasElevator=").append(req.getHasElevator()).append(" "); has = true; }
        if (req.getBuildingType() != null) { sb.append("buildingType=").append(req.getBuildingType()).append(" "); has = true; }
        if (req.getTags() != null) { sb.append("tags=").append(req.getTags()).append(" "); has = true; }
        if (req.getMinBuildYear() != null) { sb.append("minBuildYear=").append(req.getMinBuildYear()).append(" "); has = true; }
        if (req.getHouseholdYears() != null) { sb.append("householdYears=").append(req.getHouseholdYears()).append(" "); has = true; }
        if (!has) sb.append("(无)");
        log.info(sb.toString());
    }
}
