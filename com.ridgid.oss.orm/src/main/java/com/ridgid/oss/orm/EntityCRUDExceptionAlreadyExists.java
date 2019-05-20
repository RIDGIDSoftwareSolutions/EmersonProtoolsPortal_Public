package com.ridgid.oss.orm;

public class EntityCRUDExceptionAlreadyExists extends EntityCRUDException {
    public EntityCRUDExceptionAlreadyExists() {
    }

    public EntityCRUDExceptionAlreadyExists(String message) {
        super(message);
    }

    public EntityCRUDExceptionAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCRUDExceptionAlreadyExists(Throwable cause) {
        super(cause);
    }
}
