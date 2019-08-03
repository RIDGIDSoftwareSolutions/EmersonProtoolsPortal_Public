package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.hierarchy.HierarchyProcessor;
import com.ridgid.oss.orm.EntityCRUD;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface JPAEntityCRUDDelegateRequired<ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends EntityCRUD<ET, PKT> {
    /**
     * Sets the JPA entity manager used by the DAO
     *
     * @param entityManager that the DAO should use for all JPA Entity Manager actions
     */
    void setEntityManager(EntityManager entityManager);

    /**
     * Gets the JPA entity manager set for the DAO
     *
     * @return JPA EntityManager or null if not set
     */
    EntityManager getEntityManager();

    Class<ET> getClassType();

    Class<PKT> getPkType();

    @Override
    ET initializeAndDetach(ET entity,
                           HierarchyProcessor<ET> hierarchy);

    @Override
    Optional<ET> load(PKT pk);

    @Override
    Stream<ET> loadBatch(List<PKT> pkList);

    @Override
    short getLoadBatchSize();

    @Override
    ET initialize(ET entity,
                  HierarchyProcessor<ET> hierarchy);

    @Override
    ET detach(ET entity,
              HierarchyProcessor<ET> hierarchy);
}
