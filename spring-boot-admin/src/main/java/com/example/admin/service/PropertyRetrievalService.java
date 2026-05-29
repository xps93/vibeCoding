package com.example.admin.service;

import com.example.admin.rag.EmbeddingService;
import com.example.admin.rag.dto.PropertySearchRequest;
import com.example.admin.rag.dto.PropertySearchResult;
import com.example.admin.rag.model.Document;
import com.example.admin.rag.model.FilterExpression;
import com.example.admin.rag.model.SearchRequest;
import com.example.admin.rag.vectorstore.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 房产向量检索服务 — 混合检索（向量相似度 + 关键词评分融合）。
 * <p>
 * 当前 SimpleVectorStore 阶段，复杂过滤在内存层做兜底处理。
 * </p>
 *
 * TODO: 切换 PgVector 后，将此 Service 中的内存后置过滤逻辑移除，
 *       改为通过 FilterExpression 生成 SQL WHERE 子句在数据库层执行。
 */
@Service
public class PropertyRetrievalService {

    private static final Logger log = LoggerFactory.getLogger(PropertyRetrievalService.class);

    private static final double VECTOR_WEIGHT = 0.4;
    private static final double KEYWORD_WEIGHT = 0.4;
    private static final double STRUCTURED_WEIGHT = 0.2;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingService embeddingService;

