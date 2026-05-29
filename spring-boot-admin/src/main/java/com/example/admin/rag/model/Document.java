package com.example.admin.rag.model;

import java.util.*;

/**
 * 向量文档模型，对齐 Spring AI {@code org.springframework.ai.document.Document}。
 * <p>
 * Metadata 使用蛇形命名法（snake_case），数值类型保持 Number，严禁序列化为 String。
 * 字段体系参照贝壳找房标准房源数据模型。
 * </p>
 */
public class Document {

    private String id;
    private String content;
    private Map<String, Object> metadata;
    private List<Double> embedding;

    public Document() {
        this.id = UUID.randomUUID().toString();
        this.metadata = new HashMap<>();
        this.embedding = new ArrayList<>();
    }

    public Document(String content) {
        this();
        this.content = content;
    }

    public Document(String content, Map<String, Object> metadata) {
        this();
        this.content = content;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    // ════════════════ 位置层级 ════════════════

    public Document withCity(String city) {
        this.metadata.put("city", city);
        return this;
    }

    public Document withDistrict(String district) {
        this.metadata.put("district", district);
        return this;
    }

    public Document withBizcircle(String bizcircle) {
        this.metadata.put("bizcircle", bizcircle);
        return this;
    }

    public Document withCommunity(String community) {
        this.metadata.put("community", community);
        return this;
    }

    // ════════════════ 规格信息 ════════════════

    public Document withBedrooms(Integer bedrooms) {
        this.metadata.put("bedrooms", bedrooms);
        return this;
    }

    public Document withLivingRooms(Integer livingRooms) {
        this.metadata.put("living_rooms", livingRooms);
        return this;
    }

    public Document withBathrooms(Integer bathrooms) {
        this.metadata.put("bathrooms", bathrooms);
        return this;
    }

    public Document withLayout(String layout) {
        this.metadata.put("layout", layout);
        return this;
    }

    public Document withArea(Double area) {
        this.metadata.put("area", area);
        return this;
    }

    public Document withOrientation(String orientation) {
        this.metadata.put("orientation", orientation);
        return this;
    }

    public Document withFloorLevel(String floorLevel) {
        this.metadata.put("floor_level", floorLevel);
        return this;
    }

    public Document withTotalFloors(Integer totalFloors) {
        this.metadata.put("total_floors", totalFloors);
        return this;
    }

    public Document withDecoration(String decoration) {
        this.metadata.put("decoration", decoration);
        return this;
    }

    public Document withBuildYear(Integer buildYear) {
        this.metadata.put("build_year", buildYear);
        return this;
    }

    public Document withBuildingType(String buildingType) {
        this.metadata.put("building_type", buildingType);
        return this;
    }

    public Document withBuildingStructure(String buildingStructure) {
        this.metadata.put("building_structure", buildingStructure);
        return this;
    }

    public Document withHasElevator(Boolean hasElevator) {
        this.metadata.put("has_elevator", hasElevator);
        return this;
    }

    public Document withPropertyType(String propertyType) {
        this.metadata.put("property_type", propertyType);
        return this;
    }

    public Document withHeatingMethod(String heatingMethod) {
        this.metadata.put("heating_method", heatingMethod);
        return this;
    }

    public Document withGreeningRate(Double greeningRate) {
        this.metadata.put("greening_rate", greeningRate);
        return this;
    }

    public Document withPropertyFee(Double propertyFee) {
        this.metadata.put("property_fee", propertyFee);
        return this;
    }

    // ════════════════ 价格信息 ════════════════

    public Document withTotalPrice(Double totalPrice) {
        this.metadata.put("total_price", totalPrice);
        return this;
    }

    public Document withUnitPrice(Double unitPrice) {
        this.metadata.put("unit_price", unitPrice);
        return this;
    }

    public Document withListingDate(String listingDate) {
        this.metadata.put("listing_date", listingDate);
        return this;
    }

    // ════════════════ 权属信息 ════════════════

    public Document withOwnershipYears(Integer ownershipYears) {
        this.metadata.put("ownership_years", ownershipYears);
        return this;
    }

    public Document withTransactionOwnership(String transactionOwnership) {
        this.metadata.put("transaction_ownership", transactionOwnership);
        return this;
    }

    public Document withHouseholdYears(String householdYears) {
        this.metadata.put("household_years", householdYears);
        return this;
    }

    public Document withMortgageInfo(String mortgageInfo) {
        this.metadata.put("mortgage_info", mortgageInfo);
        return this;
    }

    // ════════════════ 标签与特色 ════════════════

    public Document withTags(String tags) {
        this.metadata.put("tags", tags);
        return this;
    }

    public Document withFeatures(String features) {
        this.metadata.put("features", features);
        return this;
    }

    // ════════════════ 周边设施 ════════════════

    public Document withNearbySubway(String nearbySubway) {
        this.metadata.put("nearby_subway", nearbySubway);
        return this;
    }

    public Document withNearbySchools(String nearbySchools) {
        this.metadata.put("nearby_schools", nearbySchools);
        return this;
    }

    // ════════════════ Metadata 读取 ════════════════

    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        Object val = metadata.get(key);
        if (val == null) return null;
        return (T) val;
    }

