package com.ridgid.oss.orm.exception;

@SuppressWarnings("unused")
public class EntityCRUDExceptionNotFound extends EntityCRUDException {
    public EntityCRUDExceptionNotFound() {
    }

    public EntityCRUDExceptionNotFound(String message) {
        super(message);
    }

    public EntityCRUDExceptionNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCRUDExceptionNotFound(Throwable cause) {
        super(cause);
    }
}
