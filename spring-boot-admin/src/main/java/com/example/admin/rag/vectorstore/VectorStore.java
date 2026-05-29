package com.example.admin.rag.vectorstore;

import com.example.admin.rag.model.Document;
import com.example.admin.rag.model.SearchRequest;

import java.util.List;
import java.util.Optional;

/**
 * 向量存储抽象接口，与 Spring AI {@code org.springframework.ai.vectorstore.VectorStore} 签名对齐。
 * <p>
 * 【绝对红线】所有 Service / Controller 只能注入此接口，禁止依赖 SimpleVectorStore 等具体实现。
 * 切换 PgVector 时仅需修改 Bean 配置，业务代码零改动。
 * </p>
 */
public interface VectorStore {

    /** 批量写入文档（含向量） */
    void add(List<Document> documents);

    /** 单条写入 */
    void add(Document document);

    /** 向量相似度搜索 */
    List<Document> similaritySearch(SearchRequest request);

    /** 按ID查询 */
    Optional<Document> get(String id);

    /** 按ID删除 */
    void delete(List<String> idList);

    /** 获取文档总数 */
    long count();

    /** 清空所有文档 */
    void clear();
}
