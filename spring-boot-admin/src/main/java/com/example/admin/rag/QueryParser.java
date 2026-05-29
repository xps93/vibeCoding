package com.example.admin.rag;

import com.example.admin.rag.dto.PropertySearchRequest;

import java.util.*;
import java.util.regex.*;

/**
 * 房产查询解析器 — 从自然语言查询中提取结构化筛选条件。
 * <p>
 * 纯工具类，无外部依赖。用于弥补本地哈希向量语义能力不足的问题，
 * 将用户口语化的房产条件自动转换为 {@link PropertySearchRequest} 的精确筛选字段。
 * </p>
 *
 * <p>使用方式：在检索前调用 {@code QueryParser.parse(queryText, searchRequest)}，
 * 已设置的字段不会被覆盖。</p>
 */
public final class QueryParser {

    private QueryParser() {}

    // ── 中文数字映射 ──
    private static final Map<String, Integer> CN_NUM = new LinkedHashMap<>();
    static {
        CN_NUM.put("九", 9); CN_NUM.put("八", 8); CN_NUM.put("七", 7);
        CN_NUM.put("六", 6); CN_NUM.put("五", 5); CN_NUM.put("四", 4);
        CN_NUM.put("三", 3); CN_NUM.put("二", 2); CN_NUM.put("两", 2);
        CN_NUM.put("一", 1); CN_NUM.put("单", 1); CN_NUM.put("双", 2);
    }

    // ── 城市 → 完整名称 ──
    private static final Map<String, String> CITY_MAP = new LinkedHashMap<>();
    static {
        CITY_MAP.put("北京", "北京"); CITY_MAP.put("上海", "上海");
        CITY_MAP.put("深圳", "深圳"); CITY_MAP.put("广州", "广州");
        CITY_MAP.put("杭州", "杭州"); CITY_MAP.put("成都", "成都");
        CITY_MAP.put("南京", "南京"); CITY_MAP.put("武汉", "武汉");
        CITY_MAP.put("重庆", "重庆"); CITY_MAP.put("苏州", "苏州");
    }

    // ── 热门行政区关键词 → 完整名称 ──
    private static final Map<String, String> DISTRICT_MAP = new LinkedHashMap<>();
    static {
        DISTRICT_MAP.put("朝阳", "朝阳区"); DISTRICT_MAP.put("海淀", "海淀区");
        DISTRICT_MAP.put("丰台", "丰台区"); DISTRICT_MAP.put("东城", "东城区");
        DISTRICT_MAP.put("西城", "西城区"); DISTRICT_MAP.put("通州", "通州区");
        DISTRICT_MAP.put("大兴", "大兴区"); DISTRICT_MAP.put("石景山", "石景山区");
        DISTRICT_MAP.put("浦东", "浦东新区"); DISTRICT_MAP.put("徐汇", "徐汇区");
        DISTRICT_MAP.put("静安", "静安区"); DISTRICT_MAP.put("黄浦", "黄浦区");
        DISTRICT_MAP.put("闵行", "闵行区"); DISTRICT_MAP.put("杨浦", "杨浦区");
        DISTRICT_MAP.put("虹口", "虹口区"); DISTRICT_MAP.put("普陀", "普陀区");
        DISTRICT_MAP.put("南山", "南山区"); DISTRICT_MAP.put("福田", "福田区");
        DISTRICT_MAP.put("罗湖", "罗湖区"); DISTRICT_MAP.put("宝安", "宝安区");
    }

    // ── 朝向关键词 ──
    private static final Map<String, String> ORIENTATION_MAP = new LinkedHashMap<>();
    static {
        ORIENTATION_MAP.put("南北通透", "南北通透");
        ORIENTATION_MAP.put("朝南", "南"); ORIENTATION_MAP.put("南向", "南");
        ORIENTATION_MAP.put("朝北", "北"); ORIENTATION_MAP.put("北向", "北");
        ORIENTATION_MAP.put("朝东", "东"); ORIENTATION_MAP.put("东向", "东");
        ORIENTATION_MAP.put("朝西", "西"); ORIENTATION_MAP.put("西向", "西");
        ORIENTATION_MAP.put("东南", "东南"); ORIENTATION_MAP.put("西南", "西南");
        ORIENTATION_MAP.put("东北", "东北"); ORIENTATION_MAP.put("西北", "西北");
    }

