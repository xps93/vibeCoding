package com.example.admin.service.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    protected final ObjectMapper objectMapper = new ObjectMapper();

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
        String jsonBody = buildRequestBody(messages, model, temperature, maxTokens, true);

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v1/chat/completions")
                .header("Authorization", "Bearer " + getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, JSON))
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

    @Override
    public String chatSync(List<Map<String, String>> messages, String model,
                           Double temperature, Integer maxTokens) {
        String jsonBody = buildRequestBody(messages, model, temperature, maxTokens, false);

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/v1/chat/completions")
                .header("Authorization", "Bearer " + getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                log.error("{} sync API error: {} {}", getProviderName(), response.code(), errorBody);
                throw new RuntimeException(getProviderName() + " API返回错误: " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new RuntimeException("响应体为空");
            }

            String respBody = body.string();
            return extractMessageContent(respBody);
        } catch (IOException e) {
            log.error("{} sync API 请求异常", getProviderName(), e);
            throw new RuntimeException("同步调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建 OpenAI 兼容的请求体 JSON。
     */
    protected String buildRequestBody(List<Map<String, String>> messages, String model,
                                      Double temperature, Integer maxTokens, boolean stream) {
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
        json.append("],\"stream\":").append(stream);
        json.append(",\"temperature\":").append(temp);
        json.append(",\"max_tokens\":").append(tokens);
        json.append("}");

        return json.toString();
    }

    /**
     * 从非流式响应中提取 choices[0].message.content，使用 Jackson 正确解析 JSON。
     */
    protected String extractMessageContent(String respBody) {
        try {
            JsonNode root = objectMapper.readTree(respBody);
            JsonNode choices = root.get("choices");
            if (choices == null || !choices.isArray() || choices.size() == 0) {
                throw new RuntimeException("响应中无choices字段: " + respBody);
            }
            JsonNode message = choices.get(0).get("message");
            if (message == null) {
                throw new RuntimeException("响应中无message字段");
            }
            JsonNode content = message.get("content");
            if (content == null) {
                return "";
            }
            return content.asText();
        } catch (IOException e) {
            throw new RuntimeException("解析响应JSON失败: " + e.getMessage(), e);
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
