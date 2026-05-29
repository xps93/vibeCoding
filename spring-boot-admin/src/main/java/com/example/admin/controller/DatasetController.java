package com.example.admin.controller;

import com.example.admin.entity.Dataset;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.DatasetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "本地数据集", description = "上传、管理与查询本地数据集")
@RestController
@RequestMapping("/api/ai")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

    /** 上传数据集文件 */
    @PostMapping("/datasets/upload")
    public Result upload(@AuthenticationPrincipal User user,
                         @RequestParam("file") MultipartFile file) {
        try {
            Dataset dataset = datasetService.upload(file, user.getId());
            return Result.success("上传成功", dataset);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("文件解析失败: " + e.getMessage());
        }
    }

    /** 获取数据集列表 */
    @GetMapping("/datasets")
    public Result list(@AuthenticationPrincipal User user) {
        List<Dataset> list = datasetService.listByUser(user.getId());
        return Result.success(list);
    }

    /** 获取数据集详情（含行数据） */
    @GetMapping("/datasets/{id}")
    public Result detail(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Dataset dataset = datasetService.getById(id, user.getId());
        if (dataset == null) {
            return Result.error("数据集不存在或无权访问");
        }
        List<Map<String, Object>> rows = datasetService.getRows(id);
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("dataset", dataset);
        data.put("rows", rows);
        return Result.success(data);
    }

    /** 查询数据集 */
    @PostMapping("/datasets/{id}/query")
    public Result query(@AuthenticationPrincipal User user,
                        @PathVariable Long id,
                        @RequestBody Map<String, Object> body) {
        String query = body.get("query") != null ? body.get("query").toString() : "";
        if (query.trim().isEmpty()) {
            return Result.error("查询内容不能为空");
        }
        try {
            Map<String, Object> result = datasetService.queryDataset(id, user.getId(), query);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /** 删除数据集 */
    @DeleteMapping("/datasets/{id}")
    public Result delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        try {
            datasetService.delete(id, user.getId());
            return Result.success("删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }
}
