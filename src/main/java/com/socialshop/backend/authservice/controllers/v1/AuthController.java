package com.socialshop.backend.authservice.controllers.v1;

import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.services.AuthService;
import com.socialshop.backend.authservice.services.dtos.BaseResponse;
import com.socialshop.backend.authservice.services.dtos.LoginRequest;
import com.socialshop.backend.authservice.services.dtos.RegisterRequest;
import com.socialshop.backend.authservice.services.dtos.SessionAuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    protected ResponseEntity<BaseResponse<SessionAuthResponse>> register(@RequestBody @Valid RegisterRequest request) throws BadRequestException {
        var session = authService.register(request);
        var res = BaseResponse.<SessionAuthResponse>builder().data(session).statusCode(HttpStatus.CREATED).build();
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
