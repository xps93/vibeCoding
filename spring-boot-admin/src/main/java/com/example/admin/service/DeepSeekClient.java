package com.example.admin.service;

import okhttp3.*;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek API 客户端，支持流式聊天
 */
@Service
public class DeepSeekClient {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String apiKey;
    private final String baseUrl;
    private final String defaultModel;

    public DeepSeekClient(@Value("${deepseek.api-key}") String apiKey,
                          @Value("${deepseek.base-url}") String baseUrl,
                          @Value("${deepseek.default-model}") String defaultModel) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.defaultModel = defaultModel;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 流式聊天，通过回调返回每段内容
     */
    public void streamChat(List<Map<String, String>> messages, String model,
                           Double temperature, Integer maxTokens,
                           StreamCallback callback) {
        String modelName = model != null && !model.isEmpty() ? model : defaultModel;
        double temp = temperature != null ? temperature : 0.7;
        int tokens = maxTokens != null ? maxTokens : 2048;

        StringBuilder json = new StringBuilder();
        json.append("{\"model\":\"").append(modelName).append("\"");
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
                .url(baseUrl + "/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(json.toString(), JSON))
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                log.error("DeepSeek API error: {} {}", response.code(), errorBody);
                callback.onError("DeepSeek API返回错误: " + response.code());
                return;
            }

            ResponseBody body = response.body();
            if (body == null) {
                callback.onError("响应体为空");
                return;
            }

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
                    // 简单解析 OpenAI 格式的JSON，提取 content
                    String content = extractContent(data);
                    if (content != null && !content.isEmpty()) {
                        callback.onContent(content);
                    }
                } catch (Exception e) {
                    log.warn("解析SSE数据失败: {}", data);
                }
            }
            callback.onDone();
        } catch (IOException e) {
            log.error("DeepSeek API 请求异常", e);
            callback.onError("网络请求失败: " + e.getMessage());
        }
    }

    /**
     * 从OpenAI格式的SSE数据中提取content
     */
    private String extractContent(String json) {
        int deltaIdx = json.indexOf("\"delta\"");
        if (deltaIdx < 0) return null;
        int contentIdx = json.indexOf("\"content\"", deltaIdx);
        if (contentIdx < 0) return null;
        int colonIdx = json.indexOf(":", contentIdx);
        if (colonIdx < 0) return null;
        // 跳过冒号后的空白
        int valStart = colonIdx + 1;
        while (valStart < json.length() && (json.charAt(valStart) == ' ' || json.charAt(valStart) == '\t')) {
            valStart++;
        }
        // content 值为 null 时不处理（reasoning_content 推理内容跳过）
        if (valStart >= json.length() || json.charAt(valStart) != '"') return null;
        int startQuote = valStart;
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote < 0) return null;
        String raw = json.substring(startQuote + 1, endQuote);
        // 空字符串不处理
        if (raw.isEmpty()) return null;
        return unescapeJson(raw);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    /**
     * 流式回调接口
     */
    public interface StreamCallback {
        void onContent(String content);
        void onDone();
        void onError(String error);
    }
}