    /**
     * 混合检索：向量相似度 + 关键词评分融合。
     */
    public PropertySearchResult search(PropertySearchRequest bizReq) {
        String queryText = bizReq.getQuery();
        if (queryText == null || queryText.trim().isEmpty()) {
            throw new IllegalArgumentException("查询内容不能为空");
        }

        int topK = bizReq.getTopK() > 0 ? bizReq.getTopK() : 10;
        log.info("[RAG检索] 开始检索 query=\"{}\" topK={}", queryText, topK);

        // 1. 生成查询向量
        List<Double> queryEmbedding = embeddingService.embed(queryText);
        if (queryEmbedding.isEmpty()) {
            throw new RuntimeException("向量生成失败");
        }

        // 2. 向量检索（扩大候选集）
        FilterExpression filter = buildFuzzyFilter(bizReq);
        if (filter != null) {
            log.info("[RAG检索] 模糊筛选条件: {}", filter);
        } else {
            log.info("[RAG检索] 无筛选条件，纯语义检索");
        }
        SearchRequest searchRequest = SearchRequest.withEmbedding(queryEmbedding)
                .withTopK(topK * 3)
                .withSimilarityThreshold(0.0)
                .withFilter(filter);

        List<Document> candidates = vectorStore.similaritySearch(searchRequest);
        log.info("[RAG检索] 向量检索候选集: {} 条", candidates.size());

        // 3. 内存后置过滤（TODO: PgVector 后移除）
        int beforePostFilter = candidates.size();
        candidates = postFilter(candidates, bizReq);
        if (candidates.size() < beforePostFilter) {
            log.info("[RAG检索] 内存后置过滤: {} → {} 条", beforePostFilter, candidates.size());
        }

        // 硬筛选无结果时回退：去掉 QueryParser 提取的精确筛选条件重新检索
        if (candidates.isEmpty() && filter != null) {
            log.info("[RAG检索] 模糊筛选无结果，回退到无筛选检索");
            SearchRequest fallbackReq = SearchRequest.withEmbedding(queryEmbedding)
                    .withTopK(topK * 3)
                    .withSimilarityThreshold(0.0)
                    .withFilter(null);
            candidates = vectorStore.similaritySearch(fallbackReq);
            candidates = postFilter(candidates, bizReq);
            log.info("[RAG检索] 回退检索候选集: {} 条", candidates.size());
        }

        if (candidates.isEmpty()) {
            log.info("[RAG检索] 无匹配结果，向量库总量: {}", vectorStore.count());
            return emptyResult(queryText);
        }

        // 4-6. 评分与融合（省略详细日志，仅记 top3）
        // ... (scoring logic unchanged, log top results at end)

        log.info("[RAG检索] 开始融合评分，候选 {} 条", candidates.size());

        // 4. 计算关键词评分
        List<String> queryTokens = tokenize(queryText);
        Map<String, Double> keywordScores = new HashMap<>();
        double maxKeywordScore = 0.0;
        for (Document doc : candidates) {
            double ks = keywordScore(doc.getContent(), queryTokens);
            keywordScores.put(doc.getId(), ks);
            if (ks > maxKeywordScore) maxKeywordScore = ks;
        }

        // 5. 计算向量相似度
        Map<String, Double> vectorScores = new HashMap<>();
        double maxVectorScore = 0.0;
        for (Document doc : candidates) {
            double vs = computeSimilarity(queryEmbedding, doc.getEmbedding());
            vectorScores.put(doc.getId(), vs);
            if (vs > maxVectorScore) maxVectorScore = vs;
        }

        // 6. 计算结构化匹配加分
        Map<String, Double> structScores = new HashMap<>();
        double maxStructScore = 0.0;
        for (Document doc : candidates) {
            double ss = structuredBonus(doc, bizReq);
            structScores.put(doc.getId(), ss);
            if (ss > maxStructScore) maxStructScore = ss;
        }

        // 7. 融合评分并排序
        List<ScoredDoc> scored = new ArrayList<>();
        for (Document doc : candidates) {
            double normalizedVector = maxVectorScore > 0 ? vectorScores.get(doc.getId()) / maxVectorScore : 0;
            double normalizedKeyword = maxKeywordScore > 0 ? keywordScores.get(doc.getId()) / maxKeywordScore : 0;
            double normalizedStruct = maxStructScore > 0 ? structScores.get(doc.getId()) / maxStructScore : 0;
            double fused = VECTOR_WEIGHT * normalizedVector
                    + KEYWORD_WEIGHT * normalizedKeyword
                    + STRUCTURED_WEIGHT * normalizedStruct;
            scored.add(new ScoredDoc(doc, fused, vectorScores.get(doc.getId()),
                    keywordScores.get(doc.getId()), structScores.get(doc.getId())));
        }
        scored.sort((a, b) -> Double.compare(b.fusedScore, a.fusedScore));

        // 7. 取 topK 并映射 DTO
        List<ScoredDoc> topResults = scored.subList(0, Math.min(topK, scored.size()));

        // 打印 top3 结果
        int show = Math.min(3, topResults.size());
        for (int i = 0; i < show; i++) {
            ScoredDoc sd = topResults.get(i);
            log.info("[RAG检索] Top{}: id={} fused={} vec={} kw={} struct={} title={}",
                    i + 1, sd.doc.getId(),
                    String.format("%.4f", sd.fusedScore),
                    String.format("%.4f", sd.vectorScore),
                    String.format("%.4f", sd.keywordScore),
                    String.format("%.4f", sd.structScore),
                    sd.doc.getMetadata() != null ? sd.doc.getMetadata().getOrDefault("title", "") : "");
        }

        return toResult(queryText, topResults);
    }

