package com.socialshop.backend.authservice.services;

import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.services.dtos.*;

public interface AuthService {
    RegisterSessionAuthResponse register(RegisterRequest request) throws BadRequestException;

    SessionAuthResponse validateRegisterEmailOtp(RegisterValidateTokenAuthRequest request) throws BadRequestException;

    SessionAuthResponse login(LoginRequest request) throws BadRequestException;

    void logout();

}
