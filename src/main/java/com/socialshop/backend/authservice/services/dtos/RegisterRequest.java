package com.socialshop.backend.authservice.services.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email
    private String email;
    @NotEmpty
    @NotBlank
    private String password;
}
