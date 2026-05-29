package com.example.admin.rag.model;

import java.util.List;

/**
 * 向量搜索请求，对齐 Spring AI {@code SearchRequest}。
 */
public class SearchRequest {

    private String query;
    private List<Double> queryEmbedding;
    private int topK = 10;
    private double similarityThreshold = 0.6;
    private FilterExpression filterExpression;

    public SearchRequest() {}

    public SearchRequest(List<Double> queryEmbedding, int topK) {
        this.queryEmbedding = queryEmbedding;
        this.topK = topK;
    }

    public static SearchRequest withEmbedding(List<Double> embedding) {
        SearchRequest req = new SearchRequest();
        req.queryEmbedding = embedding;
        return req;
    }

    public SearchRequest withTopK(int topK) {
        this.topK = topK;
        return this;
    }

    public SearchRequest withSimilarityThreshold(double threshold) {
        this.similarityThreshold = threshold;
        return this;
    }

    public SearchRequest withFilter(FilterExpression expr) {
        this.filterExpression = expr;
        return this;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public List<Double> getQueryEmbedding() { return queryEmbedding; }
    public void setQueryEmbedding(List<Double> queryEmbedding) { this.queryEmbedding = queryEmbedding; }
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
    public double getSimilarityThreshold() { return similarityThreshold; }
    public void setSimilarityThreshold(double similarityThreshold) { this.similarityThreshold = similarityThreshold; }
    public FilterExpression getFilterExpression() { return filterExpression; }
    public void setFilterExpression(FilterExpression filterExpression) { this.filterExpression = filterExpression; }
}
