package com.socialshop.backend.authservice.services.dtos;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
