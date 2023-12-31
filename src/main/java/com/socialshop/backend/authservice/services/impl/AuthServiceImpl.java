package com.socialshop.backend.authservice.services.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.socialshop.backend.authservice.constants.ErrorApp;
import com.socialshop.backend.authservice.constants.JwtConstant;
import com.socialshop.backend.authservice.constants.enums.JwtEnum;
import com.socialshop.backend.authservice.constants.enums.RoleEnum;
import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.mq.producer.EmailProducer;
import com.socialshop.backend.authservice.repositiories.RoleRepository;
import com.socialshop.backend.authservice.repositiories.SessionRepository;
import com.socialshop.backend.authservice.repositiories.UserRepository;
import com.socialshop.backend.authservice.services.AuthService;
import com.socialshop.backend.authservice.services.dtos.*;
import com.socialshop.backend.authservice.services.mapper.UserMapper;
import com.socialshop.backend.authservice.services.model.SessionEntity;
import com.socialshop.backend.authservice.services.model.UserEntity;
import com.socialshop.backend.authservice.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailProducer emailProducer;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;


    @Override
    public RegisterSessionAuthResponse register(RegisterRequest request) throws BadRequestException {
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
        user.setIsActive(false);
        userRepository.save(user);

        // Validation Process
        String otpID = UUID.randomUUID().toString();
        String otp = RandomStringUtils.randomNumeric(6);
        redisTemplate.opsForValue().set(otpID, otp, Duration.ofMillis(JwtConstant.verificationEmailLifeTime));
        // Send email
        emailProducer.sendEmailEvent(request.getEmail(), otp);
        return RegisterSessionAuthResponse.builder().requestId(otpID).build();
    }

    @Override
    public SessionAuthResponse validateRegisterEmailOtp(RegisterValidateTokenAuthRequest request) throws BadRequestException {
        var otp = redisTemplate.opsForValue().get(request.getRequestId());
        if (otp == null || otp.equals(request.getCode())) {
            throw new BadRequestException("OTP not valid");
        }
        var userFind = userRepository.findUserByEmail(request.getEmail());
        if (userFind.isEmpty()) {
            throw new BadRequestException("Email already exist", ErrorApp.USER_ALREADY_EXIST);
        }
        var user = userFind.get();
        user.setIsActive(true);
        var res = generateSession(user);
        var session = SessionEntity.builder()
                .user(user)
                .refreshToken(res.getRefreshToken())
                .expiredAt(Instant.ofEpochMilli(System.currentTimeMillis() + JwtConstant.refreshLifeTime))
                .build();
        session = sessionRepository.save(session);
        userRepository.save(user);
        redisTemplate.opsForValue().set(res.getAccessToken(), session.getId(), Duration.ofMillis(System.currentTimeMillis() + JwtConstant.accessLifeTime));
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
        session = sessionRepository.save(session);
        redisTemplate.opsForValue().set(res.getAccessToken(), session.getId(), Duration.ofMillis(System.currentTimeMillis() + JwtConstant.accessLifeTime));
        return res;
    }

    @Override
    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = auth.getCredentials().toString();
        Long sessionId = (Long) redisTemplate.opsForValue().get(token);
        assert sessionId != null;
        var sessionFind = sessionRepository.findById(sessionId);
        if (sessionFind.isPresent()) {
            var session = sessionFind.get();
            session.setIsBlackList(true);
            sessionRepository.save(session);
        }
        redisTemplate.opsForValue().getAndDelete(token);
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
