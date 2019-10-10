package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
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
    JPAEntityCRUDReadDelegateRequired<ET, PKT>,
    EntityCRUDRead<ET, PKT>
{

    private final JPAEntityCRUDDelegate<ET, PKT> baseDelegate;

    JPAEntityCRUDReadDelegate(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = baseDelegate;
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType)
    {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType);
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType,
                              String pkName)
    {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, pkName);
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType,
                              short loadBatchSize)
    {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, loadBatchSize);
    }

    JPAEntityCRUDReadDelegate(Class<ET> classType,
                              Class<PKT> pkType,
                              String pkName,
                              short loadBatchSize)
    {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, pkName, loadBatchSize);
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
    public Class<ET> getClassType() {
        return baseDelegate.getClassType();
    }

    @Override
    public Class<PKT> getPkType() {
        return baseDelegate.getPkType();
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
    public ET initializeAndDetach(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.initializeAndDetach(entity, hierarchy);
    }

    @SuppressWarnings("TypeParameterHidesVisibleType")
    @Override
    public <ET> ET initializeEntity(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.initializeEntity(entity, hierarchy);
    }

    @SuppressWarnings("TypeParameterHidesVisibleType")
    @Override
    public <ET> ET detachEntity(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.detachEntity(entity, hierarchy);
    }

    @Override
    public void flushContext() {
        baseDelegate.flushContext();
    }

    @Override
    public void clearContext() {
        baseDelegate.clearContext();
    }

    @Override
    public Optional<ET> optionalFind(PKT pk) throws EntityCRUDExceptionError {
        try {
            return Optional.ofNullable(
                baseDelegate.getEntityManager().find(baseDelegate.classType, pk, LockModeType.NONE));
        } catch ( Exception ex ) {
            throw new EntityCRUDExceptionError(ex);
        }
    }

    @Override
    public Optional<ET> optionalFind(PKT pk, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError {
        try {
            return optionalFind(pk);
        } catch ( Exception ex ) {
            throw new EntityCRUDExceptionError(ex);
        }
    }

    @Override
    public final List<ET> findAll(int offset, int limit) throws EntityCRUDExceptionError {
        try {
            return getFindAllByLimitQuery()
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
        } catch ( Exception ex ) {
            throw baseDelegate.enhanceExceptionWithEntityManagerNullCheck(ex);
        }
    }

    private TypedQuery<ET> getFindAllByLimitQuery() {
        CriteriaQuery<ET> cb = baseDelegate.getEntityManager()
                                           .getCriteriaBuilder()
                                           .createQuery(baseDelegate.classType);
        Root<ET> entity = cb.from(baseDelegate.classType);
        return baseDelegate.getEntityManager()
                           .createQuery(cb.select(entity))
                           .setLockMode(LockModeType.NONE);
    }

    @Override
    public List<ET> findAll(int offset, int limit, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError {
        try {
            return initialize
                (
                    getFindAllByLimitQuery()
                        .setFirstResult(offset)
                        .setMaxResults(limit)
                        .getResultStream(),
                    hierarchy
                );
        } catch ( Exception ex ) {
            throw new EntityCRUDExceptionError(ex);
        }
    }

}
