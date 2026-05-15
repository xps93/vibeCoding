package com.example.admin.config;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(0)
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MS = 15 * 60 * 1000L; // 15分钟
    private static final long WINDOW_MS = 60 * 1000L; // 1分钟窗口

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!isLoginRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        String username = request.getParameter("username");
        String key = ip + ":" + (username != null ? username : "");

        LoginAttempt attempt = attempts.computeIfAbsent(key, k -> new LoginAttempt());

        synchronized (attempt) {
            if (attempt.isBlocked()) {
                writeRateLimitResponse(response, "登录尝试过于频繁，请15分钟后再试");
                return;
            }

            if (attempt.isWindowExpired()) {
                attempt.reset();
            }

            attempt.increment();
        }

        chain.doFilter(request, response);
    }

    public void recordFailure(String ip, String username) {
        String key = ip + ":" + (username != null ? username : "");
        LoginAttempt attempt = attempts.computeIfAbsent(key, k -> new LoginAttempt());
        synchronized (attempt) {
            if (attempt.count >= MAX_ATTEMPTS) {
                attempt.blockUntil = System.currentTimeMillis() + BLOCK_DURATION_MS;
            }
        }
    }

    public void recordSuccess(String ip, String username) {
        String key = ip + ":" + (username != null ? username : "");
        attempts.remove(key);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return request.getRequestURI().equals("/api/login")
                && "POST".equalsIgnoreCase(request.getMethod());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }

    private void writeRateLimitResponse(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(429);
        response.getWriter().write("{\"code\":429,\"msg\":\"" + msg + "\"}");
    }

    private static class LoginAttempt {
        int count;
        long windowStart;
        long blockUntil;

        LoginAttempt() {
            this.count = 0;
            this.windowStart = System.currentTimeMillis();
            this.blockUntil = 0;
        }

        boolean isBlocked() {
            return System.currentTimeMillis() < blockUntil;
        }

        boolean isWindowExpired() {
            return System.currentTimeMillis() - windowStart > WINDOW_MS;
        }

        void reset() {
            this.count = 0;
            this.windowStart = System.currentTimeMillis();
        }

        void increment() {
            this.count++;
        }
    }
}
