package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.rag.QueryAnalysisService;
import com.example.admin.rag.dto.PropertySearchRequest;
import com.example.admin.rag.dto.PropertySearchResult;
import com.example.admin.service.PropertyIngestionService;
import com.example.admin.service.PropertyRetrievalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 房产 RAG 数据导入与检索 API。
 */
@Tag(name = "房产RAG", description = "房产向量检索：数据导入、检索查询")
@RestController
@RequestMapping("/api/ai/rag")
public class PropertyRagController {

    private static final Logger log = LoggerFactory.getLogger(PropertyRagController.class);

    @Autowired
    private PropertyIngestionService ingestionService;

    @Autowired
    private PropertyRetrievalService retrievalService;

    @Autowired
    private QueryAnalysisService queryAnalysisService;

    /** 上传 JSON/CSV 房产数据文件进行导入 */
    @Operation(summary = "上传房产数据文件并导入向量存储")
    @PostMapping("/ingest/file")
    public Result ingestFile(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = ingestionService.ingestFile(file);
            return Result.success("导入完成", result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("房产数据导入失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /** 直接提交 JSON 数据导入 */
    @Operation(summary = "提交房产数据JSON数组直接导入")
    @PostMapping("/ingest/json")
    public Result ingestJson(@RequestBody List<Map<String, Object>> records) {
        try {
            Map<String, Object> result = ingestionService.ingestRecords(records);
            return Result.success("导入完成", result);
        } catch (Exception e) {
            log.error("房产数据导入失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /** 清空并重新导入（开发期手动触发） */
    @Operation(summary = "清空并重新导入房产数据")
    @PostMapping("/reload")
    public Result reload(@RequestBody List<Map<String, Object>> records) {
        try {
            Map<String, Object> result = ingestionService.reload(records);
            return Result.success("重新加载完成", result);
        } catch (Exception e) {
            log.error("重新加载失败", e);
            return Result.error("重新加载失败: " + e.getMessage());
        }
    }

    /** 向量存储统计信息 */
    @Operation(summary = "获取向量存储统计")
    @GetMapping("/stats")
    public Result stats() {
        return Result.success(ingestionService.stats());
    }

    /** 房产向量检索 */
    @Operation(summary = "房产语义检索")
    @PostMapping("/search")
    public Result search(@RequestBody PropertySearchRequest request) {
        try {
            if (request.getQuery() != null) {
                queryAnalysisService.analyze(request.getQuery(), request);
            }
            PropertySearchResult result = retrievalService.search(request);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("房产检索失败", e);
            return Result.error("检索失败: " + e.getMessage());
        }
    }

    /** 检索并返回 AI 可读格式的上下文（供 AI 对话调用，支持完整筛选条件） */
    @Operation(summary = "检索房产数据并返回AI可读上下文")
    @PostMapping("/search/context")
    public Result searchContext(@RequestBody PropertySearchRequest request) {
        try {
            if (request.getTopK() <= 0) request.setTopK(5);
            if (request.getQuery() != null) {
                queryAnalysisService.analyze(request.getQuery(), request);
            }
            String context = retrievalService.searchAsContext(request);
            return Result.success(context);
        } catch (Exception e) {
            log.error("上下文检索失败", e);
            return Result.error("检索失败: " + e.getMessage());
        }
    }
}
