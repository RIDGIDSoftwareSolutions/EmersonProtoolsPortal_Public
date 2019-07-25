package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.common.helper.PrimaryKeyAutoGenerationType;
import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateRead;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"JavaDoc", "unused"})
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
    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
    public JPADAO_TestCRUDCreateRead(Class<ET> entityClass,
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
    void when_findAll_is_called_after_add_it_retrieves_all_added_records_from_the_db() {
        addRecordsOneAtATimeThroughAddMethodAndThenReadBackAndVerify(false);
    }

    @Test
    void when_add_is_called_with_items_containing_collections_Of_child_entities_it_persists_the_children_also() {
        if (getChildCollectionFieldNames().size() == 0)
            return; // Test auto-succeeds if there are no designated child collections to test
        addRecordsOneAtATimeThroughAddMethodAndThenReadBackAndVerify(true);
    }

    private void addRecordsOneAtATimeThroughAddMethodAndThenReadBackAndVerify(boolean validateChildCollections) {
        createNativeDeleteQueryFrom
                (
                        getSchemaName(),
                        getTableName()
                )
                .executeUpdate();
        assertEquals(0, getDao().findAll(0, 10).size(), "Should be 0 records found");
        int childCollectionIndex = 0;
        int recordNumber = 0;
        for (ET rec : generateTestEntities()) {
            if (validateChildCollections) {
                deterministicallyPopulateChildCollections(childCollectionIndex, rec);
                childCollectionIndex++;
            }
            foreignKeysUpdater(recordNumber, rec);
            storeSetupRecord(rec);
            assertDoesNotThrow(() -> getDao().add(rec));
            recordNumber++;
        }
        findAndCompareAllWithoutSetup(validateChildCollections);
    }

    private void deterministicallyPopulateChildCollections(int idx, ET rec) {
        getChildCollectionPopulators().forEach(p -> p.accept(idx, rec));
    }

}
