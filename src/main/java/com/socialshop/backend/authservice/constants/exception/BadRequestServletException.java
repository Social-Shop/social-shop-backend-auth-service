package com.socialshop.backend.authservice.constants.exception;

import lombok.Getter;
import lombok.experimental.StandardException;

@StandardException
@Getter
public class BadRequestServletException extends AppServletException {
    public BadRequestServletException(String message) {
        super(message);
    }

    public BadRequestServletException(Throwable cause) {
        super(cause);
    }

    public BadRequestServletException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestServletException(long errorCode) {
        super(errorCode);
    }

    public BadRequestServletException(String message, long errorCode) {
        super(message, errorCode);
    }

    public BadRequestServletException(String message, Throwable rootCause, long errorCode) {
        super(message, rootCause, errorCode);
    }

    public BadRequestServletException(Throwable rootCause, long errorCode) {
        super(rootCause, errorCode);
    }
}
