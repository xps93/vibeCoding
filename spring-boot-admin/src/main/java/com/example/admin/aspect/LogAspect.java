package com.example.admin.aspect;

import com.example.admin.entity.OperLog;
import com.example.admin.entity.User;
import com.example.admin.mapper.OperLogMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperLogMapper operLogMapper;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) && execution(* com.example.admin.controller.*.*(..))")
    public void controllerPointcut() {
    }

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        recordLog(joinPoint, null);
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        recordLog(joinPoint, e);
    }

    private void recordLog(JoinPoint joinPoint, Exception e) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String uri = request.getRequestURI();
            // Skip login/logout endpoints (already logged in TokenService)
            if (uri.contains("/login") || uri.contains("/logout")) return;
            // Skip info endpoints
            if (uri.contains("/user/info") || uri.contains("/menus/routers")) return;

            OperLog log = new OperLog();
            log.setOperUrl(uri);
            log.setRequestMethod(request.getMethod());
            log.setOperIp(request.getRemoteAddr());
            log.setOperTime(LocalDateTime.now());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                log.setOperName(((User) auth.getPrincipal()).getUsername());
            }

            String methodName = joinPoint.getSignature().getName();
            log.setMethod(joinPoint.getTarget().getClass().getSimpleName() + "." + methodName);
            log.setTitle(methodName);

            // Classify business type
            if (methodName.startsWith("add") || methodName.startsWith("insert")) {
                log.setBusinessType(1);
            } else if (methodName.startsWith("edit") || methodName.startsWith("update")) {
                log.setBusinessType(2);
            } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
                log.setBusinessType(3);
            } else {
                log.setBusinessType(0);
            }

            // Skip pure query operations (type 0) to reduce noise
            if (log.getBusinessType() == 0) return;

            log.setOperParam(Arrays.toString(joinPoint.getArgs()));

            if (e != null) {
                log.setStatus(1);
                log.setErrorMsg(e.getMessage());
            } else {
                log.setStatus(0);
            }

            operLogMapper.insert(log);
        } catch (Exception ignored) {
            // Don't let logging failure affect business logic
        }
    }
}
