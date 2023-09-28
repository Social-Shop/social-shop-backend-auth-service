package com.socialshop.backend.authservice.configuration;

import com.socialshop.backend.authservice.constants.exception.BadRequestException;
import com.socialshop.backend.authservice.services.dtos.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<BaseResponse<Object>> handleBadRequestException(BadRequestException badRequestException) {
        var res = BaseResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .errorCode(badRequestException.getErrorCode())
                .errorMessage(badRequestException.getMessage())
                .success(false)
                .build();
        return res.toResponseEntity();
    }


}
