package com.socialshop.backend.authservice.services;

import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.services.dtos.RefreshTokenRequest;
import com.socialshop.backend.authservice.services.dtos.SessionAuthResponse;
import com.socialshop.backend.authservice.services.dtos.ValidateTokenResponse;

public interface TokenService {
    SessionAuthResponse refreshToken(RefreshTokenRequest request) throws BadRequestException;
    ValidateTokenResponse validateToken();
}
