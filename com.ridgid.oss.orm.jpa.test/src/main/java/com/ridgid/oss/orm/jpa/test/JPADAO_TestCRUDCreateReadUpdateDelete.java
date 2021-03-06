package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.common.helper.CollectionHelpers;
import com.ridgid.oss.common.helper.PrimaryKeyAutoGenerationType;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDCreateReadUpdateDelete;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unused")
@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestCRUDCreateReadUpdateDelete<DAO extends JPAEntityCRUDCreateReadUpdateDelete<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPADAO_TestCRUDCreateReadUpdate<DAO, ET, PKT> {

    public JPADAO_TestCRUDCreateReadUpdateDelete(Class<ET> entityClass,
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

    public JPADAO_TestCRUDCreateReadUpdateDelete(Class<ET> entityClass,
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

    public JPADAO_TestCRUDCreateReadUpdateDelete(Class<ET> entityClass,
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

    public JPADAO_TestCRUDCreateReadUpdateDelete(Class<ET> entityClass,
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
    void when_remove_is_called_on_all_records_and_then_all_are_retrieved_the_count_is_zero() {
        setupTestEntities();
        for (ET rec : getDao().findAll(0, Integer.MAX_VALUE)) {
            getDao().delete(rec.getPk());
        }
        getEntityManager().flush();
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        int actual = getDao().findAll(0, Integer.MAX_VALUE).size();
        int expected = 0;
        assertEquals(expected, actual, "After all records removed there should be zero records");
    }

    @Test
    void when_remove_is_called_on_a_record_that_has_any_child_collections_populated_an_exception_is_thrown_and_the_record_is_not_deleted() {

        if (getChildCollectionFieldNames().size() == 0) return;

        setupTestEntities();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        ET unmodifiedEntity = getAllEntitiesFromTestSet(getEntityClass())
                .stream()
                .min(comparing(ET::getPk))
                .get();
        PKT pk = unmodifiedEntity.getPk();
        ET entity = getDao().find(pk);

        for (int i = 0; i < getChildCollectionPopulators().size(); i++) {
            CollectionHelpers.clearCollection(entity, getChildCollectionFieldNames().get(i));
            getDao().update(entity);
            BiConsumer<Integer, ET> populator = getChildCollectionPopulators().get(i);
            populator.accept(0, entity);
            ET entityWithCollectionPopulated = getDao().update(entity);
            assertThrows(RuntimeException.class, () -> getDao().delete(pk));
            entity = getDao().find(pk);
            validateExpectedAndActualEntitiesAreAllEqual
                    (
                            Collections.singletonList(entityWithCollectionPopulated),
                            Collections.singletonList(entity),
                            true
                    );
        }
    }

}
