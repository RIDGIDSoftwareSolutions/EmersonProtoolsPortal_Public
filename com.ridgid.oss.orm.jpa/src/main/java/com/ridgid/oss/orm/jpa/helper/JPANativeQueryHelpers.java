package com.ridgid.oss.orm.jpa.helper;

import com.ridgid.oss.common.helper.FieldReflectionHelpers;
import com.ridgid.oss.common.helper.PrimaryKeyAutoGenerationType;
import com.ridgid.oss.orm.entity.CreateModifyTracking;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ridgid.oss.common.helper.FieldReflectionHelpers.getFieldValueOrThrowRuntimeException;
import static com.ridgid.oss.common.helper.PrimaryKeyAutoGenerationType.IDENTITY;

/**
 *
 */
@SuppressWarnings({"WeakerAccess", "unused", "JavaDoc"})
public final class JPANativeQueryHelpers {

    private JPANativeQueryHelpers() {
    }

    /**
     * @param tableName
     * @param primaryKeyColumnNames
     * @param primaryKeyFieldNames
     * @param entityColumnNames
     * @param entityFieldNames
     * @return
     */
    public static String createNativeInsertQueryStringFrom(String tableName,
                                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                                           List<String> primaryKeyColumnNames,
                                                           List<String> primaryKeyFieldNames,
                                                           List<String> entityColumnNames,
                                                           List<String> entityFieldNames) {
        return createNativeInsertQueryStringFrom
                (
                        null,
                        tableName,
                        primaryKeyAutoGenerationType,
                        primaryKeyColumnNames,
                        primaryKeyFieldNames,
                        entityColumnNames,
                        entityFieldNames
                );
    }

    public static String createNativeInsertQueryStringFrom(String tableName,
                                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                                           List<String> primaryKeyColumnNames,
                                                           List<String> primaryKeyFieldNames,
                                                           List<String> entityColumnNames,
                                                           List<String> entityFieldNames,
                                                           List<String> additionalColumnNames) {
        return createNativeInsertQueryStringFrom
                (
                        null,
                        tableName,
                        primaryKeyAutoGenerationType,
                        primaryKeyColumnNames,
                        primaryKeyFieldNames,
                        entityColumnNames,
                        entityFieldNames,
                        additionalColumnNames
                );
    }

    public static String createNativeInsertQueryStringFrom(String schemaName,
                                                           String tableName,
                                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                                           List<String> primaryKeyColumnNames,
                                                           List<String> primaryKeyFieldNames,
                                                           List<String> entityColumnNames,
                                                           List<String> entityFieldNames) {
        //noinspection unchecked
        return createNativeInsertQueryStringFrom
                (
                        null,
                        tableName,
                        primaryKeyAutoGenerationType,
                        primaryKeyColumnNames,
                        primaryKeyFieldNames,
                        entityColumnNames,
                        entityFieldNames,
                        Collections.EMPTY_LIST
                );
    }

    public static String createNativeInsertQueryStringFrom(String schemaName,
                                                           String tableName,
                                                           PrimaryKeyAutoGenerationType primaryKeyAutoGenerationType,
                                                           List<String> primaryKeyColumnNames,
                                                           List<String> primaryKeyFieldNames,
                                                           List<String> entityColumnNames,
                                                           List<String> entityFieldNames,
                                                           List<String> additionalColumnNames) {
        Objects.requireNonNull(tableName, "tableName must be non-null");
        if (primaryKeyColumnNames.size() != primaryKeyFieldNames.size())
            throw new RuntimeException("primary key column names and primary key field names must match in number");
        if (entityColumnNames.size() != entityFieldNames.size())
            throw new RuntimeException("entity column names and entity field names must match in number");
        String schemaPart = schemaName == null ? "" : "\"" + schemaName + "\".";
        String fieldsPart
                = Stream.concat
                (
                        Stream.concat
                                (
                                        primaryKeyAutoGenerationType.equals(IDENTITY)
                                                ? Stream.empty()
                                                : primaryKeyColumnNames.stream(),
                                        entityColumnNames.stream()
                                ),
                        additionalColumnNames.stream()
                )
                .map(cn -> "\"" + cn + "\"")
                .collect(Collectors.joining(","));
        String valuesPart
                = Stream.concat
                (
                        Stream.concat
                                (
                                        primaryKeyAutoGenerationType.equals(IDENTITY)
                                                ? Stream.empty()
                                                : primaryKeyColumnNames.stream(),
                                        entityColumnNames.stream()
                                ),
                        additionalColumnNames.stream()
                )
                .map(fn -> "?")
                .collect(Collectors.joining(","));
        return String.format
                (
                        "insert into %s\"%s\" ( %s ) values ( %s )",
                        schemaPart,
                        tableName,
                        fieldsPart,
                        valuesPart
                );
    }

