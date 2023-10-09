package com.socialshop.backend.authservice.configuration.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialshop.backend.authservice.constants.ErrorApp;
import com.socialshop.backend.authservice.services.dtos.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var base = BaseResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED)
                .errorMessage("Token is not valid")
                .success(false)
                .errorCode(ErrorApp.AUTHENTICATION_FAIL)
                .build();
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(base));
    }
}
