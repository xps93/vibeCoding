package com.example.admin.controller;

import com.example.admin.entity.AiConfig;
import com.example.admin.entity.Result;
import com.example.admin.entity.User;
import com.example.admin.service.AiConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "AI配置", description = "AI用户个性化配置")
@RestController
@RequestMapping("/api/ai")
public class AiConfigController {

    @Autowired
    private AiConfigService configService;

    /** 获取当前用户AI配置 */
    @GetMapping("/config")
    public Result getConfig(@AuthenticationPrincipal User user) {
        AiConfig config = configService.getByUserId(user.getId());
        return Result.success(config);
    }

    /** 保存当前用户AI配置 */
    @PutMapping("/config")
    public Result saveConfig(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> body) {
        AiConfig config = new AiConfig();
        config.setUserId(user.getId());
        if (body.get("temperature") != null) {
            config.setTemperature(Double.valueOf(body.get("temperature").toString()));
        }
        if (body.get("maxTokens") != null) {
            config.setMaxTokens(Integer.valueOf(body.get("maxTokens").toString()));
        }
        if (body.get("systemPrompt") != null) {
            config.setSystemPrompt((String) body.get("systemPrompt"));
        }
        AiConfig saved = configService.save(config);
        return Result.success(saved);
    }
}
