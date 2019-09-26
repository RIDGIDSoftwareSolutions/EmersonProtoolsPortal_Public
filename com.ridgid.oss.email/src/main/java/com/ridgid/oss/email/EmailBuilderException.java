package com.ridgid.oss.email;

public class EmailBuilderException extends RuntimeException {
    private static final long serialVersionUID = -4332457031085617173L;

    public EmailBuilderException() {
    }

    public EmailBuilderException(Throwable cause) {
        super(cause);
    }

    public EmailBuilderException(String message) {
        super(message);
    }

    public EmailBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
