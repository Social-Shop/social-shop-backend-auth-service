package com.socialshop.backend.authservice.controllers.v1;

import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.services.AuthService;
import com.socialshop.backend.authservice.services.dtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    protected ResponseEntity<BaseResponse<RegisterSessionAuthResponse>> register(@RequestBody @Valid RegisterRequest request) throws BadRequestException {
        var session = authService.register(request);
        var res = BaseResponse.<RegisterSessionAuthResponse>builder().data(session).statusCode(HttpStatus.CREATED).build();
        return res.toResponseEntity();
    }

    @PostMapping("/register/validate")
    protected ResponseEntity<BaseResponse<SessionAuthResponse>> validateRegisterEmailOtp(@RequestBody @Valid RegisterValidateTokenAuthRequest request) throws BadRequestException {
        var session = authService.validateRegisterEmailOtp(request);
        var res = BaseResponse.<SessionAuthResponse>builder().data(session).statusCode(HttpStatus.OK).build();
        return res.toResponseEntity();
    }


    @PostMapping("/login")
    protected ResponseEntity<BaseResponse<SessionAuthResponse>> login(@RequestBody @Valid LoginRequest request) throws BadRequestException {
        var session = authService.login(request);
        var res = BaseResponse.<SessionAuthResponse>builder().data(session).statusCode(HttpStatus.CREATED).build();
        return res.toResponseEntity();
    }

    @PostMapping("/logout")
    protected ResponseEntity<BaseResponse<Object>> logout() {
        authService.logout();
        var res = BaseResponse.builder().statusCode(HttpStatus.OK).build();
        return res.toResponseEntity();
    }
}