    // ── 装修关键词 ──
    private static final Map<String, String> DECORATION_MAP = new LinkedHashMap<>();
    static {
        DECORATION_MAP.put("豪装", "豪装"); DECORATION_MAP.put("豪华装修", "豪装");
        DECORATION_MAP.put("精装", "精装"); DECORATION_MAP.put("精装修", "精装");
        DECORATION_MAP.put("简装", "简装"); DECORATION_MAP.put("简单装修", "简装");
        DECORATION_MAP.put("毛坯", "毛坯"); DECORATION_MAP.put("毛坯房", "毛坯");
    }

    // ── 楼层关键词 ──
    private static final Map<String, String> FLOOR_MAP = new LinkedHashMap<>();
    static {
        FLOOR_MAP.put("高楼层", "高楼层"); FLOOR_MAP.put("高层", "高楼层");
        FLOOR_MAP.put("中楼层", "中楼层"); FLOOR_MAP.put("中层", "中楼层");
        FLOOR_MAP.put("低楼层", "低楼层"); FLOOR_MAP.put("低层", "低楼层");
    }

    // ── 建筑类型 ──
    private static final Map<String, String> BUILDING_TYPE_MAP = new LinkedHashMap<>();
    static {
        BUILDING_TYPE_MAP.put("板楼", "板楼"); BUILDING_TYPE_MAP.put("塔楼", "塔楼");
        BUILDING_TYPE_MAP.put("板塔结合", "板塔结合");
    }

    /**
     * 解析自然语言查询，将识别到的条件填充到 request 中。
     * 仅当 request 中对应字段为 null 时才会设置，不覆盖已有值。
     */
    public static void parse(String query, PropertySearchRequest req) {
        if (query == null || query.trim().isEmpty()) return;

        String q = query.trim();

        // ── 1. 户型解析：三室一厅、3室2厅2卫 ──
        parseLayout(q, req);

        // ── 2. 价格解析：500万以内、300-500万、五六百万 ──
        parsePrice(q, req);

        // ── 3. 面积解析：100平以上、80-120平 ──
        parseArea(q, req);

        // ── 4. 朝向 ──
        for (Map.Entry<String, String> e : ORIENTATION_MAP.entrySet()) {
            if (q.contains(e.getKey()) && req.getOrientation() == null) {
                req.setOrientation(e.getValue());
                break;
            }
        }

        // ── 5. 装修 ──
        for (Map.Entry<String, String> e : DECORATION_MAP.entrySet()) {
            if (q.contains(e.getKey()) && req.getDecoration() == null) {
                req.setDecoration(e.getValue());
                break;
            }
        }

        // ── 6. 楼层 ──
        for (Map.Entry<String, String> e : FLOOR_MAP.entrySet()) {
            if (q.contains(e.getKey()) && req.getFloorLevel() == null) {
                req.setFloorLevel(e.getValue());
                break;
            }
        }

        // ── 7. 电梯 ──
        if ((q.contains("有电梯") || q.contains("带电梯") || q.contains("电梯房")) && req.getHasElevator() == null) {
            req.setHasElevator(true);
        }

        // ── 8. 建筑类型 ──
        for (Map.Entry<String, String> e : BUILDING_TYPE_MAP.entrySet()) {
            if (q.contains(e.getKey()) && req.getBuildingType() == null) {
                req.setBuildingType(e.getValue());
                break;
            }
        }

        // ── 9. 城市 ──
        for (Map.Entry<String, String> e : CITY_MAP.entrySet()) {
            if (q.contains(e.getKey()) && req.getCity() == null) {
                req.setCity(e.getValue());
                break;
            }
        }

        // ── 10. 行政区 ──
        for (Map.Entry<String, String> e : DISTRICT_MAP.entrySet()) {
            if (q.contains(e.getKey()) && req.getDistrict() == null) {
                req.setDistrict(e.getValue());
                break;
            }
        }

        // ── 11. 楼龄/年代 ──
        parseBuildYear(q, req);

        // ── 12. 标签 ──
        parseTags(q, req);

        // ── 13. 权属 ──
        parseOwnership(q, req);
    }

