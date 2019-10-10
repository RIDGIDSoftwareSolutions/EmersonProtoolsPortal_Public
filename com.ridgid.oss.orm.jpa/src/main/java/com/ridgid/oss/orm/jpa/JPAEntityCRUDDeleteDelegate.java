package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUDDelete;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionNotFound;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")
final class JPAEntityCRUDDeleteDelegate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        implements
        JPAEntityCRUDDelegateRequired<ET, PKT>,
        JPAEntityCRUDDeleteDelegateRequired<ET, PKT>,
        EntityCRUDDelete<ET, PKT> {

    private final JPAEntityCRUDDelegate<ET, PKT> baseDelegate;

    JPAEntityCRUDDeleteDelegate(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = baseDelegate;
    }

    JPAEntityCRUDDeleteDelegate(Class<ET> classType,
                                Class<PKT> pkType) {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType);
    }

    JPAEntityCRUDDeleteDelegate(Class<ET> classType,
                                Class<PKT> pkType,
                                String pkName) {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, pkName);
    }

    JPAEntityCRUDDeleteDelegate(Class<ET> classType,
                                Class<PKT> pkType,
                                short loadBatchSize) {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, loadBatchSize);
    }

    JPAEntityCRUDDeleteDelegate(Class<ET> classType,
                                Class<PKT> pkType,
                                String pkName,
                                short loadBatchSize) {
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
    public Class<ET> getClassType() {
        return baseDelegate.getClassType();
    }

    @Override
    public Class<PKT> getPkType() {
        return baseDelegate.getPkType();
    }

    @Override
    public void flushContext() {
        baseDelegate.flushContext();
    }

    @Override
    public void clearContext() {
        baseDelegate.clearContext();
    }

    /**
     * Deletes the entity one the given primary key pk from the persistent storage
     *
     * @param pk primary key of the entity to delete from persistent storage
     * @throws EntityCRUDExceptionError    if there is an issue deleting/removing the record (specific "cause" may vary)
     * @throws EntityCRUDExceptionNotFound if there is no entity ET one the primary key PK in the persistence store
     */
    @Override
    public void delete(PKT pk, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError, EntityCRUDExceptionNotFound {
        try {
            ET entity = baseDelegate.getEntityManager().find(baseDelegate.getClassType(), pk);
            if (entity == null) throw new EntityCRUDExceptionNotFound();
            baseDelegate.getEntityManager().remove(entity);
            baseDelegate.getEntityManager().flush();
        } catch (EntityCRUDExceptionNotFound ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw baseDelegate.enhanceExceptionWithEntityManagerNullCheck(ex);
        }
    }
}
