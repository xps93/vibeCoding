package com.example.admin.service.model;

import okhttp3.*;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OpenAI兼容API的抽象提供者（DeepSeek、Qwen、Zhipu等均兼容此格式）
 */
public abstract class AbstractOpenAIProvider implements ModelProvider {

    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final OkHttpClient httpClient;

    public AbstractOpenAIProvider() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    protected abstract String getApiKey();
    protected abstract String getBaseUrl();

    @Override
    public void streamChat(List<Map<String, String>> messages, String model,
                           Double temperature, Integer maxTokens,
                           StreamCallback callback) {
        double temp = temperature != null ? temperature : 0.7;
        int tokens = maxTokens != null ? maxTokens : 2048;

        StringBuilder json = new StringBuilder();
        json.append("{\"model\":\"").append(model).append("\"");
        json.append(",\"messages\":[");
        for (int i = 0; i < messages.size(); i++) {
            Map<String, String> msg = messages.get(i);
            if (i > 0) json.append(",");
            json.append("{\"role\":\"").append(msg.get("role"))
                .append("\",\"content\":\"").append(escapeJson(msg.get("content"))).append("\"}");
        }
        json.append("],\"stream\":true");
        json.append(",\"temperature\":").append(temp);
        json.append(",\"max_tokens\":").append(tokens);
        json.append("}");

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v1/chat/completions")
                .header("Authorization", "Bearer " + getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(json.toString(), JSON))
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                log.error("{} API error: {} {}", getProviderName(), response.code(), errorBody);
                callback.onError(getProviderName() + " API返回错误: " + response.code());
                return;
            }

            ResponseBody body = response.body();
            if (body == null) {
                callback.onError("响应体为空");
                return;
            }

            readStream(body, callback);
        } catch (IOException e) {
            log.error("{} API 请求异常", getProviderName(), e);
            callback.onError("网络请求失败: " + e.getMessage());
        }
    }

    protected void readStream(ResponseBody body, StreamCallback callback) throws IOException {
        BufferedSource source = body.source();
        String line;
        while ((line = source.readUtf8Line()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            if (!trimmed.startsWith("data:")) continue;

            String data = trimmed.substring(5).trim();
            if ("[DONE]".equals(data)) {
                callback.onDone();
                return;
            }

            try {
                String content = extractDeltaField(data, "content");
                if (content != null && !content.isEmpty()) {
                    callback.onContent(content);
                }
                String reasoning = extractDeltaField(data, "reasoning_content");
                if (reasoning != null && !reasoning.isEmpty()) {
                    callback.onReasoningContent(reasoning);
                }
            } catch (Exception e) {
                log.warn("解析SSE数据失败: {}", data);
            }
        }
        callback.onDone();
    }

    /**
     * 从OpenAI格式delta对象中提取指定字段
     */
    protected String extractDeltaField(String json, String fieldName) {
        int deltaIdx = json.indexOf("\"delta\"");
        if (deltaIdx < 0) return null;
        int fieldIdx = json.indexOf("\"" + fieldName + "\"", deltaIdx);
        if (fieldIdx < 0) return null;
        int colonIdx = json.indexOf(":", fieldIdx);
        if (colonIdx < 0) return null;
        int valStart = colonIdx + 1;
        while (valStart < json.length() && (json.charAt(valStart) == ' ' || json.charAt(valStart) == '\t')) {
            valStart++;
        }
        if (valStart >= json.length() || json.charAt(valStart) != '"') return null;
        int startQuote = valStart;
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote < 0) return null;
        String raw = json.substring(startQuote + 1, endQuote);
        if (raw.isEmpty()) return null;
        return unescapeJson(raw);
    }

    protected String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    protected String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