    // ════════════════ 户型解析 ════════════════

    private static void parseLayout(String q, PropertySearchRequest req) {
        // 模式：{数字}室{数字}厅{数字}卫（阿拉伯数字或中文数字）
        Pattern p = Pattern.compile(
                "([1-9一二三四五六七八九两])\\s*[室居]\\s*" +
                "([1-9一二三四五六七八九两]?)\\s*[厅堂]?\\s*" +
                "([1-9一二三四五六七八九两]?)\\s*[卫浴]?");
        Matcher m = p.matcher(q);
        if (m.find()) {
            if (req.getBedrooms() == null) {
                Integer bd = toArabic(m.group(1));
                if (bd != null) req.setBedrooms(bd);
            }
            if (req.getLivingRooms() == null) {
                String lr = m.group(2);
                if (lr != null && !lr.isEmpty()) {
                    Integer v = toArabic(lr);
                    if (v != null) req.setLivingRooms(v);
                }
            }
            if (req.getBathrooms() == null) {
                String br = m.group(3);
                if (br != null && !br.isEmpty()) {
                    Integer v = toArabic(br);
                    if (v != null) req.setBathrooms(v);
                }
            }
            // 同时设置 layout 用于模糊匹配
            if (req.getLayout() == null) {
                StringBuilder layout = new StringBuilder();
                if (req.getBedrooms() != null) layout.append(req.getBedrooms()).append("室");
                if (req.getLivingRooms() != null) layout.append(req.getLivingRooms()).append("厅");
                if (req.getBathrooms() != null) layout.append(req.getBathrooms()).append("卫");
                if (layout.length() > 0) req.setLayout(layout.toString());
            }
        }

        // 单独匹配室数（没有厅卫信息时）：三室、3居
        if (req.getBedrooms() == null) {
            Pattern bedP = Pattern.compile("([1-9一二三四五六七八九两])\\s*[室居]");
            Matcher bedM = bedP.matcher(q);
            if (bedM.find()) {
                Integer bd = toArabic(bedM.group(1));
                if (bd != null) req.setBedrooms(bd);
            }
        }
    }

    // ════════════════ 价格解析 ════════════════

    private static void parsePrice(String q, PropertySearchRequest req) {
        // 模糊价格：五六百万 → 500-600万
        Pattern fuzzyP = Pattern.compile("([一二三四五六七八九])([一二三四五六七八九])[百千]万");
        Matcher fuzzyM = fuzzyP.matcher(q);
        if (fuzzyM.find() && req.getMinPrice() == null && req.getMaxPrice() == null) {
            Integer lo = toArabic(fuzzyM.group(1));
            Integer hi = toArabic(fuzzyM.group(2));
            if (lo != null && hi != null) {
                req.setMinPrice(lo * 100.0);
                req.setMaxPrice(hi * 100.0);
                return;
            }
        }

        // 区间：300-500万、300到500万
        Pattern rangeP = Pattern.compile("(\\d+)\\s*[-~到至]\\s*(\\d+)\\s*万");
        Matcher rangeM = rangeP.matcher(q);
        if (rangeM.find() && req.getMinPrice() == null && req.getMaxPrice() == null) {
            req.setMinPrice(Double.parseDouble(rangeM.group(1)));
            req.setMaxPrice(Double.parseDouble(rangeM.group(2)));
            return;
        }

        // 上限：500万以内、500万以下、不到500万、不超过500万
        Pattern maxP = Pattern.compile("(\\d+)\\s*万\\s*(以[内下]|不到|不超过|以下|以内)");
        Matcher maxM = maxP.matcher(q);
        if (maxM.find() && req.getMaxPrice() == null) {
            req.setMaxPrice(Double.parseDouble(maxM.group(1)));
        }

        // 下限：500万以上、超过500万、500万起
        Pattern minP = Pattern.compile("(\\d+)\\s*万\\s*(以[上外]|超过|起|以上)");
        Matcher minM = minP.matcher(q);
        if (minM.find() && req.getMinPrice() == null) {
            req.setMinPrice(Double.parseDouble(minM.group(1)));
        }
    }

