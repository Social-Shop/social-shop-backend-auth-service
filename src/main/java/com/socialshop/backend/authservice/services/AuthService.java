package com.socialshop.backend.authservice.services;

import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.services.dtos.LoginRequest;
import com.socialshop.backend.authservice.services.dtos.RefreshTokenRequest;
import com.socialshop.backend.authservice.services.dtos.RegisterRequest;
import com.socialshop.backend.authservice.services.dtos.SessionAuthResponse;

public interface AuthService {
    SessionAuthResponse register(RegisterRequest request) throws BadRequestException;
    SessionAuthResponse login(LoginRequest request) throws BadRequestException;

}
