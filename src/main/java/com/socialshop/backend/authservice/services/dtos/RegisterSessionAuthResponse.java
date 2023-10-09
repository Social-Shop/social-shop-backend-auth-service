package com.socialshop.backend.authservice.services.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterSessionAuthResponse {
    private String requestId;
}