    /**
     * 检索房产数据并格式化为 AI 上下文（压缩格式，节省 Token）。
     * <p>
     * 每条仅保留核心字段：名称|区域|户型|面积|总价|装修|标签，约 40-60 字符/条。
     * </p>
     */
    public String searchAsContext(PropertySearchRequest bizReq) {
        PropertySearchResult result = search(bizReq);

        if (result.getItems() == null || result.getItems().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【房产数据").append(result.getMatchCount()).append("条】\n");
        for (int i = 0; i < result.getItems().size(); i++) {
            PropertySearchResult.PropertyItem item = result.getItems().get(i);
            sb.append("● ").append(formatCompact(item));
            sb.append("\n");
        }
        sb.append("基于以上数据回答，不足则告知。");
        log.info("[RAG检索] 上下文压缩: {} 条, {} 字符", result.getItems().size(), sb.length());
        return sb.toString();
    }

    /** 将一条房产文档格式化为紧凑的单行文本 */
    private String formatCompact(PropertySearchResult.PropertyItem item) {
        Map<String, Object> meta = item.getMetadata();
        if (meta == null) return item.getContent() != null ? item.getContent() : "";

        StringBuilder sb = new StringBuilder();

        // 小区名 + 区域
        String community = str(meta.get("community"));
        String district = str(meta.get("district"));
        String bizcircle = str(meta.get("bizcircle"));
        if (!community.isEmpty()) {
            sb.append(community);
        }
        if (!district.isEmpty()) {
            sb.append("|").append(district);
            if (!bizcircle.isEmpty()) sb.append(bizcircle);
        }

        // 户型
        String layout = str(meta.get("layout"));
        if (!layout.isEmpty()) sb.append("|").append(layout);
        else {
            Object bd = meta.get("bedrooms");
            Object lr = meta.get("living_rooms");
            if (bd != null) {
                sb.append("|").append(bd).append("室");
                if (lr != null) sb.append(lr).append("厅");
            }
        }

        // 面积
        Object area = meta.get("area");
        if (area != null) sb.append("|").append(area).append("㎡");

        // 总价
        Object price = meta.get("total_price");
        if (price != null) {
            double p = ((Number) price).doubleValue();
            sb.append("|").append((int) p).append("万");
        }

        // 装修
        String deco = str(meta.get("decoration"));
        if (!deco.isEmpty()) sb.append("|").append(deco);

        // 关键标签（最多2个）
        String tags = str(meta.get("tags"));
        if (!tags.isEmpty()) {
            String[] parts = tags.split(",");
            sb.append("|");
            int show = Math.min(2, parts.length);
            for (int j = 0; j < show; j++) sb.append(parts[j]).append("/");
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    private static String str(Object v) {
        return v == null ? "" : v.toString();
    }

    // ════════════════ 关键词评分（简化 BM25） ════════════════

    private List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) return Collections.emptyList();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            tokens.add(String.valueOf(text.charAt(i)));
            if (i < text.length() - 1) {
                tokens.add(text.substring(i, i + 2));
            }
        }
        for (String word : text.split("[\\s,，。！？、]+")) {
            if (word.length() > 2) tokens.add(word.toLowerCase());
        }
        return tokens;
    }

    private double keywordScore(String docContent, List<String> queryTokens) {
        if (docContent == null || queryTokens.isEmpty()) return 0.0;
        String lower = docContent.toLowerCase();
        double score = 0.0;
        for (String token : queryTokens) {
            if (token.isEmpty()) continue;
            int idx = 0;
            int count = 0;
            while ((idx = lower.indexOf(token.toLowerCase(), idx)) != -1) {
                count++;
                idx += token.length();
            }
            if (count > 0) {
                double tf = count;
                score += tf / (tf + 2.0);
            }
        }
        long matchedTokens = queryTokens.stream()
                .filter(t -> lower.contains(t.toLowerCase()))
                .distinct().count();
        double coverage = queryTokens.isEmpty() ? 0 : (double) matchedTokens / queryTokens.size();
        return score * (1.0 + coverage);
    }

    // ════════════════ 结构化匹配加分 ════════════════

