package com.socialshop.backend.authservice.services.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseResponse<T> {
    @JsonIgnore
    private HttpStatus statusCode;
    private T data;
    private String errorMessage;
    private Long errorCode;
    @Builder.Default
    private boolean success = true;



    @JsonProperty(value = "statusCode")
    public int getStatusCodeValue() {
        return statusCode.value();
    }

    public ResponseEntity<BaseResponse<T>> toResponseEntity() {
        return ResponseEntity.status(statusCode).body(this);
    }
}
