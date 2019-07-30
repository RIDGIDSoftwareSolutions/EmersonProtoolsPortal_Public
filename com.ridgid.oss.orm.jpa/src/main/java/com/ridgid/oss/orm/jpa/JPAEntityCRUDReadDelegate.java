package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.Hierarchy;
import com.ridgid.oss.orm.EntityCRUDRead;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

final class JPAEntityCRUDReadDelegate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements
        JPAEntityCRUDDelegateRequired<ET, PKT>,
        EntityCRUDRead<ET, PKT> {

    private final JPAEntityCRUDDelegate<ET, PKT> baseDelegate;

    private TypedQuery<ET> findAllByLimitQuery;

    JPAEntityCRUDReadDelegate(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = baseDelegate;
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDDelegate<ET, PKT>(classType, pkType);
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType,
                              String pkName) {
        this.baseDelegate = new JPAEntityCRUDDelegate<ET, PKT>(classType, pkType, pkName);
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType,
                              short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDDelegate<ET, PKT>(classType, pkType, loadBatchSize);
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType,
                              String pkName,
                              short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDDelegate<ET, PKT>(classType, pkType, pkName, loadBatchSize);
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        baseDelegate.setEntityManager(entityManager);
    }

    @Override
    public EntityManager getEntityManager() {
        return baseDelegate.getEntityManager();
    }

    @Override
    public short getLoadBatchSize() {
        return baseDelegate.getLoadBatchSize();
    }

    @Override
    public Optional<ET> load(PKT pk) {
        return Optional.empty();
    }

    @Override
    public Stream<ET> loadBatch(List<PKT> pkList) {
        return baseDelegate.loadBatch(pkList);
    }

    @Override
    public ET initializeAndDetach(ET entity, Hierarchy<ET> hierarchy) {
        return baseDelegate.initializeAndDetach(entity, hierarchy);
    }

    @Override
    public ET initialize(ET entity, Hierarchy<ET> hierarchy) {
        return baseDelegate.initialize(entity, hierarchy);
    }

    @Override
    public ET detach(ET entity, Hierarchy<ET> hierarchy) {
        return baseDelegate.detach(entity, hierarchy);
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
            return Optional.ofNullable(baseDelegate.getEntityManager().find(baseDelegate.classType, pk, LockModeType.NONE));
        } catch (Exception ex) {
            throw new EntityCRUDExceptionError(ex);
        }
    }

    @Override
    public Optional<ET> optionalFind(PKT pk, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError {
        try {
            return optionalFind(pk).map(this::initializeAndDetach);
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
            return getFindAllByLimitQuery()
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception ex) {
            throw baseDelegate.enhanceExceptionWithEntityManagerNullCheck(ex);
        }
    }

    private TypedQuery<ET> getFindAllByLimitQuery() {
        if (findAllByLimitQuery == null)
            synchronized (baseDelegate.getEntityManager()) {
                if (findAllByLimitQuery == null) {
                    CriteriaQuery<ET> cb = baseDelegate.getEntityManager().getCriteriaBuilder().createQuery(baseDelegate.classType);
                    Root<ET> entity = cb.from(baseDelegate.classType);
                    findAllByLimitQuery
                            = baseDelegate.getEntityManager()
                            .createQuery(cb.select(entity))
                            .setLockMode(LockModeType.NONE);
                }
            }
        return findAllByLimitQuery;
    }

    @Override
    public List<ET> findAll(int offset, int limit, Hierarchy<ET> hierarchy) throws EntityCRUDExceptionError {
        try {
            return initializeAndDetach
                    (
                            getFindAllByLimitQuery()
                                    .setFirstResult(offset)
                                    .setMaxResults(limit)
                                    .getResultStream(),
                            hierarchy
                    );
        } catch (Exception ex) {
            throw new EntityCRUDExceptionError(ex);
        }
    }

}
