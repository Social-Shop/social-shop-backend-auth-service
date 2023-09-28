package com.socialshop.backend.authservice.utils.jwt;

import com.socialshop.backend.authservice.constants.enums.JwtEnum;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Map;

public interface JwtUtil {
    String extractUsername(String token);

    JwtEnum extractTokenType(String token);

    String generateToken(Map<String, Object> claims, UserDetails userDetails, Duration duration);

    boolean isValidToken(String token, UserDetails userDetails);

}
