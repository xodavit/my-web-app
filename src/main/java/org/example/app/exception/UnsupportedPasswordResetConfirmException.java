package org.example.app.exception;

public class UnsupportedPasswordResetConfirmException extends RuntimeException {
    public UnsupportedPasswordResetConfirmException() {
    }

    public UnsupportedPasswordResetConfirmException(String message) {
        super(message);
    }

    public UnsupportedPasswordResetConfirmException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedPasswordResetConfirmException(Throwable cause) {
        super(cause);
    }

    public UnsupportedPasswordResetConfirmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
