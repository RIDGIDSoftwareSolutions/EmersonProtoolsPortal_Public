package com.ridgid.oss.orm;

public class EntityCRUDExceptionError extends EntityCRUDException {
    public EntityCRUDExceptionError() {
    }

    public EntityCRUDExceptionError(String message) {
        super(message);
    }

    public EntityCRUDExceptionError(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCRUDExceptionError(Throwable cause) {
        super(cause);
    }
}
