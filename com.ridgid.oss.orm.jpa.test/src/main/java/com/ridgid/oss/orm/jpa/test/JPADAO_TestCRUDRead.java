package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUDRead;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.Query;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestCRUDRead<DAO extends JPAEntityCRUDRead<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>>
        extends JPADAO_TestBase<DAO, ET, PKT> {

    public JPADAO_TestCRUDRead(Class<ET> entityClass,
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

    public JPADAO_TestCRUDRead(Class<ET> entityClass,
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

    @Test
    void when_find_is_called_it_retrieves_the_previously_stored_record_for_the_given_primary_key() {
        setupTestEntities();
        PKT expectedRecKey
                = getEntityFromTestSet
                (
                        getEntityClass(),
                        0
                ).getPk();
        ET actualRec = getDao().find(expectedRecKey);
        assertEquals(
                expectedRecKey,
                actualRec.getPk(),
                "Primary Key of retrieved record does not match primary key requested");
        expectedRecKey
                = getEntityFromTestSet
                (
                        getEntityClass(),
                        getNumberOfTestRecords() - 1
                ).getPk();
        actualRec = getDao().find(expectedRecKey);
        assertEquals(
                expectedRecKey,
                actualRec.getPk(),
                "Primary Key of retrieved record does not match primary key requested");
    }

    @Test
    void when_findAll_is_called_it_returns_all_existing_records_in_the_desired_range() {
        setupTestEntities();
        findAndCompareAllWithoutSetup();
    }

    @Test
    void it_retrieves_records_that_were_not_added_through_jpa_from_the_correct_fields() {

        // Arrange & Set-Up Expectations
        List<ET> expected
                = generateTestEntities()
                .stream()
                .sorted(Comparator.comparing(ET::getPk))
                .collect(toList());
        for (ET entity : expected) {
            Query query = createNativeInsertQueryFrom(
                    getSchemaName(),
                    getTableName(),
                    getPrimaryKeyColumnAndFieldNames(),
                    getEntityColumnAndFieldNames(),
                    entity);
            if (query.executeUpdate() < 1)
                throw new RuntimeException("Unable to insert record in preparation for test through direct insert SQL query");
        }

        // Act & Get Actual Values
        List<ET> actual
                = getDao().findAll(0, Integer.MAX_VALUE)
                .stream()
                .sorted(Comparator.comparing(ET::getPk))
                .collect(toList());

        // Assert that everything is equal
        validateExpectedAndActualEntitiesAreAllEqual(actual, expected);
    }

    /**
     *
     */
    protected final void findAndCompareAllWithoutSetup() {
        List<ET> actual = getDao().findAll(0, Integer.MAX_VALUE).stream().sorted(comparing(ET::getPk)).collect(toList());
        List<ET> expected = getAllEntitiesFromTestSet(getEntityClass()).stream().sorted(comparing(ET::getPk)).collect(toList());
        validateExpectedAndActualEntitiesAreAllEqual(actual, expected);
    }

}
