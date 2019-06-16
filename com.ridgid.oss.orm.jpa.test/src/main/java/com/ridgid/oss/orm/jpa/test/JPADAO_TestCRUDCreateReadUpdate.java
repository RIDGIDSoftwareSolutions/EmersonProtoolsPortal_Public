package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateReadUpdate;
import com.ridgid.oss.orm.jpa.helper.JPAFieldModificationHelpers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

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
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                tableName,
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
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           Set<String> foreignKeyFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                tableName,
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
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                schemaName,
                tableName,
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
                                           List<String> primaryKeyColumnAndFieldNames,
                                           List<String> entityColumnAndFieldNames,
                                           Set<String> foreignKeyFieldNames,
                                           int numberOfTestRecords) {
        super(entityClass,
                entityPrimaryKeyClass,
                dao,
                schemaName,
                tableName,
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
}
