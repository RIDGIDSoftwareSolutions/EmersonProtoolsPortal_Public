package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;
import com.ridgid.oss.orm.jpa.JPAEntityCRUD;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @param <DAO>
 * @param <ET>
 * @param <PKT>
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class JPADAO_TestBase<DAO extends JPAEntityCRUD<ET, PKT>, ET extends PrimaryKeyedEntity<PKT>, PKT extends Comparable<PKT>> {

    private final Map<Class<?>, List<PrimaryKeyedEntity<?>>> TEST_DATA_MAP = new ConcurrentHashMap<>();

    private final DAO dao;
    private final int numberOfTestRecords;
    private final Class<ET> entityClass;
    private final Class<PKT> entityPrimaryKeyClass;
    private final String schemaName;
    private final String tableName;
    private final List<String> primaryKeyColumnAndFieldNames;
    private final List<String> entityColumnAndFieldNames;
    private final Set<String> foreignKeyFieldNames;

    @Autowired
    private EntityManager entityManager;

    /**
     * @param entityClass
     * @param entityPrimaryKeyClass
     * @param dao
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param numberOfTestRecords
     */
    protected JPADAO_TestBase(Class<ET> entityClass,
                              Class<PKT> entityPrimaryKeyClass,
                              DAO dao,
                              String tableName,
                              List<String> primaryKeyColumnAndFieldNames,
                              List<String> entityColumnAndFieldNames,
                              int numberOfTestRecords) {
        this(
                entityClass,
                entityPrimaryKeyClass,
                dao,
                (String) null,
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
    protected JPADAO_TestBase(Class<ET> entityClass,
                              Class<PKT> entityPrimaryKeyClass,
                              DAO dao,
                              String tableName,
                              List<String> primaryKeyColumnAndFieldNames,
                              List<String> entityColumnAndFieldNames,
                              Set<String> foreignKeyFieldNames,
                              int numberOfTestRecords) {
        this(
                entityClass,
                entityPrimaryKeyClass,
                dao,
                (String) null,
                tableName,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                foreignKeyFieldNames,
                numberOfTestRecords);
        if (foreignKeyFieldNames == null || foreignKeyFieldNames.size() == 0)
            throw new IllegalArgumentException("Foreign Key Field Names must not be null or empty");
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
    protected JPADAO_TestBase(Class<ET> entityClass,
                              Class<PKT> entityPrimaryKeyClass,
                              DAO dao,
                              String schemaName,
                              String tableName,
                              List<String> primaryKeyColumnAndFieldNames,
                              List<String> entityColumnAndFieldNames,
                              int numberOfTestRecords) {
        this(entityClass,
                entityPrimaryKeyClass,
                dao,
                schemaName,
                tableName,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                Collections.EMPTY_SET,
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
    public JPADAO_TestBase(Class<ET> entityClass,
                           Class<PKT> entityPrimaryKeyClass,
                           DAO dao,
                           String schemaName,
                           String tableName,
                           List<String> primaryKeyColumnAndFieldNames,
                           List<String> entityColumnAndFieldNames,
                           Set<String> foreignKeyFieldNames,
                           int numberOfTestRecords) {
        if (foreignKeyFieldNames == null)
            throw new IllegalArgumentException("Foreign Key Field Names must not be null");
        if (schemaName != null && schemaName.isEmpty())
            throw new IllegalArgumentException("schemaName must be null or non-blank");
        if (tableName == null || tableName.length() < 1) throw new IllegalArgumentException("tableName required");
        if (primaryKeyColumnAndFieldNames.size() % 2 != 0)
            throw new IllegalArgumentException("primaryKeyColumnAndFieldNames must be an even number - 1 column name per 1 field name, alternating column name followed by field name");
        if (primaryKeyColumnAndFieldNames.size() % 2 != 0)
            throw new IllegalArgumentException("entityColumnAndFieldNames must be an even number - 1 column name per 1 field name, alternating column name followed by field name");
        this.entityClass = entityClass;
        this.entityPrimaryKeyClass = entityPrimaryKeyClass;
        this.dao = dao;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.primaryKeyColumnAndFieldNames = primaryKeyColumnAndFieldNames;
        this.entityColumnAndFieldNames = entityColumnAndFieldNames;
        this.foreignKeyFieldNames = foreignKeyFieldNames;
        this.numberOfTestRecords = numberOfTestRecords;
    }

    @BeforeEach
    void setUp() {
        initialSetup();
        setupDao();
        setupForeignKeyReferencedTestData();
    }

    private void setupDao() {
        dao.setEntityManager(entityManager);
    }

    @Test
    void daoAndEntityManagerAreNotNull() {
        assertNotNull(dao);
        assertNotNull(entityManager);
    }

    /**
     *
     */
    protected void initialSetup() {
        System.out.println("Initial Set-Up (Nothing to do)");
    }

    @AfterEach
    void tearDown() {
        finalTearDown();
    }

    /**
     *
     */
    protected void finalTearDown() {
        System.out.println("Final Tear-Down (Nothing to do)");
    }

    /**
     *
     */
    protected final void setupTestEntities() {
        setupEntitiesFromPrimaryKeys(numberOfTestRecords, this::primaryKeyGenerator);
    }

    protected final void setupTestEntities(BiConsumer<Integer, ET> fieldsModifierCallback) {
        List<ET> entities = generateTestEntities();
        for (int i = 0; i < entities.size(); i++) {
            ET entity = entities.get(i);
            foreignKeysUpdater(i, entity);
            fieldsModifierCallback.accept(i, entity);
            persistFlushAndDetachEntity(entity);
            storeSetupRecord(entity);
        }
        evictAllFromPersistenceCache();
    }

    /**
     *
     */
    protected final List<ET> generateTestEntities() {
        return generateEntitiesFromPrimaryKeys(numberOfTestRecords, this::primaryKeyGenerator);
    }

    /**
     * @param recordNumber
     * @return
     */
    protected abstract PKT primaryKeyGenerator(int recordNumber);

    /**
     * @param recordNumber
     */
    protected void foreignKeysUpdater(int recordNumber, ET entity) {
        // nothing to do by default
    }

    /**
     * Override to set-up Data records referenced by Foreign Key needed by the Entity under test.
     */
    protected abstract void setupForeignKeyReferencedTestData();

    /**
     * @return
     */
    protected final DAO getDao() {
        return dao;
    }

    /**
     * @return
     */
    protected final EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * @return
     */
    public final int getNumberOfTestRecords() {
        return numberOfTestRecords;
    }

    /**
     * @return
     */
    public final Class<ET> getEntityClass() {
        return this.entityClass;
    }

    /**
     * @return
     */
    public final Class<PKT> getEntityPrimaryKeyClass() {
        return this.entityPrimaryKeyClass;
    }

    /**
     * @return
     */
    public final String getTableName() {
        return tableName;
    }

    /**
     * @return
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @return
     */
    public final List<String> getPrimaryKeyColumnAndFieldNames() {
        return Collections.unmodifiableList(primaryKeyColumnAndFieldNames);
    }

    /**
     * @return
     */
    public final List<String> getPrimaryKeyColumnNames() {
        List<String> primaryKeyColumnNames = new ArrayList<>();
        List<String> primaryKeyFieldNames = new ArrayList<>();
        DAOTestHelpers.separateColumnAndFieldNames(primaryKeyColumnAndFieldNames, primaryKeyColumnNames, primaryKeyFieldNames);
        return Collections.unmodifiableList(primaryKeyColumnNames);
    }

    /**
     * @return
     */
    public final List<String> getPrimaryKeyFieldNames() {
        List<String> primaryKeyColumnNames = new ArrayList<>();
        List<String> primaryKeyFieldNames = new ArrayList<>();
        DAOTestHelpers.separateColumnAndFieldNames(primaryKeyColumnAndFieldNames, primaryKeyColumnNames, primaryKeyFieldNames);
        return Collections.unmodifiableList(primaryKeyFieldNames);
    }

    /**
     * @return
     */
    public final List<String> getEntityColumnAndFieldNames() {
        return Collections.unmodifiableList(entityColumnAndFieldNames);
    }

    /**
     * @return
     */
    public final List<String> getEntityColumnNames() {
        List<String> entityColumnNames = new ArrayList<>();
        List<String> entityFieldNames = new ArrayList<>();
        DAOTestHelpers.separateColumnAndFieldNames(entityColumnAndFieldNames, entityColumnNames, entityFieldNames);
        return Collections.unmodifiableList(entityColumnNames);
    }

    /**
     * @return
     */
    public final List<String> getEntityFieldNames() {
        List<String> entityColumnNames = new ArrayList<>();
        List<String> entityFieldNames = new ArrayList<>();
        DAOTestHelpers.separateColumnAndFieldNames(entityColumnAndFieldNames, entityColumnNames, entityFieldNames);
        return Collections.unmodifiableList(entityFieldNames);
    }

    public Set<String> getForeignKeyFieldNames() {
        return Collections.unmodifiableSet(foreignKeyFieldNames);
    }

    /**
     * @param entityClass
     * @param idx
     * @param <T2>
     * @param <PKT2>
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    T2 getEntityFromTestSet(Class<T2> entityClass,
                            int idx) {
        return (T2) TEST_DATA_MAP.get(entityClass).get(idx);
    }

    /**
     * @param entityClass
     * @param <T2>
     * @param <PKT2>
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    List<T2> getAllEntitiesFromTestSet(Class<T2> entityClass) {
        return (List<T2>) TEST_DATA_MAP.get(entityClass);
    }

    /**
     * @param numRecsToGenerate
     * @param generatorFunction
     * @param <T2>
     * @param <PKT2>
     */
    protected final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    void setupEntities(int numRecsToGenerate,
                       Function<Integer, T2> generatorFunction) {
        for (int i = 0; i < numRecsToGenerate; i++) {
            T2 entity = generatorFunction.apply(i);
            persistFlushAndDetachEntity(entity);
            storeSetupRecord(entity);
        }
        evictAllFromPersistenceCache();
    }

    public final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    void persistFlushAndDetachEntity(T2 entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.detach(entity);
    }

    public final void evictAllFromPersistenceCache() {
        entityManager.getEntityManagerFactory().getCache().evictAll();
    }

    /**
     * @param numberOfRecords
     * @param entityClass
     * @param primaryKeyGenerator
     * @param <T2>
     * @param <PKT2>
     */
    public final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    void setupEntitiesFromPrimaryKeys(int numberOfRecords,
                                      Class<T2> entityClass,
                                      Class<PKT2> primaryKeyClass,
                                      Function<Integer, PKT2> primaryKeyGenerator) {
        setupEntitiesFromPrimaryKeys(
                numberOfRecords,
                DAOTestHelpers.getConstructorForEntityOrThrowRuntimeException(entityClass, primaryKeyClass),
                primaryKeyGenerator);
    }

    /**
     * @param numberOfRecords
     * @param primaryKeyGenerator
     */
    public final void setupEntitiesFromPrimaryKeys(int numberOfRecords,
                                                   Function<Integer, PKT> primaryKeyGenerator) {
        setupEntitiesFromPrimaryKeys(
                numberOfRecords,
                DAOTestHelpers.getConstructorForEntityOrThrowRuntimeException(entityClass, entityPrimaryKeyClass),
                primaryKeyGenerator,
                this::foreignKeysUpdater);
    }

    /**
     * @param numberOfRecords
     * @param primaryKeyGenerator
     * @return
     */
    public final List<ET> generateEntitiesFromPrimaryKeys(int numberOfRecords,
                                                          Function<Integer, PKT> primaryKeyGenerator) {
        return DAOTestHelpers.generateEntitiesFromPrimaryKeys(
                numberOfRecords,
                DAOTestHelpers.getConstructorForEntityOrThrowRuntimeException(entityClass, entityPrimaryKeyClass),
                primaryKeyGenerator);
    }

    private <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    void setupEntitiesFromPrimaryKeys(int numberOfTestRecords,
                                      Constructor<T2> entityConstructor,
                                      Function<Integer, PKT2> primaryKeyGenerator) {
        setupEntitiesFromPrimaryKeys(numberOfTestRecords, entityConstructor, primaryKeyGenerator, null);
    }

    private <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    void setupEntitiesFromPrimaryKeys(int numberOfTestRecords,
                                      Constructor<T2> entityConstructor,
                                      Function<Integer, PKT2> primaryKeyGenerator,
                                      BiConsumer<Integer, T2> foreignKeysUpdater) {
        setupEntities(numberOfTestRecords, (idx) -> {
            try {
                T2 rv = entityConstructor.newInstance(primaryKeyGenerator.apply(idx));
                DAOTestHelpers.populateBaseFields(idx, rv);
                if (foreignKeysUpdater != null) foreignKeysUpdater.accept(idx, rv);
                return rv;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @param entity
     * @param <T2>
     * @param <PKT2>
     */
    protected final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    void storeSetupRecord(T2 entity) {
        List<PrimaryKeyedEntity<?>> testDataList = TEST_DATA_MAP.computeIfAbsent(entity.getClass(), k -> new ArrayList<>());
        testDataList.add(entity);
    }

    /**
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param entity
     * @param <T2>
     * @param <PKT2>
     * @return
     */
    public final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    Query createNativeInsertQueryFrom(String tableName,
                                      List<String> primaryKeyColumnAndFieldNames,
                                      List<String> entityColumnAndFieldNames,
                                      T2 entity) {
        return createNativeInsertQueryFrom(
                null,
                tableName,
                primaryKeyColumnAndFieldNames,
                entityColumnAndFieldNames,
                entity);
    }

    /**
     * @param schemaName
     * @param tableName
     * @param primaryKeyColumnAndFieldNames
     * @param entityColumnAndFieldNames
     * @param entity
     * @param <T2>
     * @param <PKT2>
     * @return
     */
    public final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    Query createNativeInsertQueryFrom(String schemaName,
                                      String tableName,
                                      List<String> primaryKeyColumnAndFieldNames,
                                      List<String> entityColumnAndFieldNames,
                                      T2 entity) {
        List<String> primaryKeyColumnNames = new ArrayList<>();
        List<String> primaryKeyFieldNames = new ArrayList<>();
        DAOTestHelpers.separateColumnAndFieldNames(primaryKeyColumnAndFieldNames, primaryKeyColumnNames, primaryKeyFieldNames);

        List<String> entityColumnNames = new ArrayList<>();
        List<String> entityFieldNames = new ArrayList<>();
        DAOTestHelpers.separateColumnAndFieldNames(entityColumnAndFieldNames, entityColumnNames, entityFieldNames);

        Query query
                = schemaName == null
                ? entityManager.createNativeQuery
                (
                        DAOTestHelpers.createNativeInsertQueryStringFrom(
                                tableName,
                                primaryKeyColumnNames,
                                primaryKeyFieldNames,
                                entityColumnNames,
                                entityFieldNames)
                )
                : entityManager.createNativeQuery
                (
                        DAOTestHelpers.createNativeInsertQueryStringFrom(
                                schemaName,
                                tableName,
                                primaryKeyColumnNames,
                                primaryKeyFieldNames,
                                entityColumnNames,
                                entityFieldNames)
                );

        DAOTestHelpers.setInsertQueryColumnValues(
                query,
                entity.getPk(),
                0,
                primaryKeyFieldNames);
        DAOTestHelpers.setInsertQueryColumnValues(
                query,
                entity,
                primaryKeyColumnNames.size(),
                entityFieldNames);

        return query;
    }

    public final <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    Query createNativeDeleteQueryFrom(String tableName) {
        return createNativeDeleteQueryFrom(tableName);
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    public final Query createNativeDeleteQueryFrom(String schemaName, String tableName) {
        Query query = entityManager.createNativeQuery(
                schemaName == null
                        ? DAOTestHelpers.createNativeDeleteQueryStringFrom(tableName)
                        : DAOTestHelpers.createNativeDeleteQueryStringFrom(schemaName, tableName)
        );
        return query;
    }

    /**
     * @param actual
     * @param expected
     */
    public final void validateExpectedAndActualEntitiesAreAllEqual(List<ET> actual, List<ET> expected) {
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(
                    expected.get(i).getPk(),
                    actual.get(i).getPk(),
                    "Primary keys are NOT equal"
            );
            assertTrue(
                    DAOTestHelpers.fieldsAreEqual(
                            getEntityFieldNames(),
                            expected.get(i),
                            actual.get(i),
                            errors),
                    () -> "All fields are not equal:\n" + errors.stream().collect(joining(",\n\t", "\t", "")));
        }
    }

}