    /**
     * @param q
     * @param offset
     * @param fieldNames
     * @param obj
     */
    public static void setInsertQueryColumnValues(Query q,
                                                  Object obj,
                                                  int offset,
                                                  List<String> fieldNames) {
        for (int i = 0; i < fieldNames.size(); i++) {
            Optional<Map.Entry<Object, Field>> objectField = FieldReflectionHelpers.determineObjectAndFieldForPathIntoObject(obj, fieldNames.get(i));
            Optional<Object> valueObj = objectField.map(Map.Entry::getKey);
            Optional<Field> f = objectField.map(Map.Entry::getValue);
            if (!f.isPresent())
                q.setParameter(offset + i + 1, null);
            else if (f.get().isAnnotationPresent(Convert.class))
                setConvertedParameterValue(q, valueObj.orElse(null), offset, i, f.get());
            else
                setBasicParameterValue(q, valueObj.orElse(null), offset, i, f.get());
        }
    }

    public static void setInsertQueryColumnValues(Query q,
                                                  Object obj,
                                                  int offset,
                                                  List<String> additionalColumnNames,
                                                  List<Function<Object, Object>> additionalColumnGetters) {
        for (int i = 0; i < additionalColumnNames.size(); i++)
            setBasicParameterValue(q, obj, offset, i, additionalColumnGetters.get(i));
    }

    private static void setConvertedParameterValue(Query q,
                                                   Object obj,
                                                   int offset,
                                                   int i,
                                                   Field f) {
        try {
            @SuppressWarnings("unchecked")
            AttributeConverter converter
                    = (AttributeConverter) f
                    .getAnnotation(Convert.class)
                    .converter()
                    .getConstructor()
                    .newInstance();
            //noinspection unchecked
            q.setParameter
                    (
                            i + offset + 1,
                            converter.convertToDatabaseColumn(getFieldValueOrThrowRuntimeException(obj, f))
                    );
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setBasicParameterValue(Query q,
                                               Object obj,
                                               int offset,
                                               int i,
                                               Field f) {
        Class<?> ft = f.getType();
        if (ft == Calendar.class)
            setCalendarParameterValue(q, obj, i + offset + 1, f);
        else if (ft == Date.class)
            setDateParameterValue(q, obj, i + offset + 1, f);
        else
            q.setParameter(i + offset + 1, getFieldValueOrThrowRuntimeException(obj, f));
    }

    /**
     * @param q
     * @param obj
     * @param offset
     * @param i
     * @param getter
     */
    private static void setBasicParameterValue(Query q,
                                               Object obj,
                                               int offset,
                                               int i,
                                               Function<Object, Object> getter) {
        Object val = getter.apply(obj);
        if (val instanceof Calendar || val instanceof Date)
            throw new IllegalArgumentException("Embedded required fields of type Date or Calendar not supported. Used java.time package Date/Time types instead");
        else
            q.setParameter(i + offset + 1, val);
    }

    /**
     * @param query
     * @param obj
     * @param idx
     * @param field
     */
    public static void setDateParameterValue(Query query,
                                             Object obj,
                                             int idx,
                                             Field field) {
        TemporalType tt = JPAFieldReflectionHelpers.getJPATemporalTypeForAmbiguousTemporalField(field);
        query.setParameter(idx, (Date) getFieldValueOrThrowRuntimeException(obj, field), tt);
    }

    /**
     * @param query
     * @param obj
     * @param idx
     * @param field
     */
    public static void setCalendarParameterValue(Query query,
                                                 Object obj,
                                                 int idx,
                                                 Field field) {
        TemporalType tt = JPAFieldReflectionHelpers.getJPATemporalTypeForAmbiguousTemporalField(field);
        query.setParameter(idx, (Calendar) getFieldValueOrThrowRuntimeException(obj, field), tt);
    }

    /**
     * @param tableName
     * @return
     */
    public static String createNativeDeleteQueryStringFrom(String tableName) {
        return "delete from \"" + tableName + "\"";
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    public static String createNativeDeleteQueryStringFrom(String schemaName,
                                                           String tableName) {
        if (schemaName == null
                || schemaName.isEmpty()
                || schemaName.matches(" +")
                || schemaName.trim().isEmpty())
            return createNativeDeleteQueryStringFrom(tableName);
        return "delete from \"" + schemaName + "\".\"" + tableName + "\"";
    }

    /**
     * @param entityClass
     * @param additionalColumnNames
     * @param additionalColumnGetters
     */
    public static void determineEmbeddedAdditionalRequiredFields(Class<?> entityClass,
                                                                 List<String> additionalColumnNames,
                                                                 List<Function<Object, Object>> additionalColumnGetters) {
        if (CreateModifyTracking.class.isAssignableFrom(entityClass)) {
            additionalColumnNames.addAll
                    (
                            Arrays.asList
                                    (
                                            "Created",
                                            "CreatedBy",
                                            "Modified",
                                            "ModifiedBy"
                                    )
                    );
            additionalColumnGetters.addAll
                    (
                            Arrays.asList
                                    (
                                            o -> LocalDate.now(),
                                            o -> "*TEST*",
                                            o -> LocalDate.now(),
                                            o -> "*TEST*"
                                    )
                    );
        }
    }

}
