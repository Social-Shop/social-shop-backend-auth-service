package com.socialshop.backend.authservice.configuration.security.filter;

import com.socialshop.backend.authservice.utils.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            if (!StringUtils.contains(authHeader, "Bearer ")) {
                throw new BadCredentialsException("Token is not valid");
            }
            String jwtToken = authHeader.substring(7).trim();
            if (StringUtils.isNotEmpty(jwtToken) && redisTemplate.opsForValue().get(jwtToken) != null) {
                String username = jwtUtil.extractUsername(jwtToken);
                var user = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.isValidToken(jwtToken, user)) {
                    var token = new UsernamePasswordAuthenticationToken(username, jwtToken, user.getAuthorities());
                    var context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(token);
                    SecurityContextHolder.setContext(context);
                }
            }
        }


        filterChain.doFilter(request, response);
    }
}