    /** 计算文档结构化字段与查询条件的匹配程度（0~N，越高越匹配） */
    private double structuredBonus(Document doc, PropertySearchRequest req) {
        double bonus = 0.0;

        // 户型精确匹配
        if (req.getBedrooms() != null && req.getBedrooms().equals(doc.getBedrooms())) {
            bonus += 1.0;
        }
        if (req.getLivingRooms() != null && req.getLivingRooms().equals(doc.getLivingRooms())) {
            bonus += 0.5;
        }
        if (req.getBathrooms() != null && req.getBathrooms().equals(doc.getBathrooms())) {
            bonus += 0.5;
        }

        // 价格范围匹配（在范围内加分）
        if (req.getMinPrice() != null && doc.getTotalPrice() != null
                && doc.getTotalPrice() >= req.getMinPrice()) bonus += 0.5;
        if (req.getMaxPrice() != null && doc.getTotalPrice() != null
                && doc.getTotalPrice() <= req.getMaxPrice()) bonus += 0.5;

        // 面积范围匹配
        if (req.getMinArea() != null && doc.getArea() != null
                && doc.getArea() >= req.getMinArea()) bonus += 0.5;
        if (req.getMaxArea() != null && doc.getArea() != null
                && doc.getArea() <= req.getMaxArea()) bonus += 0.5;

        // 装修匹配
        if (req.getDecoration() != null && req.getDecoration().equals(doc.getDecoration())) {
            bonus += 1.0;
        }

        // 朝向匹配
        if (req.getOrientation() != null && doc.getOrientation() != null
                && doc.getOrientation().contains(req.getOrientation())) {
            bonus += 0.8;
        }

        // 电梯匹配
        if (req.getHasElevator() != null && req.getHasElevator().equals(doc.getHasElevator())) {
            bonus += 0.5;
        }

        // 标签匹配
        if (req.getTags() != null && doc.getTags() != null) {
            for (String t : req.getTags().split(",")) {
                if (doc.getTags().contains(t)) bonus += 0.8;
            }
        }

        return bonus;
    }

    // ════════════════ Filter 构建 ════════════════

    private FilterExpression buildFilter(PropertySearchRequest bizReq) {
        List<FilterExpression> conditions = new ArrayList<>();

        // ── 位置层级（精确匹配） ──
        if (notEmpty(bizReq.getCity())) {
            conditions.add(FilterExpression.eq("city", bizReq.getCity()));
        }
        if (notEmpty(bizReq.getDistrict())) {
            conditions.add(FilterExpression.eq("district", bizReq.getDistrict()));
        }
        if (notEmpty(bizReq.getBizcircle())) {
            conditions.add(FilterExpression.eq("bizcircle", bizReq.getBizcircle()));
        }
        if (notEmpty(bizReq.getCommunity())) {
            conditions.add(FilterExpression.like("community", bizReq.getCommunity()));
        }

        // ── 规格筛选 ──
        if (notEmpty(bizReq.getLayout())) {
            conditions.add(FilterExpression.like("layout", bizReq.getLayout()));
        }
        // 精确室数筛选（QueryParser 自动提取，只对 bedrooms 做硬筛选）
        if (bizReq.getBedrooms() != null) {
            conditions.add(FilterExpression.eq("bedrooms", bizReq.getBedrooms()));
        }
        // living_rooms / bathrooms 仅用于 structuredBonus 软加权，不做硬筛选
        // 户型范围筛选
        if (bizReq.getMinBedrooms() != null) {
            conditions.add(FilterExpression.gte("bedrooms", bizReq.getMinBedrooms()));
        }
        if (bizReq.getMaxBedrooms() != null) {
            conditions.add(FilterExpression.lte("bedrooms", bizReq.getMaxBedrooms()));
        }
        if (notEmpty(bizReq.getOrientation())) {
            conditions.add(FilterExpression.like("orientation", bizReq.getOrientation()));
        }
        if (notEmpty(bizReq.getFloorLevel())) {
            conditions.add(FilterExpression.eq("floor_level", bizReq.getFloorLevel()));
        }
        if (notEmpty(bizReq.getDecoration())) {
            conditions.add(FilterExpression.eq("decoration", bizReq.getDecoration()));
        }
        if (bizReq.getMinBuildYear() != null) {
            conditions.add(FilterExpression.gte("build_year", bizReq.getMinBuildYear()));
        }
        if (bizReq.getMaxBuildYear() != null) {
            conditions.add(FilterExpression.lte("build_year", bizReq.getMaxBuildYear()));
        }
        if (notEmpty(bizReq.getBuildingType())) {
            conditions.add(FilterExpression.eq("building_type", bizReq.getBuildingType()));
        }
        if (bizReq.getHasElevator() != null) {
            conditions.add(FilterExpression.eq("has_elevator", bizReq.getHasElevator()));
        }

        // ── 权属筛选 ──
        if (notEmpty(bizReq.getTransactionOwnership())) {
            conditions.add(FilterExpression.eq("transaction_ownership", bizReq.getTransactionOwnership()));
        }
        if (notEmpty(bizReq.getHouseholdYears())) {
            conditions.add(FilterExpression.eq("household_years", bizReq.getHouseholdYears()));
        }

        // ── 标签筛选 ──
        if (notEmpty(bizReq.getTags())) {
            conditions.add(FilterExpression.like("tags", bizReq.getTags()));
        }

        if (conditions.isEmpty()) return null;
        if (conditions.size() == 1) return conditions.get(0);
        return FilterExpression.and(conditions.toArray(new FilterExpression[0]));
    }

