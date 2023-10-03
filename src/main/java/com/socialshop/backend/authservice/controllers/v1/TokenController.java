package com.socialshop.backend.authservice.controllers.v1;

import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.services.TokenService;
import com.socialshop.backend.authservice.services.dtos.BaseResponse;
import com.socialshop.backend.authservice.services.dtos.RefreshTokenRequest;
import com.socialshop.backend.authservice.services.dtos.SessionAuthResponse;
import com.socialshop.backend.authservice.services.dtos.ValidateTokenResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/token")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<SessionAuthResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) throws BadRequestException {
        var session = tokenService.refreshToken(request);
        var res = BaseResponse.<SessionAuthResponse>builder().data(session).statusCode(HttpStatus.CREATED).build();
        return res.toResponseEntity();
    }

    @GetMapping("/validate")
    public ResponseEntity<BaseResponse<ValidateTokenResponse>> validate() {
        var validated = tokenService.validateToken();
        var res = BaseResponse.<ValidateTokenResponse>builder().data(validated).statusCode(HttpStatus.OK).build();
        return res.toResponseEntity();
    }
}
