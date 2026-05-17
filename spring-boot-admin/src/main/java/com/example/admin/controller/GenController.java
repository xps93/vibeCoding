package com.example.admin.controller;

import com.example.admin.entity.Result;
import com.example.admin.service.GenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Tag(name = "代码生成", description = "自动生成代码（ZIP下载）")
@RestController
@RequestMapping("/api/gen")
/** 代码生成控制器 */
public class GenController {

    @Autowired
    private GenService genService;

    /** 查询数据库表列表 */
    @GetMapping("/tables")
    public Result tables() {
        List<Map<String, Object>> tables = genService.getTables();
        return Result.success(tables);
    }

    /** 查询指定表的字段信息 */
    @GetMapping("/tables/{tableName}")
    public Result columns(@PathVariable String tableName) {
        List<Map<String, Object>> columns = genService.getColumns(tableName);
        return Result.success(columns);
    }

    /** 生成代码并下载ZIP文件 */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@RequestBody Map<String, String> params) {
        String tableName = params.get("tableName");
        String packageName = params.get("packageName");
        String moduleName = params.get("moduleName");
        String author = params.get("author");

        if (tableName == null || tableName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = "com.example.admin";
        }
        if (moduleName == null || moduleName.isEmpty()) {
            moduleName = "system";
        }
        if (author == null || author.isEmpty()) {
            author = "admin";
        }

        byte[] zip = genService.generateCode(tableName, packageName, moduleName, author);

        String filename;
        try {
            filename = URLEncoder.encode(tableName + "_code.zip", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            filename = tableName + "_code.zip";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zip);
    }
}
