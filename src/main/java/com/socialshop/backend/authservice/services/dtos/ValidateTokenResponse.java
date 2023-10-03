package com.socialshop.backend.authservice.services.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidateTokenResponse {
    private String username;
    private List<String> authorizes;
    private String accessToken;
}
