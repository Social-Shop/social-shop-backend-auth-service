package com.socialshop.backend.authservice.services.impl;

import com.socialshop.backend.authservice.constants.ErrorApp;
import com.socialshop.backend.authservice.constants.JwtConstant;
import com.socialshop.backend.authservice.constants.enums.JwtEnum;
import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.repositiories.SessionRepository;
import com.socialshop.backend.authservice.repositiories.UserRepository;
import com.socialshop.backend.authservice.services.TokenService;
import com.socialshop.backend.authservice.services.dtos.RefreshTokenRequest;
import com.socialshop.backend.authservice.services.dtos.SessionAuthResponse;
import com.socialshop.backend.authservice.services.mapper.UserMapper;
import com.socialshop.backend.authservice.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    public SessionAuthResponse refreshToken(RefreshTokenRequest request) throws BadRequestException {
        String username = jwtUtil.extractUsername(request.getRefreshToken());
        JwtEnum type = jwtUtil.extractTokenType(request.getRefreshToken());
        var userFind = userRepository.findUserByEmail(username);
        if (userFind.isEmpty() || !jwtUtil.isValidToken(request.getRefreshToken(), userFind.get()) || type != JwtEnum.REFRESH) {
            throw new BadRequestException("Refresh token is not valid", ErrorApp.INVALID_REFRESH_TOKEN);
        }
        var session = sessionRepository.findByRefreshToken(request.getRefreshToken());
        if (session.getIsBlackList()) {
            throw new BadRequestException("Refresh token is blacklist", ErrorApp.INVALID_REFRESH_TOKEN);
        }
        String accessToken = generateAccessToken(userFind.get());
        redisTemplate.opsForValue().set(accessToken, Instant.ofEpochMilli(System.currentTimeMillis() + JwtConstant.accessLifeTime));
        return SessionAuthResponse.builder().refreshToken(request.getRefreshToken()).accessToken(accessToken).build();
    }

    private String generateAccessToken(UserDetails userDetails) {
        // Access Token
        var claimsRefreshToken = new HashMap<String, Object>();
        claimsRefreshToken.put(JwtConstant.typeKey, JwtEnum.ACCESS);
        claimsRefreshToken.put(JwtConstant.authorizationKey, Arrays.toString(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray()));
        return jwtUtil.generateToken(claimsRefreshToken, userDetails, Duration.ofMillis(JwtConstant.accessLifeTime));
    }
}