    // 位置
    public String getCity() { return getMetadata("city", String.class); }
    public String getDistrict() { return getMetadata("district", String.class); }
    public String getBizcircle() { return getMetadata("bizcircle", String.class); }
    public String getCommunity() { return getMetadata("community", String.class); }

    // 规格
    public Integer getBedrooms() { return getMetadata("bedrooms", Integer.class); }
    public Integer getLivingRooms() { return getMetadata("living_rooms", Integer.class); }
    public Integer getBathrooms() { return getMetadata("bathrooms", Integer.class); }
    public String getLayout() { return getMetadata("layout", String.class); }
    public Double getArea() { return getMetadata("area", Double.class); }
    public String getOrientation() { return getMetadata("orientation", String.class); }
    public String getFloorLevel() { return getMetadata("floor_level", String.class); }
    public Integer getTotalFloors() { return getMetadata("total_floors", Integer.class); }
    public String getDecoration() { return getMetadata("decoration", String.class); }
    public Integer getBuildYear() { return getMetadata("build_year", Integer.class); }
    public String getBuildingType() { return getMetadata("building_type", String.class); }
    public String getBuildingStructure() { return getMetadata("building_structure", String.class); }
    public Boolean getHasElevator() { return getMetadata("has_elevator", Boolean.class); }
    public String getPropertyType() { return getMetadata("property_type", String.class); }
    public String getHeatingMethod() { return getMetadata("heating_method", String.class); }
    public Double getGreeningRate() { return getMetadata("greening_rate", Double.class); }
    public Double getPropertyFee() { return getMetadata("property_fee", Double.class); }

    // 价格
    public Double getTotalPrice() { return getMetadata("total_price", Double.class); }
    public Double getUnitPrice() { return getMetadata("unit_price", Double.class); }
    public String getListingDate() { return getMetadata("listing_date", String.class); }

    // 权属
    public Integer getOwnershipYears() { return getMetadata("ownership_years", Integer.class); }
    public String getTransactionOwnership() { return getMetadata("transaction_ownership", String.class); }
    public String getHouseholdYears() { return getMetadata("household_years", String.class); }
    public String getMortgageInfo() { return getMetadata("mortgage_info", String.class); }

    // 标签
    public String getTags() { return getMetadata("tags", String.class); }
    public String getFeatures() { return getMetadata("features", String.class); }

    // 周边
    public String getNearbySubway() { return getMetadata("nearby_subway", String.class); }
    public String getNearbySchools() { return getMetadata("nearby_schools", String.class); }

    // ════════════════ Getters / Setters ════════════════

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public List<Double> getEmbedding() { return embedding; }
    public void setEmbedding(List<Double> embedding) { this.embedding = embedding; }

    // ════════════════ 序列化辅助 ════════════════

    /** 转为可JSON序列化的Map */
    public Map<String, Object> toJsonMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("content", content);
        map.put("metadata", metadata);
        map.put("embedding", embedding);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Document fromJsonMap(Map<String, Object> map) {
        Document doc = new Document();
        doc.id = (String) map.getOrDefault("id", UUID.randomUUID().toString());
        doc.content = (String) map.getOrDefault("content", "");
        doc.metadata = (Map<String, Object>) map.getOrDefault("metadata", new HashMap<>());

        if (doc.metadata != null) {
            // 数值类型修复：JSON反序列化后Integer转目标类型
            doc.metadata = fixNumericType(doc.metadata, "total_price");
            doc.metadata = fixNumericType(doc.metadata, "unit_price");
            doc.metadata = fixNumericType(doc.metadata, "area");
            doc.metadata = fixNumericType(doc.metadata, "greening_rate");
            doc.metadata = fixNumericType(doc.metadata, "property_fee");
            doc.metadata = fixIntegerType(doc.metadata, "bedrooms");
            doc.metadata = fixIntegerType(doc.metadata, "living_rooms");
            doc.metadata = fixIntegerType(doc.metadata, "bathrooms");
            doc.metadata = fixIntegerType(doc.metadata, "total_floors");
            doc.metadata = fixIntegerType(doc.metadata, "build_year");
            doc.metadata = fixIntegerType(doc.metadata, "ownership_years");
        }

        Object embRaw = map.get("embedding");
        if (embRaw instanceof List) {
            doc.embedding = new ArrayList<>();
            for (Object v : (List<?>) embRaw) {
                if (v instanceof Double) doc.embedding.add((Double) v);
                else if (v instanceof Number) doc.embedding.add(((Number) v).doubleValue());
            }
        }
        return doc;
    }

    private static Map<String, Object> fixNumericType(Map<String, Object> meta, String key) {
        Object val = meta.get(key);
        if (val instanceof Integer) meta.put(key, ((Integer) val).doubleValue());
        else if (val instanceof Long) meta.put(key, ((Long) val).doubleValue());
        return meta;
    }

    private static Map<String, Object> fixIntegerType(Map<String, Object> meta, String key) {
        Object val = meta.get(key);
        if (val instanceof Long) meta.put(key, ((Long) val).intValue());
        else if (val instanceof Double) meta.put(key, ((Double) val).intValue());
        return meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        return Objects.equals(id, ((Document) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
