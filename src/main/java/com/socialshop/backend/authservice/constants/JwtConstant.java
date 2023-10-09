package com.socialshop.backend.authservice.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConstant {
    public static final String authorizationKey = "authorization";
    public static final String typeKey = "type";

    @Value("${LIFE_TIME_REFRESH_TOKEN}")
    public static final Long refreshLifeTime = 60 * 60 * 24 * 15 * 1000L;

    @Value("${LIFE_TIME_ACCESS_TOKEN}")
    public static final Long accessLifeTime = 60 * 15 * 1000L;

    @Value("${LIFE_TIME_ACCESS_TOKEN}")
    public static final Long verificationEmailLifeTime = 60 * 5 * 1000L;

}
