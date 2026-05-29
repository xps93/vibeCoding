package com.example.admin.service;

import com.example.admin.rag.EmbeddingService;
import com.example.admin.rag.model.Document;
import com.example.admin.rag.vectorstore.VectorStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 房产数据导入服务 — 参照贝壳找房标准字段体系。
 * <p>
 * 负责将结构化房产数据（JSON/CSV）解析为 Document 并写入 VectorStore。
 * 针对 SimpleVectorStore 内存特性做分批处理（每批100条），避免 OOM。
 * </p>
 */
@Service
public class PropertyIngestionService {

    private static final Logger log = LoggerFactory.getLogger(PropertyIngestionService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int BATCH_SIZE = 100;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingService embeddingService;

    /** 从 MultipartFile 导入（支持 JSON 数组和 CSV） */
    public Map<String, Object> ingestFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名为空");
        }
        List<Map<String, Object>> records;
        if (fileName.endsWith(".json")) {
            records = parseJsonFile(file);
        } else if (fileName.endsWith(".csv")) {
            records = parseCsvFile(file);
        } else {
            throw new IllegalArgumentException("仅支持 JSON 和 CSV 格式");
        }
        return ingestRecords(records);
    }

    /** 从 Map 列表导入 */
    public Map<String, Object> ingestRecords(List<Map<String, Object>> records) throws Exception {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException("数据为空");
        }

        List<Document> documents = records.stream()
                .map(this::toPropertyDocument)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("未解析到有效房产数据");
        }

        int totalBatches = (documents.size() + BATCH_SIZE - 1) / BATCH_SIZE;
        int successCount = 0;
        for (int batch = 0; batch < totalBatches; batch++) {
            int fromIdx = batch * BATCH_SIZE;
            int toIdx = Math.min(fromIdx + BATCH_SIZE, documents.size());
            List<Document> batchDocs = documents.subList(fromIdx, toIdx);

            List<String> texts = batchDocs.stream()
                    .map(Document::getContent)
                    .collect(Collectors.toList());

            List<List<Double>> embeddings = embeddingService.embedBatch(texts);
            for (int i = 0; i < batchDocs.size() && i < embeddings.size(); i++) {
                batchDocs.get(i).setEmbedding(embeddings.get(i));
            }

            vectorStore.add(batchDocs);
            successCount += batchDocs.size();
            log.info("分批导入进度: {}/{} 条", successCount, documents.size());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", documents.size());
        result.put("success", successCount);
        result.put("storedCount", vectorStore.count());
        return result;
    }

    /** 清空并重新加载 */
    public Map<String, Object> reload(List<Map<String, Object>> records) throws Exception {
        log.warn("正在清空向量存储...");
        long beforeCount = vectorStore.count();
        vectorStore.clear();
        log.info("已清空 {} 条文档，开始重新导入...", beforeCount);
        return ingestRecords(records);
    }

    /** 获取当前存储统计 */
    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocuments", vectorStore.count());
        return stats;
    }

    // ════════════════ 房产记录 → Document ════════════════

    private Document toPropertyDocument(Map<String, Object> record) {
        try {
            Document doc = new Document();

            // ── 位置层级 ──
            String city = toString(record, "city");
            String district = toString(record, "district");
            String bizcircle = toString(record, "bizcircle");
            String community = toString(record, "community");
            if (community == null || community.isEmpty()) {
                log.warn("跳过缺少 community 字段的记录");
                return null;
            }

            if (city != null) doc.withCity(city);
            if (district != null) doc.withDistrict(district);
            if (bizcircle != null) doc.withBizcircle(bizcircle);
            doc.withCommunity(community);

            // ── 规格 ──
            Integer bedrooms = toInteger(record, "bedrooms");
            Integer livingRooms = toInteger(record, "living_rooms");
            Integer bathrooms = toInteger(record, "bathrooms");
            String layout = toString(record, "layout");
            Double area = toDouble(record, "area");
            String orientation = toString(record, "orientation");
            String floorLevel = toString(record, "floor_level");
            Integer totalFloors = toInteger(record, "total_floors");
            String decoration = toString(record, "decoration");
            Integer buildYear = toInteger(record, "build_year");
            String buildingType = toString(record, "building_type");
            String buildingStructure = toString(record, "building_structure");
            Boolean hasElevator = toBoolean(record, "has_elevator");
            String propertyType = toString(record, "property_type");
            String heatingMethod = toString(record, "heating_method");
            Double greeningRate = toDouble(record, "greening_rate");
            Double propertyFee = toDouble(record, "property_fee");

            if (bedrooms != null) doc.withBedrooms(bedrooms);
            if (livingRooms != null) doc.withLivingRooms(livingRooms);
            if (bathrooms != null) doc.withBathrooms(bathrooms);
            if (layout != null) doc.withLayout(layout);
            if (area != null) doc.withArea(area);
            if (orientation != null) doc.withOrientation(orientation);
            if (floorLevel != null) doc.withFloorLevel(floorLevel);
            if (totalFloors != null) doc.withTotalFloors(totalFloors);
            if (decoration != null) doc.withDecoration(decoration);
            if (buildYear != null) doc.withBuildYear(buildYear);
            if (buildingType != null) doc.withBuildingType(buildingType);
            if (buildingStructure != null) doc.withBuildingStructure(buildingStructure);
            if (hasElevator != null) doc.withHasElevator(hasElevator);
            if (propertyType != null) doc.withPropertyType(propertyType);
            if (heatingMethod != null) doc.withHeatingMethod(heatingMethod);
            if (greeningRate != null) doc.withGreeningRate(greeningRate);
            if (propertyFee != null) doc.withPropertyFee(propertyFee);

            // ── 价格 ──
            Double totalPrice = toDouble(record, "total_price");
            Double unitPrice = toDouble(record, "unit_price");
            String listingDate = toString(record, "listing_date");

            if (totalPrice != null) doc.withTotalPrice(totalPrice);
            if (unitPrice != null) doc.withUnitPrice(unitPrice);
            if (listingDate != null) doc.withListingDate(listingDate);

            // ── 权属 ──
            Integer ownershipYears = toInteger(record, "ownership_years");
            String transactionOwnership = toString(record, "transaction_ownership");
            String householdYears = toString(record, "household_years");
            String mortgageInfo = toString(record, "mortgage_info");

            if (ownershipYears != null) doc.withOwnershipYears(ownershipYears);
            if (transactionOwnership != null) doc.withTransactionOwnership(transactionOwnership);
            if (householdYears != null) doc.withHouseholdYears(householdYears);
            if (mortgageInfo != null) doc.withMortgageInfo(mortgageInfo);

            // ── 标签与特色 ──
            String tags = toString(record, "tags");
            String features = toString(record, "features");

            if (tags != null) doc.withTags(tags);
            if (features != null) doc.withFeatures(features);

            // ── 周边 ──
            String nearbySubway = toString(record, "nearby_subway");
            String nearbySchools = toString(record, "nearby_schools");

            if (nearbySubway != null) doc.withNearbySubway(nearbySubway);
            if (nearbySchools != null) doc.withNearbySchools(nearbySchools);

            // ── 保留其他未知字段 ──
            Set<String> knownKeys = new HashSet<>(Arrays.asList(
                    "city", "district", "bizcircle", "community",
                    "bedrooms", "living_rooms", "bathrooms", "layout", "area",
                    "orientation", "floor_level", "total_floors", "decoration",
                    "build_year", "building_type", "building_structure", "has_elevator",
                    "property_type", "heating_method", "greening_rate", "property_fee",
                    "total_price", "unit_price", "listing_date",
                    "ownership_years", "transaction_ownership", "household_years", "mortgage_info",
                    "tags", "features", "nearby_subway", "nearby_schools"
            ));
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                if (!knownKeys.contains(entry.getKey()) && entry.getValue() != null) {
                    doc.getMetadata().put(entry.getKey(), entry.getValue());
                }
            }

            // ── 构建嵌入文本（按类别分层组织） ──
            doc.setContent(buildContent(doc));
            return doc;
        } catch (Exception e) {
            log.warn("解析房产记录失败: {}", record, e);
            return null;
        }
    }

    /** 按贝壳标准构建用于嵌入的富文本内容 */
    private String buildContent(Document doc) {
        StringBuilder sb = new StringBuilder();

        // 位置
        sb.append("位置:");
        appendIf(sb, doc.getCity(), null);
        appendIf(sb, doc.getDistrict(), null);
        appendIf(sb, doc.getBizcircle(), null);
        appendIf(sb, doc.getCommunity(), null);

        // 规格
        sb.append(" | 规格:");
        if (doc.getLayout() != null) sb.append(doc.getLayout());
        if (doc.getBedrooms() != null) sb.append("，").append(doc.getBedrooms()).append("室");
        if (doc.getLivingRooms() != null) sb.append(doc.getLivingRooms()).append("厅");
        if (doc.getBathrooms() != null) sb.append(doc.getBathrooms()).append("卫");
        if (doc.getArea() != null) sb.append("，").append(doc.getArea()).append("㎡");
        if (doc.getOrientation() != null) sb.append("，朝向").append(doc.getOrientation());
        if (doc.getFloorLevel() != null) {
            if (doc.getTotalFloors() != null) {
                sb.append("，").append(doc.getFloorLevel()).append("/共").append(doc.getTotalFloors()).append("层");
            } else {
                sb.append("，").append(doc.getFloorLevel());
            }
        }
        if (doc.getDecoration() != null) sb.append("，").append(doc.getDecoration());
        if (doc.getBuildYear() != null) sb.append("，").append(doc.getBuildYear()).append("年建");
        if (doc.getBuildingType() != null) sb.append("，").append(doc.getBuildingType());
        if (doc.getBuildingStructure() != null) sb.append("，").append(doc.getBuildingStructure());
        if (doc.getHasElevator() != null) {
            sb.append("，").append(doc.getHasElevator() ? "有电梯" : "无电梯");
        }
        if (doc.getHeatingMethod() != null) sb.append("，").append(doc.getHeatingMethod());
        if (doc.getGreeningRate() != null) {
            sb.append("，绿化率").append((int)(doc.getGreeningRate() * 100)).append("%");
        }
        if (doc.getPropertyFee() != null) sb.append("，物业费").append(doc.getPropertyFee()).append("元/㎡/月");

        // 价格
        sb.append(" | 价格:");
        if (doc.getTotalPrice() != null) sb.append("总价").append(doc.getTotalPrice()).append("万");
        if (doc.getUnitPrice() != null) sb.append("，单价").append(doc.getUnitPrice()).append("元/㎡");
        if (doc.getListingDate() != null) sb.append("，").append(doc.getListingDate()).append("挂牌");

        // 权属
        if (doc.getTransactionOwnership() != null || doc.getOwnershipYears() != null) {
            sb.append(" | 权属:");
            if (doc.getTransactionOwnership() != null) sb.append(doc.getTransactionOwnership());
            if (doc.getOwnershipYears() != null) sb.append("，").append(doc.getOwnershipYears()).append("年产权");
            if (doc.getHouseholdYears() != null) sb.append("，").append(doc.getHouseholdYears());
            if (doc.getMortgageInfo() != null) sb.append("，").append(doc.getMortgageInfo());
        }

        // 周边
        if (doc.getNearbySubway() != null || doc.getNearbySchools() != null) {
            sb.append(" | 周边:");
            if (doc.getNearbySubway() != null) sb.append("近地铁").append(doc.getNearbySubway());
            if (doc.getNearbySchools() != null) {
                if (doc.getNearbySubway() != null) sb.append("，");
                sb.append("学区:").append(doc.getNearbySchools());
            }
        }

        // 标签
        if (doc.getTags() != null) sb.append(" | 标签:").append(doc.getTags());
        if (doc.getFeatures() != null) sb.append(" | 特色:").append(doc.getFeatures());

        return sb.toString();
    }

    private static void appendIf(StringBuilder sb, String val, String prefix) {
        if (val != null) {
            if (prefix != null) sb.append(prefix);
            sb.append(val);
        } else if (prefix == null && sb.length() > 0 && !sb.toString().endsWith(":") && !sb.toString().endsWith(" | ")) {
            // no-op for location sequence
        }
    }

    private static void appendIf(StringBuilder sb, String val) {
        if (val != null) sb.append(val);
    }

    // ════════════════ 文件解析 ════════════════

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseJsonFile(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        Object parsed = objectMapper.readValue(bytes, Object.class);
        if (parsed instanceof List) {
            return (List<Map<String, Object>>) parsed;
        } else if (parsed instanceof Map) {
            Map<String, Object> root = (Map<String, Object>) parsed;
            for (Object val : root.values()) {
                if (val instanceof List) {
                    return (List<Map<String, Object>>) val;
                }
            }
            return Collections.singletonList(root);
        }
        throw new IllegalArgumentException("JSON 数据格式不正确，期望数组或对象");
    }

    private List<Map<String, Object>> parseCsvFile(MultipartFile file) throws Exception {
        List<Map<String, Object>> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return records;
            if (!headerLine.isEmpty() && headerLine.charAt(0) == 0xFEFF) {
                headerLine = headerLine.substring(1);
            }
            String[] headers = headerLine.split(",");
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, Object> record = new LinkedHashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    String key = headers[i];
                    String val = values[i].trim();
                    try {
                        if (val.contains(".")) {
                            record.put(key, Double.parseDouble(val));
                        } else {
                            record.put(key, Long.parseLong(val));
                        }
                    } catch (NumberFormatException e) {
                        record.put(key, val);
                    }
                }
                records.add(record);
            }
        }
        return records;
    }

    // ════════════════ 类型转换 ════════════════

    private String toString(Map<String, Object> record, String key) {
        Object val = record.get(key);
        if (val == null) return null;
        String s = val.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private Double toDouble(Map<String, Object> record, String key) {
        Object val = record.get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(val.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer toInteger(Map<String, Object> record, String key) {
        Object val = record.get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(val.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 支持多种格式：true/false、1/0、"有"/"无"、"是"/"否" */
    private Boolean toBoolean(Map<String, Object> record, String key) {
        Object val = record.get(key);
        if (val == null) return null;
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() != 0;
        String s = val.toString().trim();
        if (s.equals("1") || s.equalsIgnoreCase("true") || s.equals("有") || s.equals("是")) return true;
        if (s.equals("0") || s.equalsIgnoreCase("false") || s.equals("无") || s.equals("否")) return false;
        return null;
    }
}
