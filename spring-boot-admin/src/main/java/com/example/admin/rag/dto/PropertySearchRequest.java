package com.example.admin.rag.dto;

/**
 * 房产检索请求 DTO — 参照贝壳找房标准筛选维度。
 */
public class PropertySearchRequest {

    /** 语义查询文本 */
    private String query;

    // ── 位置层级 ──
    private String city;
    private String district;
    private String bizcircle;
    private String community;

    // ── 规格筛选 ──
    private String layout;
    private Integer bedrooms;       // 精确室数（QueryParser 自动提取）
    private Integer livingRooms;    // 精确厅数（QueryParser 自动提取）
    private Integer bathrooms;      // 精确卫数（QueryParser 自动提取）
    private Integer minBedrooms;
    private Integer maxBedrooms;
    private Double minArea;
    private Double maxArea;
    private String orientation;
    private String floorLevel;
    private String decoration;
    private Integer minBuildYear;
    private Integer maxBuildYear;
    private String buildingType;
    private Boolean hasElevator;

    // ── 价格筛选 ──
    private Double minPrice;
    private Double maxPrice;
    private Double minUnitPrice;
    private Double maxUnitPrice;

    // ── 权属筛选 ──
    private String transactionOwnership;
    private String householdYears;

    // ── 标签筛选 ──
    private String tags;

    private int topK = 10;

    // ════════════════ Getters / Setters ════════════════

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getBizcircle() { return bizcircle; }
    public void setBizcircle(String bizcircle) { this.bizcircle = bizcircle; }

    public String getCommunity() { return community; }
    public void setCommunity(String community) { this.community = community; }

    public String getLayout() { return layout; }
    public void setLayout(String layout) { this.layout = layout; }

    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }

    public Integer getLivingRooms() { return livingRooms; }
    public void setLivingRooms(Integer livingRooms) { this.livingRooms = livingRooms; }

    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }

    public Integer getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(Integer minBedrooms) { this.minBedrooms = minBedrooms; }

    public Integer getMaxBedrooms() { return maxBedrooms; }
    public void setMaxBedrooms(Integer maxBedrooms) { this.maxBedrooms = maxBedrooms; }

    public Double getMinArea() { return minArea; }
    public void setMinArea(Double minArea) { this.minArea = minArea; }

    public Double getMaxArea() { return maxArea; }
    public void setMaxArea(Double maxArea) { this.maxArea = maxArea; }

    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) { this.orientation = orientation; }

    public String getFloorLevel() { return floorLevel; }
    public void setFloorLevel(String floorLevel) { this.floorLevel = floorLevel; }

    public String getDecoration() { return decoration; }
    public void setDecoration(String decoration) { this.decoration = decoration; }

    public Integer getMinBuildYear() { return minBuildYear; }
    public void setMinBuildYear(Integer minBuildYear) { this.minBuildYear = minBuildYear; }

    public Integer getMaxBuildYear() { return maxBuildYear; }
    public void setMaxBuildYear(Integer maxBuildYear) { this.maxBuildYear = maxBuildYear; }

    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }

    public Boolean getHasElevator() { return hasElevator; }
    public void setHasElevator(Boolean hasElevator) { this.hasElevator = hasElevator; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Double getMinUnitPrice() { return minUnitPrice; }
    public void setMinUnitPrice(Double minUnitPrice) { this.minUnitPrice = minUnitPrice; }

    public Double getMaxUnitPrice() { return maxUnitPrice; }
    public void setMaxUnitPrice(Double maxUnitPrice) { this.maxUnitPrice = maxUnitPrice; }

    public String getTransactionOwnership() { return transactionOwnership; }
    public void setTransactionOwnership(String transactionOwnership) { this.transactionOwnership = transactionOwnership; }

    public String getHouseholdYears() { return householdYears; }
    public void setHouseholdYears(String householdYears) { this.householdYears = householdYears; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
}
