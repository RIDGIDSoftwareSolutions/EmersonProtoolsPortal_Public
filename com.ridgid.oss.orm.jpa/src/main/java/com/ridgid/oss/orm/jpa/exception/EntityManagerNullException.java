package com.ridgid.oss.orm.jpa.exception;

import com.ridgid.oss.orm.exception.EntityCRUDException;

public class EntityManagerNullException extends EntityCRUDException {
    public EntityManagerNullException(Exception e) {
        super("EntityManager is null: Ensure you have set the Entity Manager manually or that you are in a container managed context and transaction such that the container has auto-wired the entity manager",
                e);
    }
}

