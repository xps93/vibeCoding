package com.example.admin.rag.dto;

import java.util.List;
import java.util.Map;

/**
 * 房产检索结果 DTO（业务层出参，隔离 Spring AI Document 泄漏到 Controller）
 */
public class PropertySearchResult {

    private String query;
    private int matchCount;
    private long vectorStoreSize;
    private List<PropertyItem> items;

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }
    public long getVectorStoreSize() { return vectorStoreSize; }
    public void setVectorStoreSize(long vectorStoreSize) { this.vectorStoreSize = vectorStoreSize; }
    public List<PropertyItem> getItems() { return items; }
    public void setItems(List<PropertyItem> items) { this.items = items; }

    /**
     * 单条房产检索结果项
     */
    public static class PropertyItem {
        private String id;
        private String content;
        private Map<String, Object> metadata;
        private double similarity;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public double getSimilarity() { return similarity; }
        public void setSimilarity(double similarity) { this.similarity = similarity; }
    }
}
