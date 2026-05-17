package com.example.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("后台管理系统 API")
                        .description("企业级后台管理系统（Ds-Ai），提供用户、角色、菜单、部门、岗位、字典、参数、通知、日志、监控、代码生成等完整管理功能。")
                        .version("1.0.0")
                        .contact(new Contact().name("Admin")));
    }
}
