package com.socialshop.backend.authservice.configuration.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialshop.backend.authservice.constants.ErrorApp;
import com.socialshop.backend.authservice.services.dtos.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var base = BaseResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN)
                .errorMessage("Don't have permission to access")
                .success(false)
                .errorCode(ErrorApp.AUTHORIZATION_FAIL)
                .build();
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(base));
    }
}
