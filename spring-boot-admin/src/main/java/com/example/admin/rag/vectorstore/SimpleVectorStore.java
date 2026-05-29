package com.example.admin.rag.vectorstore;

import com.example.admin.rag.model.Document;
import com.example.admin.rag.model.FilterExpression;
import com.example.admin.rag.model.SearchRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 基于内存 + JSON 文件持久化的向量存储实现。
 * <p>
 * 仅用于 {@code @Profile("dev")} 开发环境。生产环境切换 PgVectorStore。
 * 【重要】业务代码禁止直接注入此实现，必须通过 {@link VectorStore} 接口引用。
 * </p>
 */
public class SimpleVectorStore implements VectorStore {

    private static final Logger log = LoggerFactory.getLogger(SimpleVectorStore.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String storeFilePath;
    private final Map<String, Document> documents = new ConcurrentHashMap<>();
    private final List<Document> documentList = new CopyOnWriteArrayList<>();

    public SimpleVectorStore(String storeFilePath) {
        this.storeFilePath = storeFilePath;
        loadFromDisk();
    }

    @Override
    public void add(List<Document> documents) {
        if (documents == null || documents.isEmpty()) return;
        for (Document doc : documents) {
            if (doc.getId() == null) {
                doc.setId(UUID.randomUUID().toString());
            }
            this.documents.put(doc.getId(), doc);
        }
        this.documentList.clear();
        this.documentList.addAll(this.documents.values());
        persistToDisk();
    }

    @Override
    public void add(Document document) {
        add(Collections.singletonList(document));
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        if (request.getQueryEmbedding() == null || request.getQueryEmbedding().isEmpty()) {
            return Collections.emptyList();
        }
        List<Double> queryVec = request.getQueryEmbedding();
        double threshold = request.getSimilarityThreshold();
        int topK = request.getTopK();
        FilterExpression filter = request.getFilterExpression();

        // 计算余弦相似度
        List<ScoredDoc> scored = new ArrayList<>();
        for (Document doc : documentList) {
            if (doc.getEmbedding() == null || doc.getEmbedding().isEmpty()) continue;
            double sim = cosineSimilarity(queryVec, doc.getEmbedding());
            if (sim >= threshold) {
                // 内存后置过滤器评估
                if (filter != null && !filter.matches(doc)) continue;
                scored.add(new ScoredDoc(doc, sim));
            }
        }

        // 按相似度降序排序
        scored.sort((a, b) -> Double.compare(b.score, a.score));

        return scored.stream()
                .limit(topK)
                .map(s -> s.doc)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Document> get(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    @Override
    public void delete(List<String> idList) {
        if (idList == null || idList.isEmpty()) return;
        for (String id : idList) {
            documents.remove(id);
        }
        documentList.clear();
        documentList.addAll(documents.values());
        persistToDisk();
    }

    @Override
    public long count() {
        return documents.size();
    }

    @Override
    public void clear() {
        documents.clear();
        documentList.clear();
        persistToDisk();
    }

    // ──────────────── 持久化 ────────────────

    private void loadFromDisk() {
        try {
            Path path = Paths.get(storeFilePath);
            if (!Files.exists(path)) {
                log.info("向量存储文件不存在，创建新文件: {}", storeFilePath);
                ensureParentDir(path);
                return;
            }
            List<Map<String, Object>> list;
            try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(path.toFile()), StandardCharsets.UTF_8)) {
                list = objectMapper.readValue(reader,
                        new TypeReference<List<Map<String, Object>>>() {});
            }
            for (Map<String, Object> map : list) {
                Document doc = Document.fromJsonMap(map);
                documents.put(doc.getId(), doc);
            }
            documentList.clear();
            documentList.addAll(documents.values());
            log.info("从磁盘加载了 {} 条向量文档", documents.size());
        } catch (IOException e) {
            log.error("加载向量存储文件失败: {}", storeFilePath, e);
        }
    }

    private synchronized void persistToDisk() {
        try {
            Path path = Paths.get(storeFilePath);
            ensureParentDir(path);
            List<Map<String, Object>> list = documents.values().stream()
                    .map(Document::toJsonMap)
                    .collect(Collectors.toList());
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(path.toFile()), StandardCharsets.UTF_8)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, list);
            }
        } catch (IOException e) {
            log.error("持久化向量存储失败: {}", storeFilePath, e);
        }
    }

    private void ensureParentDir(Path path) {
        File parent = path.getParent().toFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    // ──────────────── 余弦相似度 ────────────────

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) return 0.0;
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static class ScoredDoc {
        final Document doc;
        final double score;
        ScoredDoc(Document doc, double score) { this.doc = doc; this.score = score; }
    }
}
