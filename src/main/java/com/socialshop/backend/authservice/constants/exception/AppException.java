package com.socialshop.backend.authservice.constants.exception;


import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;


import lombok.experimental.StandardException;

@EqualsAndHashCode(callSuper = true)
@StandardException
@Data
public abstract class AppException extends Exception {
    protected long errorCode;

    public AppException() {
    }

    public AppException(long errorCode) {
        this.errorCode = errorCode;
    }

    public AppException(String message, long errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String message, Throwable cause, long errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AppException(Throwable cause, long errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public AppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, long errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
