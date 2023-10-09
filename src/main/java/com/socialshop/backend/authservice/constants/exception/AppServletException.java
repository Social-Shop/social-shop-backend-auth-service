package com.socialshop.backend.authservice.constants.exception;

import jakarta.servlet.ServletException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.StandardException;

@EqualsAndHashCode(callSuper = true)
@StandardException
@Data
public abstract class AppServletException extends ServletException {
    protected long errorCode;

    public AppServletException() {
    }

    public AppServletException(long errorCode) {
        this.errorCode = errorCode;
    }

    public AppServletException(String message, long errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppServletException(String message, Throwable rootCause, long errorCode) {
        super(message, rootCause);
        this.errorCode = errorCode;
    }

    public AppServletException(Throwable rootCause, long errorCode) {
        super(rootCause);
        this.errorCode = errorCode;
    }
}
