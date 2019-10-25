package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUDUpdate;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.exception.EntityCRUDExceptionError;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "WeakerAccess"})
public class JPAEntityCRUDUpdateDelegate<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
    implements
    JPAEntityCRUDDelegateRequired<ET, PKT>,
    JPAEntityCRUDUpdateDelegateRequired<ET, PKT>,
    EntityCRUDUpdate<ET, PKT>
{

    private final JPAEntityCRUDDelegate<ET, PKT> baseDelegate;

    public JPAEntityCRUDUpdateDelegate(JPAEntityCRUDDelegate<ET, PKT> baseDelegate) {
        this.baseDelegate = baseDelegate;
    }

    public JPAEntityCRUDUpdateDelegate(Class<ET> classType,
                                       Class<PKT> pkType)
    {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType);
    }

    public JPAEntityCRUDUpdateDelegate(Class<ET> classType,
                                       Class<PKT> pkType,
                                       String pkName)
    {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, pkName);
    }

    public JPAEntityCRUDUpdateDelegate(Class<ET> classType,
                                       Class<PKT> pkType,
                                       short loadBatchSize)
    {
        this.baseDelegate = new JPAEntityCRUDDelegate<>(classType, pkType, loadBatchSize);
    }

    public JPAEntityCRUDUpdateDelegate(Class<ET> classType,
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
    public ET initializeAndDetach(ET entity, HierarchyProcessor<ET> hierarchy) {
        return baseDelegate.initializeAndDetach(entity, hierarchy);
    }

    @Override
    public Optional<ET> load(PKT pk) {
        return baseDelegate.load(pk);
    }

    @Override
    public Stream<ET> loadBatch(List<PKT> pkList) {
        return baseDelegate.loadBatch(pkList);
    }

    @Override
    public short getLoadBatchSize() {
        return baseDelegate.getLoadBatchSize();
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

    /**
     * Updates the entity in the persistent storage one the same primary key of given entity so that all the persistent field values in the persistent storage for the entity match the values of the given entity.
     *
     * @param entity entity to update in the persistent storage
     * @return the optional entity one any values that were updated by the persistent storage layer upon successful update of the entity in the persistent storage; if the entity does not currently exist in the persistent storage returns an empty optional.
     * @throws EntityCRUDExceptionError if there is an issue updating the record (specific "cause" may vary)
     */
    @Override
    public Optional<ET> optionalUpdate(ET entity, HierarchyProcessor<ET> hierarchy) throws EntityCRUDExceptionError {
        try {
            if ( !baseDelegate.getEntityManager().contains(entity) ) {
                if ( baseDelegate.getEntityManager().find(baseDelegate.classType, entity.getPk()) == null )
                    return Optional.empty();
                baseDelegate.getEntityManager().merge(entity);
            }
            baseDelegate.getEntityManager().flush();
            return Optional.of(entity);
        } catch ( Exception ex ) {
            throw baseDelegate.enhanceExceptionWithEntityManagerNullCheck(ex);
        }
    }
}
