package com.ridgid.oss.orm;

@SuppressWarnings("WeakerAccess")
public class EntityCRUDException extends RuntimeException {
    public EntityCRUDException() {
    }

    public EntityCRUDException(String message) {
        super(message);
    }

    public EntityCRUDException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCRUDException(Throwable cause) {
        super(cause);
    }
}
