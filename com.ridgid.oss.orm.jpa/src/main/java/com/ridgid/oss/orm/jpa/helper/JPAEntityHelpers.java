package com.ridgid.oss.orm.jpa.helper;

import com.ridgid.oss.common.helper.FieldReflectionHelpers;
import com.ridgid.oss.orm.entity.PrimaryKeyedEntity;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 *
 */
@SuppressWarnings({"WeakerAccess", "unused", "JavaDoc"})
public final class JPAEntityHelpers {

    private JPAEntityHelpers() {
    }

    /**
     * @param numRecsToGenerate
     * @param generatorFunction
     * @param <T2>
     * @param <PKT2>
     */
    public static <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    List<T2> generateEntities(int numRecsToGenerate,
                              Function<Integer, T2> generatorFunction) {
        return IntStream
                .range(0, numRecsToGenerate)
                .mapToObj(generatorFunction::apply)
                .collect(toList());
    }

    /**
     * @param classToConstruct
     * @param primaryKeyClass
     * @param <T2>
     * @param <PKT2>
     * @return
     */
    public static <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    Constructor<T2> getConstructorForEntityOrThrowRuntimeException(Class<T2> classToConstruct,
                                                                   Class<PKT2> primaryKeyClass) {
        try {
            return classToConstruct.getConstructor(primaryKeyClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param classToConstruct
     * @param <T2>
     * @param <PKT2>
     * @return
     */
    public static <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    Constructor<T2> getConstructorForEntityOrThrowRuntimeException(Class<T2> classToConstruct) {
        try {
            return classToConstruct.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param numberOfTestRecords
     * @param entityConstructor
     * @param primaryKeyGenerator
     * @param <T2>
     * @param <PKT2>
     * @return
     */
    public static <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    List<T2> generateEntitiesFromPrimaryKeys(int numberOfTestRecords,
                                             Constructor<T2> entityConstructor,
                                             Function<Integer, PKT2> primaryKeyGenerator) {
        return generateEntities(numberOfTestRecords, (idx) -> {
            try {
                T2 rv = entityConstructor.newInstance(primaryKeyGenerator.apply(idx));
                JPAFieldPopulationHelpers.populateBaseFields
                        (
                                idx,
                                rv
                        );
                return rv;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T2 extends PrimaryKeyedEntity<PKT2>, PKT2 extends Comparable<PKT2>>
    List<T2> generateEntitiesFromPrimaryKeys(int numberOfTestRecords,
                                             Constructor<T2> entityConstructor) {
        return generateEntities(numberOfTestRecords, (idx) -> {
            try {
                T2 rv = entityConstructor.newInstance();
                JPAFieldPopulationHelpers.populateBaseFields
                        (
                                idx,
                                rv
                        );
                return rv;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @param columnAndFieldNames
     * @param outColumnNames
     * @param outFieldNames
     */
    public static void separateColumnAndFieldNames(List<String> columnAndFieldNames,
                                                   List<String> outColumnNames,
                                                   List<String> outFieldNames) {
        for (int i = 0; i < columnAndFieldNames.size() - 1; i += 2) {
            outColumnNames.add(columnAndFieldNames.get(i));
            outFieldNames.add(columnAndFieldNames.get(i + 1));
        }
    }

    public static void unproxy(Object obj) {
        FieldReflectionHelpers.applyToFieldsRecursively(obj, JPAEntityHelpers::unproxyProxiedValue);

    }

    private static Object unproxyProxiedValue(Object o) {
        if (o instanceof HibernateProxy)
            return Hibernate.unproxy(o);
        return null;
    }
}