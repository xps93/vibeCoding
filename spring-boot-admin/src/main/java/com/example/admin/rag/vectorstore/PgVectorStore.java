package com.example.admin.rag.vectorstore;

import com.example.admin.rag.model.Document;
import com.example.admin.rag.model.SearchRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * PgVector 向量存储占位实现。
 * <p>
 * 当前仅为接口占位，生产环境就绪后实现以下逻辑：
 * <ul>
 *   <li>通过 JdbcTemplate 操作 {@code vector_store} 表</li>
 *   <li>{@code add()} → INSERT INTO vector_store(id, content, metadata, embedding) VALUES(?, ?, ?, ?::vector)</li>
 *   <li>{@code similaritySearch()} → SELECT *, embedding <=> ?::vector AS distance ORDER BY distance LIMIT ?</li>
 *   <li>FilterExpression → SQL WHERE 子句</li>
 * </ul>
 * </p>
 *
 * @deprecated 此实现尚未完成，当前仅供类型占位。生产环境使用前需完成上述 SQL 逻辑。
 */
@Deprecated
public class PgVectorStore implements VectorStore {

    @Override
    public void add(List<Document> documents) {
        throw new UnsupportedOperationException("PgVectorStore 尚未实现，请使用 SimpleVectorStore (dev profile)");
    }

    @Override
    public void add(Document document) {
        throw new UnsupportedOperationException("PgVectorStore 尚未实现");
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        return Collections.emptyList();
    }

    @Override
    public Optional<Document> get(String id) {
        return Optional.empty();
    }

    @Override
    public void delete(List<String> idList) {
        throw new UnsupportedOperationException("PgVectorStore 尚未实现");
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("PgVectorStore 尚未实现");
    }
}
