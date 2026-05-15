package com.example.admin.config;

import com.example.admin.entity.Role;
import com.example.admin.entity.User;
import com.example.admin.mapper.RoleMapper;
import com.example.admin.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            User user = tokenService.getUserFromToken(token);
            if (user != null) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                Set<String> perms = tokenService.getPermissionsByRoleIds(user.getRoleIds());
                for (String p : perms) {
                    authorities.add(new SimpleGrantedAuthority(p));
                }
                for (Long rid : user.getRoleIds()) {
                    Role role = roleMapper.selectById(rid);
                    if (role != null) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleKey().toUpperCase()));
                    }
                }
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
