package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.EntityCRUDExceptionError;
import com.ridgid.oss.orm.EntityCRUDRead;
import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * Base class for a JPA DAO that provides READ CRUD operations only
 *
 * @param <ET>  Entity Type of the Entity that the DAO provides READ (find) CRUD methods for
 * @param <PKT> Type of the Primary Key of the Entity Type ET
 */
public class JPAEntityCRUDRead<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends JPAEntityCRUD<ET, PKT>
        implements EntityCRUDRead<ET, PKT> {

    private final Class<ET> classType;
    private final Class<PKT> pkType;

    protected JPAEntityCRUDRead(Class<ET> classType,
                                Class<PKT> pkType) {
        this.classType = classType;
        this.pkType = pkType;
    }

    /**
     * Finds and retrieves the entity ET instance (if it exits) from the persistence layer
     *
     * @param pk primary key of the entity ET type to find and retrieve
     * @return Optional entity instance of type ET if the entity exists under the given primary key, pk, in the persistence store; otherwise, Optional is empty
     * @throws EntityCRUDExceptionError if there is some error retrieving the value beyond it not existing
     */
    @Override
    public Optional<ET> optionalFind(PKT pk) throws EntityCRUDExceptionError {
        try {
            return Optional.ofNullable(getEntityManager().find(classType, pk, LockModeType.NONE));
        } catch (Exception ex) {
            throw new EntityCRUDExceptionError(ex);
        }
    }

    /**
     * Finds and retrieves all available entities of type ET in the persistence store between the offset (inclusive, zero-based) up to offset + limit (exclusive)
     *
     * @param offset number of records to skip before beginning to return records
     * @param limit  number of records to retrieve after skipping offset records (or fewer if fewer are available)
     * @return list of entities of type ET that are available in the persistence store ranged by the limit and offset given. If none available in the given range, then returns a 0 length list.
     * @throws EntityCRUDExceptionError if there is an error retrieving from the persistence store
     */
    @Override
    public final List<ET> findAll(int offset, int limit) throws EntityCRUDExceptionError {
        try {
            CriteriaQuery<ET> query = getEntityManager().getCriteriaBuilder().createQuery(classType);
            Root<ET> root = query.from(classType);
            return getEntityManager().createQuery(query.select(root))
                    .setLockMode(LockModeType.NONE)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception ex) {
            throw new EntityCRUDExceptionError(ex);
        }
    }

}
