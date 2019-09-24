package com.ridgid.oss.email;

public class EmailBuilderSendFailedException extends EmailBuilderException {
    private static final long serialVersionUID = 592533059430929410L;

    public EmailBuilderSendFailedException() {
    }

    public EmailBuilderSendFailedException(Throwable cause) {
        super(cause);
    }

    public EmailBuilderSendFailedException(String message) {
        super(message);
    }

    public EmailBuilderSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
