package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateRead;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.Query;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestCRUDCreateRead<DAO extends JPAEntityCRUDCreateRead<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPADAO_TestCRUDRead<DAO, ET, PKT> {

    /**
     * @param entityClass
     * @param entityPrimaryKeyClass
     * @param dao
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param numberOfTestRecords
     */
    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
     *
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
    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
    void when_add_is_called_it_retrieves_all_added_records_from_the_db() {
        Query query
                = createNativeDeleteQueryFrom
                (
                        getSchemaName(),
                        getTableName()
                );
        assertEquals(0, getDao().findAll(0, 10).size(), "Should be 0 records found");
        for (ET rec : generateTestEntities()) {
            storeSetupRecord(rec);
            assertDoesNotThrow(() -> getDao().add(rec));
        }
        findAndCompareAllWithoutSetup();
    }
}
