package com.example.admin.config;

import com.example.admin.service.ErrorCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    private LoginRateLimitFilter loginRateLimitFilter;

    @Autowired
    private ErrorCodeService errorCodeService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/login", "/api/register", "/api/send-code", "/api/verify-identity", "/api/reset-password").permitAll()
            .antMatchers("/api/ai/models", "/api/ai/knowledge-bases", "/api/ai/assistants", "/api/ai/share/**").permitAll()
            .antMatchers("/api/site/**").permitAll()
            .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
            .antMatchers("/doc.html").permitAll()
            .antMatchers("/actuator/**").permitAll()
            .antMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
            .and()
            .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
                .authenticationEntryPoint((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    String lang = resolveLang(req.getHeader("Accept-Language"), req.getParameter("lang"));
                    String msg = errorCodeService.getMessage(GlobalExceptionHandler.ERR_UNAUTHORIZED, lang);
                    resp.getWriter().write("{\"code\":" + GlobalExceptionHandler.ERR_UNAUTHORIZED + ",\"msg\":\"" + msg + "\"}");
                })
                .accessDeniedHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    String lang = resolveLang(req.getHeader("Accept-Language"), req.getParameter("lang"));
                    String msg = errorCodeService.getMessage(GlobalExceptionHandler.ERR_FORBIDDEN, lang);
                    resp.getWriter().write("{\"code\":" + GlobalExceptionHandler.ERR_FORBIDDEN + ",\"msg\":\"" + msg + "\"}");
                });
        return http.build();
    }

    private String resolveLang(String acceptLang, String langParam) {
        if (langParam != null && (langParam.startsWith("en") || langParam.startsWith("zh"))) {
            return langParam.startsWith("en") ? "en" : "zh";
        }
        if (acceptLang != null) {
            if (acceptLang.toLowerCase().contains("zh")) return "zh";
            if (acceptLang.toLowerCase().contains("en")) return "en";
        }
        return "zh";
    }
}
