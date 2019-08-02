package com.ridgid.oss.common.security.realm.authentication;

@SuppressWarnings({"WeakerAccess", "unused"})
public class RealmAuthenticationException extends Exception {

    public RealmAuthenticationException() {
    }

    public RealmAuthenticationException(String message) {
        super(message);
    }

    public RealmAuthenticationException(String message,
                                        Throwable cause) {
        super(message, cause);
    }

    public RealmAuthenticationException(Throwable cause) {
        super(cause);
    }

    public RealmAuthenticationException(String message,
                                        Throwable cause,
                                        boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
