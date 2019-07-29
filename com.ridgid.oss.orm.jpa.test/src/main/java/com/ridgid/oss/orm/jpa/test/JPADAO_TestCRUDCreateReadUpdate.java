package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.common.helper.PrimaryKeyAutoGenerationType;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateReadUpdate;
import com.ridgid.oss.orm.jpa.helper.JPAFieldModificationHelpers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"JavaDoc", "unused"})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestCRUDCreateReadUpdate<DAO extends JPAEntityCRUDCreateReadUpdate<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPADAO_TestCRUDCreateRead<DAO, ET, PKT> {

    /**
     * @param entityClass
     * @param entityPrimaryKeyClass
     * @param dao
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param numberOfTestRecords
     */
    public JPADAO_TestCRUDCreateReadUpdate(Class<ET> entityClass,
                                           Class<PKT> entityPrimaryKeyClass,
                                           DAO dao,
                                           String tableName,
                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                tableName,
                primaryKeyAutoGenerationType,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                numberOfTestRecords);
    }

    /**
     * @param entityClass
     * @param entityPrimaryKeyClass
     * @param dao
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param foreignKeyFieldNames
     * @param numberOfTestRecords
     */
    public JPADAO_TestCRUDCreateReadUpdate(Class<ET> entityClass,
                                           Class<PKT> entityPrimaryKeyClass,
                                           DAO dao,
                                           String tableName,
                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           Set<String> foreignKeyFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                tableName,
                primaryKeyAutoGenerationType,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                foreignKeyFieldNames,
                numberOfTestRecords);
    }

    /**
     * @param entityClass
     * @param entityPrimaryKeyClass
     * @param dao
     * @param schemaName
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param numberOfTestRecords
     */
    public JPADAO_TestCRUDCreateReadUpdate(Class<ET> entityClass,
                                           Class<PKT> entityPrimaryKeyClass,
                                           DAO dao,
                                           String schemaName,
                                           String tableName,
                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                schemaName,
                tableName,
                primaryKeyAutoGenerationType,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                numberOfTestRecords);
    }

    /**
     * @param entityClass
     * @param entityPrimaryKeyClass
     * @param dao
     * @param schemaName
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param foreignKeyFieldNames
     * @param numberOfTestRecords
     */
    public JPADAO_TestCRUDCreateReadUpdate(Class<ET> entityClass,
                                           Class<PKT> entityPrimaryKeyClass,
                                           DAO dao,
                                           String schemaName,
                                           String tableName,
                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           Set<String> foreignKeyFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                schemaName,
                tableName,
                primaryKeyAutoGenerationType,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                foreignKeyFieldNames,
                numberOfTestRecords);
    }

    @Test
    void when_update_is_called_on_all_existing_records_to_update_some_fields_the_records_read_back_reflect_the_changes() {
        setupTestEntities();
        for (ET rec : getAllEntitiesFromTestSet(getEntityClass()))
            JPAFieldModificationHelpers.modifyFields
                    (
                            rec,
                            getEntityFieldNames(),
                            getForeignKeyFieldNames()
                    );
        for (ET rec : getDao().findAll(0, Integer.MAX_VALUE)) {
            JPAFieldModificationHelpers.modifyFields
                    (
                            rec,
                            getEntityFieldNames(),
                            getForeignKeyFieldNames()
                    );
            getDao().update(rec);
        }
        getEntityManager().flush();
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        findAndCompareAllWithoutSetup(false);
    }

    @Test
    void when_update_is_called_on_all_existing_records_to_modify_collections_and_then_read_back_the_collections_reflect_the_changes() {
        setupTestEntities();
        int i = 0;
        for (ET rec : getAllEntitiesFromTestSet(getEntityClass())) {
            deterministicallyMutateChildCollections(i, rec);
            i++;
        }
        i = 0;
        for (ET rec : getDao().findAll(0, Integer.MAX_VALUE)) {
            deterministicallyMutateChildCollections(i, rec);
            i++;
            getDao().update(rec);
        }
        getEntityManager().flush();
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        findAndCompareAllWithoutSetup(true);
    }

    private void deterministicallyMutateChildCollections(int idx, ET rec) {
        getChildCollectionMutators().forEach(m -> m.accept(idx, rec));
    }
}
