package com.socialshop.backend.authservice.constants.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;


import lombok.experimental.StandardException;

@StandardException
@Getter
public class BadRequestException extends AppException {

    public BadRequestException(long errorCode) {

        super(errorCode);
    }

    public BadRequestException(String message, long errorCode) {
        super(message, errorCode);
    }

    public BadRequestException(String message, Throwable cause, long errorCode) {
        super(message, cause, errorCode);
    }

    public BadRequestException(Throwable cause, long errorCode) {
        super(cause, errorCode);
    }

    public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, long errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
