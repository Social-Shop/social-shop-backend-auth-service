package com.socialshop.backend.authservice.services.dtos;

import lombok.Data;

@Data
public class RegisterValidateTokenAuthRequest {
    private String code;
    private String requestId;
    private String email;
}