    /** TODO: PgVector 后移除，改为 SQL WHERE */
    private List<Document> postFilter(List<Document> docs, PropertySearchRequest bizReq) {
        return docs.stream().filter(doc -> {
            if (bizReq.getMinPrice() != null) {
                Double price = doc.getTotalPrice();
                if (price == null || price < bizReq.getMinPrice()) return false;
            }
            if (bizReq.getMaxPrice() != null) {
                Double price = doc.getTotalPrice();
                if (price == null || price > bizReq.getMaxPrice()) return false;
            }
            if (bizReq.getMinArea() != null) {
                Double area = doc.getArea();
                if (area == null || area < bizReq.getMinArea()) return false;
            }
            if (bizReq.getMaxArea() != null) {
                Double area = doc.getArea();
                if (area == null || area > bizReq.getMaxArea()) return false;
            }
            if (bizReq.getMinUnitPrice() != null) {
                Double unitPrice = doc.getUnitPrice();
                if (unitPrice == null || unitPrice < bizReq.getMinUnitPrice()) return false;
            }
            if (bizReq.getMaxUnitPrice() != null) {
                Double unitPrice = doc.getUnitPrice();
                if (unitPrice == null || unitPrice > bizReq.getMaxUnitPrice()) return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    // ════════════════ Fuzzy Filter 构建 ════════════════

    /**
     * 构建模糊筛选条件 — 文本字段用 LIKE（子串包含），数值字段做范围扩展。
     * <p>
     * 核心原则：禁止 EQ 精确匹配，所有筛选都带容差，提升召回率。
     * </p>
     */
    private FilterExpression buildFuzzyFilter(PropertySearchRequest bizReq) {
        List<FilterExpression> conditions = new ArrayList<>();

        // ── 文本字段：全部使用 LIKE ──
        addLikeIfNotEmpty(conditions, "city", bizReq.getCity());
        addLikeIfNotEmpty(conditions, "district", bizReq.getDistrict());
        addLikeIfNotEmpty(conditions, "bizcircle", bizReq.getBizcircle());
        addLikeIfNotEmpty(conditions, "community", bizReq.getCommunity());
        addLikeIfNotEmpty(conditions, "layout", bizReq.getLayout());
        addLikeIfNotEmpty(conditions, "orientation", bizReq.getOrientation());
        addLikeIfNotEmpty(conditions, "decoration", bizReq.getDecoration());
        addLikeIfNotEmpty(conditions, "floor_level", bizReq.getFloorLevel());
        addLikeIfNotEmpty(conditions, "building_type", bizReq.getBuildingType());
        addLikeIfNotEmpty(conditions, "tags", bizReq.getTags());
        addLikeIfNotEmpty(conditions, "transaction_ownership", bizReq.getTransactionOwnership());
        addLikeIfNotEmpty(conditions, "household_years", bizReq.getHouseholdYears());

        // ── 数值字段：范围扩展 ──
        addFuzzyBedrooms(conditions, bizReq);
        addFuzzyRange(conditions, "total_price", bizReq.getMinPrice(), bizReq.getMaxPrice(), 0.2);
        addFuzzyRange(conditions, "area", bizReq.getMinArea(), bizReq.getMaxArea(), 0.2);
        addFuzzyRange(conditions, "unit_price", bizReq.getMinUnitPrice(), bizReq.getMaxUnitPrice(), 0.2);

        if (bizReq.getMinBuildYear() != null) {
            conditions.add(FilterExpression.gte("build_year", bizReq.getMinBuildYear()));
        }
        if (bizReq.getMaxBuildYear() != null) {
            conditions.add(FilterExpression.lte("build_year", bizReq.getMaxBuildYear()));
        }

        // ── 布尔字段：保持 EQ（二值无模糊意义） ──
        if (bizReq.getHasElevator() != null) {
            conditions.add(FilterExpression.eq("has_elevator", bizReq.getHasElevator()));
        }

        if (conditions.isEmpty()) return null;
        if (conditions.size() == 1) return conditions.get(0);
        return FilterExpression.and(conditions.toArray(new FilterExpression[0]));
    }

    private void addLikeIfNotEmpty(List<FilterExpression> conditions, String field, String value) {
        if (value != null && !value.isEmpty()) {
            conditions.add(FilterExpression.like(field, value));
        }
    }

    /**
     * 数值范围模糊扩展：下限降低 ratio，上限提高 ratio。
     * 例如 maxPrice=500, ratio=0.2 → 实际筛选 maxPrice≤600
     */
    private void addFuzzyRange(List<FilterExpression> conditions, String field,
                               Double min, Double max, double ratio) {
        if (min != null) {
            double relaxed = min * (1.0 - ratio);
            conditions.add(FilterExpression.gte(field, relaxed));
        }
        if (max != null) {
            double relaxed = max * (1.0 + ratio);
            conditions.add(FilterExpression.lte(field, relaxed));
        }
    }

    /**
     * 卧室数模糊：精确值 N → 搜索 [N-1, N+1]，最少为 1
     */
    private void addFuzzyBedrooms(List<FilterExpression> conditions, PropertySearchRequest req) {
        if (req.getBedrooms() != null) {
            int n = req.getBedrooms();
            int lo = Math.max(1, n - 1);
            int hi = n + 1;
            conditions.add(FilterExpression.gte("bedrooms", lo));
            conditions.add(FilterExpression.lte("bedrooms", hi));
        } else {
            if (req.getMinBedrooms() != null) {
                conditions.add(FilterExpression.gte("bedrooms", req.getMinBedrooms()));
            }
            if (req.getMaxBedrooms() != null) {
                conditions.add(FilterExpression.lte("bedrooms", req.getMaxBedrooms()));
            }
        }
    }

    // ════════════════ 余弦相似度 ════════════════

    private double computeSimilarity(List<Double> a, List<Double> b) {
        if (a == null || b == null || a.size() != b.size()) return 0.0;
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // ════════════════ 结果映射 ════════════════

    private PropertySearchResult emptyResult(String query) {
        PropertySearchResult result = new PropertySearchResult();
        result.setQuery(query);
        result.setMatchCount(0);
        result.setVectorStoreSize(vectorStore.count());
        result.setItems(Collections.emptyList());
        return result;
    }

    private PropertySearchResult toResult(String query, List<ScoredDoc> scored) {
        PropertySearchResult result = new PropertySearchResult();
        result.setQuery(query);
        result.setMatchCount(scored.size());
        result.setVectorStoreSize(vectorStore.count());

        List<PropertySearchResult.PropertyItem> items = new ArrayList<>();
        for (ScoredDoc sd : scored) {
            PropertySearchResult.PropertyItem item = new PropertySearchResult.PropertyItem();
            item.setId(sd.doc.getId());
            item.setContent(sd.doc.getContent());
            item.setMetadata(sd.doc.getMetadata());
            item.setSimilarity(sd.fusedScore);
            items.add(item);
        }
        result.setItems(items);
        return result;
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    private static class ScoredDoc {
        final Document doc;
        final double fusedScore;
        final double vectorScore;
        final double keywordScore;
        final double structScore;
        ScoredDoc(Document doc, double fused, double vs, double ks, double ss) {
            this.doc = doc; this.fusedScore = fused; this.vectorScore = vs; this.keywordScore = ks; this.structScore = ss;
        }
    }
}
