package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDRead;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

/**
 * Base class for a JPA DAO that provides READ CRUD operations only
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides READ (find) CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class JPAEntityCRUDRead<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements EntityCRUDRead<ET, PKT> {

    private final JPAEntityCRUDReadDelegate<ET, PKT> baseDelegate;

    public JPAEntityCRUDRead(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(baseDelegate);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType,
                             String pkName) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType, pkName);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType,
                             short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType, loadBatchSize);
    }

    public JPAEntityCRUDRead(Class<ET> classType,
                             Class<PKT> pkType,
                             String pkName,
                             short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDReadDelegate<>(classType, pkType, pkName, loadBatchSize);
    }

}
