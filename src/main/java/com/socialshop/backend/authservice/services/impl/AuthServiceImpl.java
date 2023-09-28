package com.socialshop.backend.authservice.services.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.socialshop.backend.authservice.constants.ErrorApp;
import com.socialshop.backend.authservice.constants.JwtConstant;
import com.socialshop.backend.authservice.constants.enums.JwtEnum;
import com.socialshop.backend.authservice.constants.enums.RoleEnum;
import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.repositiories.RoleRepository;
import com.socialshop.backend.authservice.repositiories.SessionRepository;
import com.socialshop.backend.authservice.repositiories.UserRepository;
import com.socialshop.backend.authservice.services.AuthService;
import com.socialshop.backend.authservice.services.dtos.LoginRequest;
import com.socialshop.backend.authservice.services.dtos.RefreshTokenRequest;
import com.socialshop.backend.authservice.services.dtos.RegisterRequest;
import com.socialshop.backend.authservice.services.dtos.SessionAuthResponse;
import com.socialshop.backend.authservice.services.mapper.UserMapper;
import com.socialshop.backend.authservice.services.model.SessionEntity;
import com.socialshop.backend.authservice.services.model.UserEntity;
import com.socialshop.backend.authservice.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public SessionAuthResponse register(RegisterRequest request) throws BadRequestException {
        var userFind = userRepository.findUserByEmail(request.getEmail());
        if (userFind.isPresent()) {
            throw new BadRequestException("Email already exist", ErrorApp.USER_ALREADY_EXIST);
        }
        var roleFind = roleRepository.findByName(RoleEnum.USER.name());
        if (roleFind.isEmpty()) {
            throw new IllegalStateException();
        }
        // Setup user
        Snowflake snowflake = new Snowflake();
        UserEntity user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setUsername(snowflake.nextIdStr());
        user.setRoles(Set.of(roleFind.get()));
        userRepository.save(user);

        // Session process
        var res = generateSession(user);
        var session = SessionEntity.builder()
                .user(user)
                .refreshToken(res.getRefreshToken())
                .expiredAt(Instant.ofEpochMilli(System.currentTimeMillis() + JwtConstant.refreshLifeTime))
                .build();
        sessionRepository.save(session);
        redisTemplate.opsForValue().set(res.getAccessToken(), Instant.ofEpochMilli(System.currentTimeMillis() + JwtConstant.accessLifeTime));
        return res;
    }

    @Override
    public SessionAuthResponse login(LoginRequest request) throws BadRequestException {
        var userFind = userRepository.findUserByEmail(request.getEmail());
        if (userFind.isEmpty() || !passwordEncoder.matches(request.getPassword(), userFind.get().getPassword())) {
            throw new BadRequestException("Email or password not correct", ErrorApp.EMAIL_OR_PASSWORD_NOT_CORRECT);
        }
        var user = userFind.get();
        var res = generateSession(user);
        var expiredTime = Instant.ofEpochMilli(System.currentTimeMillis() + JwtConstant.refreshLifeTime);
        var session = SessionEntity.builder()
                .user(user)
                .refreshToken(res.getRefreshToken())
                .expiredAt(expiredTime)
                .build();
        sessionRepository.save(session);
        redisTemplate.opsForValue().set(res.getAccessToken(), Instant.ofEpochMilli(System.currentTimeMillis() + JwtConstant.accessLifeTime));
        return res;
    }


    private SessionAuthResponse generateSession(UserDetails userDetails) {
        var refreshToken = generateRefreshToken(userDetails);
        var accessToken = generateAccessToken(userDetails);
        return SessionAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateRefreshToken(UserDetails userDetails) {
        // Refresh Token
        var claimsRefreshToken = new HashMap<String, Object>();
        claimsRefreshToken.put(JwtConstant.typeKey, JwtEnum.REFRESH);
        return jwtUtil.generateToken(claimsRefreshToken, userDetails, Duration.ofMillis(JwtConstant.refreshLifeTime));
    }

    private String generateAccessToken(UserDetails userDetails) {
        // Access Token
        var claimsRefreshToken = new HashMap<String, Object>();
        claimsRefreshToken.put(JwtConstant.typeKey, JwtEnum.ACCESS);
        claimsRefreshToken.put(JwtConstant.authorizationKey, Arrays.toString(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray()));
        return jwtUtil.generateToken(claimsRefreshToken, userDetails, Duration.ofMillis(JwtConstant.accessLifeTime));
    }


}