    // ════════════════ 面积解析 ════════════════

    private static void parseArea(String q, PropertySearchRequest req) {
        // 区间：80-120平、80到120平
        Pattern rangeP = Pattern.compile("(\\d+)\\s*[-~到至]\\s*(\\d+)\\s*[平㎡]");
        Matcher rangeM = rangeP.matcher(q);
        if (rangeM.find() && req.getMinArea() == null && req.getMaxArea() == null) {
            req.setMinArea(Double.parseDouble(rangeM.group(1)));
            req.setMaxArea(Double.parseDouble(rangeM.group(2)));
            return;
        }

        // 上限：100平以内、100㎡以下
        Pattern maxP = Pattern.compile("(\\d+)\\s*[平㎡]\\s*(以[内下]|不到|不超过|以下|以内)");
        Matcher maxM = maxP.matcher(q);
        if (maxM.find() && req.getMaxArea() == null) {
            req.setMaxArea(Double.parseDouble(maxM.group(1)));
        }

        // 下限：100平以上、大于100平、100平起
        Pattern minP = Pattern.compile("(\\d+)\\s*[平㎡]\\s*(以[上外]|超过|起|以上)");
        Matcher minM = minP.matcher(q);
        if (minM.find() && req.getMinArea() == null) {
            req.setMinArea(Double.parseDouble(minM.group(1)));
        }
    }

    // ════════════════ 楼龄解析 ════════════════

    private static void parseBuildYear(String q, PropertySearchRequest req) {
        // 2015年以后、2015年后
        Pattern p = Pattern.compile("(\\d{4})\\s*年\\s*(以[后上]|以后|之后)");
        Matcher m = p.matcher(q);
        if (m.find() && req.getMinBuildYear() == null) {
            req.setMinBuildYear(Integer.parseInt(m.group(1)));
        }

        // 次新房（最近5年内）
        if (q.contains("次新房") && req.getMinBuildYear() == null) {
            req.setMinBuildYear(java.time.Year.now().getValue() - 5);
        }
    }

    // ════════════════ 标签解析 ════════════════

    private static void parseTags(String q, PropertySearchRequest req) {
        List<String> matched = new ArrayList<>();
        if (q.contains("学区")) matched.add("学区房");
        if (q.contains("地铁") || q.contains("轨道交通")) matched.add("近地铁");
        if (q.contains("江景") || q.contains("河景")) matched.add("江景房");
        if (q.contains("满五唯一")) matched.add("满五唯一");
        if (q.contains("随时看") || q.contains("随时可看")) matched.add("随时看房");

        if (!matched.isEmpty() && req.getTags() == null) {
            req.setTags(String.join(",", matched));
        }
    }

    // ════════════════ 权属解析 ════════════════

    private static void parseOwnership(String q, PropertySearchRequest req) {
        if (req.getHouseholdYears() == null) {
            if (q.contains("满五唯一")) req.setHouseholdYears("满五唯一");
            else if (q.contains("满五不唯一")) req.setHouseholdYears("满五不唯一");
            else if (q.contains("满两年") || q.contains("满二年")) req.setHouseholdYears("满两年");
            else if (q.contains("不满两年") || q.contains("不满二年")) req.setHouseholdYears("不满两年");
        }
    }

    // ════════════════ 中文数字转阿拉伯 ════════════════

    private static Integer toArabic(String s) {
        if (s == null || s.isEmpty()) return null;
        // 已是阿拉伯数字
        try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        // 中文数字
        for (Map.Entry<String, Integer> e : CN_NUM.entrySet()) {
            if (e.getKey().equals(s)) return e.getValue();
        }
        return null;
    }
}
