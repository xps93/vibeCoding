package com.example.admin.rag;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 向量嵌入服务。优先调用 DeepSeek Embedding API，失败时降级到本地哈希向量。
 * <p>
 * 降级方案（本地哈希）用于开发环境，切换 PgVector + 正式 Embedding API 后自动走 API 路径。
 * </p>
 */
@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** 向量维度，统一使用 256 维本地哈希（DeepSeek Embedding API 尚不稳定） */
    private static final int EMBEDDING_DIM = 256;
    private static final int FALLBACK_DIM = 256;

    private final OkHttpClient httpClient;
    private volatile boolean apiAvailable = false;

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    public EmbeddingService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /** 生成单条文本的向量 */
    public List<Double> embed(String text) {
        List<List<Double>> result = embedBatch(Collections.singletonList(text));
        return result.isEmpty() ? fallbackEmbed(text, FALLBACK_DIM) : result.get(0);
    }

    /** 批量生成向量 */
    public List<List<Double>> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) return Collections.emptyList();

        if (apiAvailable) {
            List<List<Double>> result = callEmbeddingApi(texts);
            if (!result.isEmpty()) return result;
            // API 失败一次后降级
            apiAvailable = false;
            log.warn("Embedding API 不可用，切换到本地哈希向量降级模式");
        }

        // 降级：本地特征哈希
        List<List<Double>> fallback = new ArrayList<>();
        for (String text : texts) {
            fallback.add(fallbackEmbed(text, FALLBACK_DIM));
        }
        return fallback;
    }

    // ──────────────── API 调用 ────────────────

    private List<List<Double>> callEmbeddingApi(List<String> texts) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", "deepseek-embedding");
            body.put("input", texts);
            body.put("encoding_format", "float");
            String json = objectMapper.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(baseUrl + "/v1/embeddings")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Embedding API 返回错误: {} {}", response.code(),
                            response.body() != null ? response.body().string() : "");
                    return Collections.emptyList();
                }

                String respBody = response.body() != null ? response.body().string() : "{}";
                @SuppressWarnings("unchecked")
                Map<String, Object> result = objectMapper.readValue(respBody, Map.class);
                List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");
                if (data == null) return Collections.emptyList();

                data.sort(Comparator.comparingInt(m -> ((Number) m.get("index")).intValue()));

                List<List<Double>> embeddings = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    @SuppressWarnings("unchecked")
                    List<Object> raw = (List<Object>) item.get("embedding");
                    if (raw != null) {
                        List<Double> vec = new ArrayList<>(raw.size());
                        for (Object v : raw) {
                            vec.add(((Number) v).doubleValue());
                        }
                        embeddings.add(vec);
                    }
                }
                if (!embeddings.isEmpty()) {
                    log.info("Embedding API 成功生成 {} 条向量", embeddings.size());
                }
                return embeddings;
            }
        } catch (IOException e) {
            log.warn("Embedding API 请求异常: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ──────────────── 降级：本地特征哈希 ────────────────

    /**
     * 基于字符 N-gram 特征哈希生成固定维度向量。
     * 原理：将文本拆解为 unigram + bigram，通过哈希函数映射到固定维度空间，
     * 保留词语/字符层面的相似度信号，适合开发期 RAG 原型验证。
     * 正式环境切换 PgVector + 真实 Embedding API 后，此方法不再调用。
     */
    private List<Double> fallbackEmbed(String text, int dim) {
        if (text == null || text.isEmpty()) {
            return zeroVector(dim);
        }

        double[] vec = new double[dim];

        // Unigram: 单字符
        for (int i = 0; i < text.length(); i++) {
            String gram = text.substring(i, i + 1);
            int idx = hashToIndex(gram, dim);
            vec[idx] += 1.0;
        }

        // Bigram: 双字符窗口
        for (int i = 0; i < text.length() - 1; i++) {
            String gram = text.substring(i, i + 2);
            int idx = hashToIndex(gram, dim);
            vec[idx] += 0.5;
        }

        // L2 归一化
        double norm = 0.0;
        for (double v : vec) norm += v * v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < dim; i++) vec[i] /= norm;
        }

        List<Double> result = new ArrayList<>(dim);
        for (double v : vec) result.add(v);
        return result;
    }

    private int hashToIndex(String s, int mod) {
        // FNV-1a 哈希
        long hash = 0xcbf29ce484222325L;
        for (int i = 0; i < s.length(); i++) {
            hash ^= s.charAt(i);
            hash *= 0x100000001b3L;
        }
        return (int) (Math.abs(hash) % mod);
    }

    private List<Double> zeroVector(int dim) {
        List<Double> vec = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) vec.add(0.0);
        return vec;
    }
}
