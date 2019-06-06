package com.ridgid.oss.orm.jpa.test;

import com.ridgid.oss.orm.PrimaryKeyedEntity;

import javax.persistence.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public final class DAOTestHelpers {

    private DAOTestHelpers() {
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
                populateBaseFields(idx, rv);
                return rv;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @param entityFieldNames
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean fieldsAreEqual(List<String> entityFieldNames,
                                         Object obj1,
                                         Object obj2,
                                         List<String> outErrors) {
        boolean areEqual = true;
        for (String fieldName : entityFieldNames) {
            Field field = getFieldOrThrowRuntimeException(obj1.getClass(), fieldName);
            Object value1 = getFieldValueOrThrowRuntimeException(obj1, field);
            Object value2 = getFieldValueOrThrowRuntimeException(obj2, field);
            if (!Objects.equals(value1, value2)) {
                outErrors.add("Field Not Equal: " + fieldName + ", Value Expected = " + value1 + ", Actual Value = " + value2);
                areEqual = false;
            }
        }
        return areEqual;
    }

    /**
     * @param idx
     * @param rec
     */
    public static void populateBaseFields(int idx, Object rec) {
        int fieldIdx = 0;
        for (Field field : rec.getClass().getDeclaredFields()) {
            if (populateBaseField(rec, field, idx, fieldIdx)) fieldIdx++;
        }
    }

    /**
     * @param columnAndFieldNames
     * @param outColumnNames
     * @param outFieldNames
     */
    public static void separateColumnAndFieldNames(List<String> columnAndFieldNames, List<String> outColumnNames, List<String> outFieldNames) {
        for (int i = 0; i < columnAndFieldNames.size() - 1; i += 2) {
            outColumnNames.add(columnAndFieldNames.get(i));
            outFieldNames.add(columnAndFieldNames.get(i + 1));
        }
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
                                                           List<String> primaryKeyColumnNames,
                                                           List<String> primaryKeyFieldNames,
                                                           List<String> entityColumnNames,
                                                           List<String> entityFieldNames) {
        return createNativeInsertQueryStringFrom
                (
                        null,
                        tableName,
                        primaryKeyColumnNames,
                        primaryKeyFieldNames,
                        entityColumnNames,
                        entityFieldNames
                );
    }

    public static String createNativeInsertQueryStringFrom(String schemaName,
                                                           String tableName,
                                                           List<String> primaryKeyColumnNames,
                                                           List<String> primaryKeyFieldNames,
                                                           List<String> entityColumnNames,
                                                           List<String> entityFieldNames) {
        Objects.requireNonNull("tableName must be non-null");
        if (primaryKeyColumnNames.size() != primaryKeyFieldNames.size())
            throw new RuntimeException("primary key column names and primary key field names must match in number");
        if (entityColumnNames.size() != entityFieldNames.size())
            throw new RuntimeException("entity column names and entity field names must match in number");
        String schemaPart = schemaName == null ? "" : "\"" + schemaName + "\".";
        String fieldsPart
                = Stream.concat(primaryKeyColumnNames.stream(), entityColumnNames.stream())
                .map(cn -> "\"" + cn + "\"")
                .collect(Collectors.joining(","));
        String valuesPart
                = Stream.concat(primaryKeyColumnNames.stream(), entityColumnNames.stream())
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
    public static void setInsertQueryColumnValues(Query q, Object obj, int offset, List<String> fieldNames) {
        for (int i = 0; i < fieldNames.size(); i++) {
            Field f = getFieldOrThrowRuntimeException(obj.getClass(), fieldNames.get(i));
            if (f.isAnnotationPresent(Convert.class))
                setConvertedParameterValue(q, obj, offset, i, f);
            else
                setBasicParameterValue(q, obj, offset, i, f);
        }
    }

    private static void setConvertedParameterValue(Query q, Object obj, int offset, int i, Field f) {
        try {
            AttributeConverter converter
                    = (AttributeConverter) f
                    .getAnnotation(Convert.class)
                    .converter()
                    .getConstructor()
                    .newInstance();
            q.setParameter
                    (
                            i + offset + 1,
                            converter.convertToDatabaseColumn(getFieldValueOrThrowRuntimeException(obj, f))
                    );
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setBasicParameterValue(Query q, Object obj, int offset, int i, Field f) {
        Class<?> ft = f.getType();
        if (ft == Calendar.class)
            setCalendarParameterValue(q, obj, i + offset + 1, f);
        else if (ft == Date.class)
            setDateParameterValue(q, obj, i + offset + 1, f);
        else
            q.setParameter(i + offset + 1, getFieldValueOrThrowRuntimeException(obj, f));
    }

    /**
     * @param objClass
     * @param fieldName
     * @return
     */
    public static Field getFieldOrThrowRuntimeException(Class<?> objClass, String fieldName) {
        try {
            Field f = objClass.getDeclaredField(fieldName);
            if (!f.isAccessible()) f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param query
     * @param obj
     * @param idx
     * @param field
     */
    public static void setDateParameterValue(Query query, Object obj, int idx, Field field) {
        TemporalType tt = getTemporalTypeForAmbiguousTemporalField(field);
        query.setParameter(idx, (Date) getFieldValueOrThrowRuntimeException(obj, field), tt);
    }

    /**
     * @param query
     * @param obj
     * @param idx
     * @param field
     */
    public static void setCalendarParameterValue(Query query, Object obj, int idx, Field field) {
        TemporalType tt = getTemporalTypeForAmbiguousTemporalField(field);
        query.setParameter(idx, (Calendar) getFieldValueOrThrowRuntimeException(obj, field), tt);
    }

    /**
     * @param field
     * @return
     */
    public static TemporalType getTemporalTypeForAmbiguousTemporalField(Field field) {
        return field.isAnnotationPresent(Temporal.class)
                ? field.getAnnotation(Temporal.class).value()
                : TemporalType.TIMESTAMP;
    }

    /**
     * @param obj
     * @param field
     * @return
     */
    public static Object getFieldValueOrThrowRuntimeException(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     * @return
     */
    public static boolean populateBaseField(Object rec, Field field, int idx, int fieldIdx) {
        if (!field.isAccessible()) field.setAccessible(true);
        if (field.isAnnotationPresent(Transient.class)
                || field.isAnnotationPresent(EmbeddedId.class)
                || Modifier.isFinal(field.getModifiers())
                || Modifier.isStatic((field.getModifiers()))) return false;
        Class<?> ft = field.getType();
        if (ft.isEnum())
            populateEnumField(rec, field, ft, idx, fieldIdx);
            // Integral Numeric Primitive & Primitive Wrapper Types
        else if (ft.equals(Byte.class) || ft.equals(Byte.TYPE))
            populateByteField(rec, field, idx, fieldIdx);
        else if (ft.equals(Short.class) || ft.equals(Short.TYPE))
            populateShortField(rec, field, idx, fieldIdx);
        else if (ft.equals(Integer.class) || ft.equals(Integer.TYPE))
            populateIntegerField(rec, field, idx, fieldIdx);
        else if (ft.equals(Long.class) || ft.equals(Long.TYPE))
            populateLongField(rec, field, idx, fieldIdx);
        else if (ft.equals(BigInteger.class))
            populateBigIntegerField(rec, field, idx, fieldIdx);
            // Floating-Point & Decimal Numeric Primitive & Primitive Wrapper Types
        else if (ft.equals(Float.class) || ft.equals(Float.TYPE))
            populateFloatField(rec, field, idx, fieldIdx);
        else if (ft.equals(Double.class) || ft.equals(Double.TYPE))
            populateDoubleField(rec, field, idx, fieldIdx);
        else if (ft.equals(BigDecimal.class))
            populateBigDecimalField(rec, field, idx, fieldIdx);
            // Boolean Primitive and Primitive Wrapper Types
        else if (ft.equals(Boolean.class) || ft.equals(Boolean.TYPE))
            populateBooleanField(rec, field, idx, fieldIdx);
            // Character Primitive and Primitive Wrapper Types
        else if (ft.equals(Character.class) || ft.equals(Character.TYPE))
            populateCharacterField(rec, field, idx, fieldIdx);
            // String Type
        else if (ft.equals(String.class))
            populateStringField(rec, field, idx, fieldIdx);
            // Array Types (Character & Byte Arrays)
        else if (ft.isArray() && (ft.getComponentType().equals(Character.class) || ft.getComponentType().equals(Character.TYPE)))
            populateCharacterArrayField(rec, field, idx, fieldIdx);
        else if (ft.isArray() && (ft.getComponentType().equals(Byte.class) || ft.getComponentType().equals(Byte.TYPE)))
            populateByteArrayField(rec, field, idx, fieldIdx);
            // Temporal Types
        else if (ft.equals(Date.class))
            populateDateField(rec, field, idx, fieldIdx);
        else if (ft.equals(Calendar.class))
            populateCalendarField(rec, field, idx, fieldIdx);
        else if (ft.equals(java.sql.Date.class))
            populateSqlDateField(rec, field, idx, fieldIdx);
        else if (ft.equals(Time.class))
            populateSqlTimeField(rec, field, idx, fieldIdx);
        else if (ft.equals(Timestamp.class))
            populateSqlTimestampField(rec, field, idx, fieldIdx);
        else if (ft.equals(LocalDate.class))
            populateLocalDateField(rec, field, idx, fieldIdx);
        else if (ft.equals(LocalTime.class))
            populateLocalTimeField(rec, field, idx, fieldIdx);
        else if (ft.equals(LocalDateTime.class))
            populateLocalDateTimeField(rec, field, idx, fieldIdx);
        else if (ft.equals(Instant.class))
            populateInstantField(rec, field, idx, fieldIdx);
        else if (ft.equals(Duration.class))
            populateDurationField(rec, field, idx, fieldIdx);
        else if (ft.equals(OffsetDateTime.class))
            populateOffsetDateTimeField(rec, field, idx, fieldIdx);
        else if (ft.equals(OffsetTime.class))
            populateOffsetTimeField(rec, field, idx, fieldIdx);
        else if (ft.equals(ZonedDateTime.class))
            populateZonedDateTimeField(rec, field, idx, fieldIdx);
        else if (ft.equals(TimeZone.class))
            populateTimeZoneField(rec, field, idx, fieldIdx);
            // Localization Types
        else if (ft.equals(Currency.class))
            populateCurrencyField(rec, field, idx, fieldIdx);
        else if (ft.equals(Locale.class))
            populateLocaleField(rec, field, idx, fieldIdx);
            // Network Types
        else if (ft.equals(URL.class))
            populateNetURLField(rec, field, idx, fieldIdx);
            // Misc Types
        else if (ft.equals(UUID.class))
            populateUUIDField(rec, field, idx, fieldIdx);
            // Embedded Types
        else if (field.isAnnotationPresent(Embedded.class) || ft.isAnnotationPresent(Embeddable.class))
            populateEmbeddedField(rec, field, idx, fieldIdx);
        else return false;
        return true;
    }

    /**
     * @param rec
     * @param field
     * @param ft
     * @param idx
     * @param fieldIdx
     */
    public static void populateEnumField(Object rec, Field field, Class<?> ft, int idx, int fieldIdx) {
        int numEnumVals = ft.getEnumConstants().length;
        try {
            field.set(rec, ft.getEnumConstants()[(idx + fieldIdx) % numEnumVals]);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateByteField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, (byte) (Math.abs(idx + fieldIdx) % (Byte.MAX_VALUE * 2 + 1) - Byte.MAX_VALUE - 1));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateShortField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, (short) (Math.abs(idx + fieldIdx) % (Short.MAX_VALUE * 2 + 1) - Short.MAX_VALUE - 1));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateIntegerField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, (int) (Math.abs((long) idx + (long) fieldIdx) % (Integer.MAX_VALUE * 2L + 1L) - Integer.MAX_VALUE - 1L));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateLongField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, Math.abs((long) idx + (long) fieldIdx));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateBigIntegerField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, BigInteger.valueOf((long) idx + (long) fieldIdx));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateFloatField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, idx + (float) Integer.MAX_VALUE / fieldIdx);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateDoubleField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, idx + (double) Integer.MAX_VALUE / fieldIdx);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateBigDecimalField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            int scale = field.isAnnotationPresent(Column.class)
                    ? field.getAnnotation(Column.class).scale()
                    : 0;
            field.set(rec, BigDecimal.valueOf(idx + (double) Integer.MAX_VALUE / fieldIdx).setScale(scale, RoundingMode.HALF_EVEN));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateBooleanField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, idx + fieldIdx % 2 == 0 ? false : true);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateCharacterField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            field.set(rec, (char) (Character.SPACE_SEPARATOR + (idx + fieldIdx) % 95));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateStringField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            int length = field.isAnnotationPresent(Column.class)
                    ? field.getAnnotation(Column.class).length()
                    : 0;
            String val = "";
            for (int i = 0; i < length; i++)
                val += (char) (Character.SPACE_SEPARATOR + (idx + fieldIdx) % 95);
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateCharacterArrayField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            int length = field.isAnnotationPresent(Column.class)
                    ? field.getAnnotation(Column.class).length()
                    : 0;
            char[] val = new char[length];
            for (int i = 0; i < length; i++)
                val[i] = (char) (Character.SPACE_SEPARATOR + (idx + fieldIdx) % 95);
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateByteArrayField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            int length = field.isAnnotationPresent(Column.class)
                    ? field.getAnnotation(Column.class).length()
                    : 0;
            byte[] val = new byte[length];
            for (int i = 0; i < length; i++)
                val[i] = (byte) (Math.abs(idx + fieldIdx) % (Byte.MAX_VALUE * 2 + 1) - Byte.MAX_VALUE - 1);
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateDateField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            TemporalType tt = getTemporalTypeForAmbiguousTemporalField(field);
            @SuppressWarnings("deprecated")
            Date val = tt.equals(TemporalType.DATE)
                    ? new Date
                    (
                            100 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12,
                            (idx + fieldIdx) % 28 + 1
                    )
                    : tt.equals(TemporalType.TIME)
                    ? new Date
                    (
                            -1900,
                            0,
                            1,
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60
                    )
                    : new Date
                    (
                            100 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12,
                            (idx + fieldIdx) % 28 + 1,
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateCalendarField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            TemporalType tt = getTemporalTypeForAmbiguousTemporalField(field);
            Calendar val = Calendar.getInstance();
            if (tt.equals(TemporalType.DATE))
                val.set
                        (
                                2000 + (idx * 100 + fieldIdx) % 50,
                                (idx + fieldIdx) % 12,
                                (idx + fieldIdx) % 28 + 1
                        );
            else if (tt.equals(TemporalType.TIME))
                val.set
                        (
                                0,
                                0,
                                1,
                                (idx + fieldIdx) % 24,
                                (idx + fieldIdx) % 60,
                                (idx + fieldIdx) % 60
                        );
            else
                val.set
                        (
                                2000 + (idx * 100 + fieldIdx) % 50,
                                (idx + fieldIdx) % 12,
                                (idx + fieldIdx) % 28 + 1,
                                (idx + fieldIdx) % 24,
                                (idx + fieldIdx) % 60,
                                (idx + fieldIdx) % 60
                        );
            field.set(rec, val);
        } catch (
                IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }

    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateSqlDateField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            java.sql.Date val = new java.sql.Date
                    (
                            100 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12,
                            (idx + fieldIdx) % 28 + 1
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateSqlTimeField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            Time val = new Time
                    (
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateSqlTimestampField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            Timestamp val = new Timestamp
                    (
                            100 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12,
                            (idx + fieldIdx) % 28 + 1,
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60,
                            0
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateLocalDateField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            LocalDate val = LocalDate.of
                    (
                            2000 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12 + 1,
                            (idx + fieldIdx) % 28 + 1
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateLocalTimeField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            LocalTime val = LocalTime.of
                    (
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60,
                            0
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateLocalDateTimeField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            LocalDateTime val = LocalDateTime.of
                    (
                            2000 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12 + 1,
                            (idx + fieldIdx) % 28 + 1,
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60,
                            0
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateInstantField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            Instant val = Instant.ofEpochMilli
                    (
                            (int) (((long) Math.pow(idx, fieldIdx) + idx * fieldIdx + idx + fieldIdx) % 1_000_000_000)
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateDurationField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            Duration val = Duration.ofSeconds
                    (
                            (long) Math.pow(fieldIdx, idx),
                            (long) Math.pow(idx, fieldIdx % 1_000_000_000)
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateOffsetDateTimeField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            OffsetDateTime val = OffsetDateTime.of
                    (
                            2000 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12 + 1,
                            (idx + fieldIdx) % 28 + 1,
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60,
                            0,
                            ZoneOffset.UTC
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateOffsetTimeField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            OffsetTime val = OffsetTime.of
                    (
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60,
                            0,
                            ZoneOffset.UTC
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateZonedDateTimeField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            ZonedDateTime val = ZonedDateTime.of
                    (
                            2000 + (idx * 100 + fieldIdx) % 50,
                            (idx + fieldIdx) % 12 + 1,
                            (idx + fieldIdx) % 28 + 1,
                            (idx + fieldIdx) % 24,
                            (idx + fieldIdx) % 60,
                            (idx + fieldIdx) % 60,
                            0,
                            ZoneOffset.UTC
                    );
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateTimeZoneField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            int numTimezones = TimeZone.getAvailableIDs().length;
            TimeZone val = TimeZone.getTimeZone(TimeZone.getAvailableIDs()[(idx + fieldIdx) % numTimezones]);
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateCurrencyField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            int numCurrencies = Currency.getAvailableCurrencies().size();
            Currency val = Currency
                    .getAvailableCurrencies()
                    .stream()
                    .skip((idx + fieldIdx) % numCurrencies)
                    .findFirst()
                    .orElse(Currency.getInstance(("USD")));
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateLocaleField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            int numLocales = Locale.getAvailableLocales().length;
            Locale val = Locale.getAvailableLocales()[(idx + fieldIdx) % numLocales];
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateNetURLField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            URL val = new URL("http://sample.url.org/idx/" + idx + "/fieldIdx/" + fieldIdx);
            field.set(rec, val);
        } catch (IllegalAccessException | MalformedURLException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateUUIDField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            UUID val = new UUID((long) Math.pow(idx, fieldIdx), (long) idx * fieldIdx);
            field.set(rec, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param rec
     * @param field
     * @param idx
     * @param fieldIdx
     */
    public static void populateEmbeddedField(Object rec, Field field, int idx, int fieldIdx) {
        try {
            Constructor<?> constructor = field.getType().getConstructor(field.getType());
            Object embeddable = constructor.newInstance();
            populateBaseFields(idx * 100_000 + fieldIdx, embeddable);
            field.set(rec, embeddable);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throwAsRuntimeExceptionUnableToSetField(rec, field, idx, fieldIdx, e);
        }
    }

    /**
     * @param obj
     * @param field
     * @param idx
     * @param fieldIdx
     * @param e
     */
    public static void throwAsRuntimeExceptionUnableToSetField(Object obj, Field field, int idx, int fieldIdx, Exception e) {
        throw new RuntimeException
                (
                        "Unable to set field value:"
                                + " Entity=" + obj.getClass().getName()
                                + ", Field=" + field.getName()
                                + ", idx=" + idx
                                + ", fieldIdx=" + fieldIdx,
                        e
                );
    }

    /**
     * @param obj
     * @param field
     * @param e
     */
    public static void throwAsRuntimeExceptionUnableToSetField(Object obj, Field field, Exception e) {
        throw new RuntimeException
                (
                        "Unable to modify field value:"
                                + " Entity=" + obj.getClass().getName()
                                + ", Field=" + field.getName(),
                        e
                );
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
    public static String createNativeDeleteQueryStringFrom(String schemaName, String tableName) {
        return "delete from \"" + schemaName + "\".\"" + tableName + "\"";
    }

    /**
     * @param obj
     * @param fieldNames
     */
    public static void modifyFields(Object obj, List<String> fieldNames) {
        for (String fieldName : fieldNames) {
            Field f = getFieldOrThrowRuntimeException(obj.getClass(), fieldName);
            modifyField(obj, f);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void modifyField(Object obj, Field field) {
        Class<?> ft = field.getType();
        if (!field.isAccessible()) field.setAccessible(true);
        if (field.isAnnotationPresent(Transient.class)
                || field.isAnnotationPresent(EmbeddedId.class)
                || field.isAnnotationPresent(Id.class)) return;
        if (ft.isEnum())
            modifyEnumField(obj, field, (Class<? extends Enum>) ft);
            // Integral Numeric Primitive & Primitive Wrapper Types
        else if (ft.equals(Byte.class) || ft.equals(Byte.TYPE))
            modifyByteField(obj, field);
        else if (ft.equals(Short.class) || ft.equals(Short.TYPE))
            modifyShortField(obj, field);
        else if (ft.equals(Integer.class) || ft.equals(Integer.TYPE))
            modifyIntegerField(obj, field);
        else if (ft.equals(Long.class) || ft.equals(Long.TYPE))
            modifyLongField(obj, field);
        else if (ft.equals(BigInteger.class))
            modifyBigIntegerField(obj, field);
            // Floating-Point & Decimal Numeric Primitive & Primitive Wrapper Types
        else if (ft.equals(Float.class) || ft.equals(Float.TYPE))
            modifyFloatField(obj, field);
        else if (ft.equals(Double.class) || ft.equals(Double.TYPE))
            modifyDoubleField(obj, field);
        else if (ft.equals(BigDecimal.class))
            modifyBigDecimalField(obj, field);
            // Boolean Primitive and Primitive Wrapper Types
        else if (ft.equals(Boolean.class) || ft.equals(Boolean.TYPE))
            modifyBooleanField(obj, field);
            // Character Primitive and Primitive Wrapper Types
        else if (ft.equals(Character.class) || ft.equals(Character.TYPE))
            modifyCharacterField(obj, field);
            // String Type
        else if (ft.equals(String.class))
            modifyStringField(obj, field);
            // Array Types (Character & Byte Arrays)
        else if (ft.isArray() && (ft.getComponentType().equals(Character.class) || ft.getComponentType().equals(Character.TYPE)))
            modifyCharacterArrayField(obj, field);
        else if (ft.isArray() && (ft.getComponentType().equals(Byte.class) || ft.getComponentType().equals(Byte.TYPE)))
            modifyByteArrayField(obj, field);
            // Temporal Types
        else if (ft.equals(Date.class))
            modifyDateField(obj, field);
        else if (ft.equals(Calendar.class))
            modifyCalendarField(obj, field);
        else if (ft.equals(java.sql.Date.class))
            modifySqlDateField(obj, field);
        else if (ft.equals(Time.class))
            modifySqlTimeField(obj, field);
        else if (ft.equals(Timestamp.class))
            modifySqlTimestampField(obj, field);
        else if (ft.equals(LocalDate.class))
            modifyLocalDateField(obj, field);
        else if (ft.equals(LocalTime.class))
            modifyLocalTimeField(obj, field);
        else if (ft.equals(LocalDateTime.class))
            modifyLocalDateTimeField(obj, field);
        else if (ft.equals(Instant.class))
            modifyInstantField(obj, field);
        else if (ft.equals(Duration.class))
            modifyDurationField(obj, field);
        else if (ft.equals(OffsetDateTime.class))
            modifyOffsetDateTimeField(obj, field);
        else if (ft.equals(OffsetTime.class))
            modifyOffsetTimeField(obj, field);
        else if (ft.equals(ZonedDateTime.class))
            modifyZonedDateTimeField(obj, field);
        else if (ft.equals(TimeZone.class))
            modifyTimeZoneField(obj, field);
            // Localization Types
        else if (ft.equals(Currency.class))
            modifyCurrencyField(obj, field);
        else if (ft.equals(Locale.class))
            modifyLocaleField(obj, field);
            // Network Types
        else if (ft.equals(URL.class))
            modifyNetURLField(obj, field);
            // Misc Types
        else if (ft.equals(UUID.class))
            modifyUUIDField(obj, field);
    }

    public static <T extends Enum> void modifyEnumField(Object obj, Field field, Class<T> ft) {
        int numEnumVals = ft.getEnumConstants().length;
        try {
            int idx = (((T) field.get(obj)).ordinal() + 1) % ft.getEnumConstants().length;
            field.set(obj, ft.getEnumConstants()[idx]);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyByteField(Object obj, Field field) {
        try {
            field.set(obj, (byte) (((byte) field.get(obj)) + 1));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyShortField(Object obj, Field field) {
        try {
            field.set(obj, (short) (((short) field.get(obj)) + 1));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyIntegerField(Object obj, Field field) {
        try {
            field.set(obj, ((int) field.get(obj)) + 1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyLongField(Object obj, Field field) {
        try {
            field.set(obj, ((long) field.get(obj)) + 1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyBigIntegerField(Object obj, Field field) {
        try {
            field.set(obj, ((BigInteger) field.get(obj)).add(BigInteger.valueOf(1)));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyFloatField(Object obj, Field field) {
        try {
            field.set(obj, ((float) field.get(obj)) + 1.1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyDoubleField(Object obj, Field field) {
        try {
            field.set(obj, ((float) field.get(obj)) + 1.1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyBigDecimalField(Object obj, Field field) {
        try {
            field.set(obj, ((BigDecimal) field.get(obj)).add(BigDecimal.valueOf(1.5)));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyBooleanField(Object obj, Field field) {
        try {
            field.set(obj, !(boolean) field.get(obj));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyCharacterField(Object obj, Field field) {
        try {
            field.set(obj, ((char) field.get(obj) - Character.SPACE_SEPARATOR + 1) % 95 + Character.SPACE_SEPARATOR);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyStringField(Object obj, Field field) {
        try {
            char[] chars = ((String) field.get(obj)).toCharArray();
            reverseArray(chars);
            field.set(obj, String.valueOf(chars));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyCharacterArrayField(Object obj, Field field) {
        try {
            char[] chars = ((char[]) field.get(obj));
            reverseArray(chars);
            field.set(obj, chars);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyByteArrayField(Object obj, Field field) {
        try {
            byte[] bytes = ((byte[]) field.get(obj));
            reverseArray(bytes);
            field.set(obj, bytes);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    @SuppressWarnings("deprecated")
    public static void modifyDateField(Object obj, Field field) {
        try {
            Date val = ((Date) field.get(obj));
            TemporalType tt = getTemporalTypeForAmbiguousTemporalField(field);
            if (tt.equals(TemporalType.DATE) || tt.equals(TemporalType.TIMESTAMP))
                val.setYear(val.getYear() + 1);
            if (tt.equals(TemporalType.TIME) || tt.equals(TemporalType.TIMESTAMP))
                val.setHours((val.getHours() + 1) % 24);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyCalendarField(Object obj, Field field) {
        try {
            Calendar val = ((Calendar) field.get(obj));
            TemporalType tt = getTemporalTypeForAmbiguousTemporalField(field);
            if (tt.equals(TemporalType.DATE) || tt.equals(TemporalType.TIMESTAMP))
                val.add(Calendar.YEAR, 1);
            if (tt.equals(TemporalType.TIME) || tt.equals(TemporalType.TIMESTAMP))
                val.add(Calendar.HOUR, 1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    @SuppressWarnings("deprecated")
    public static void modifySqlDateField(Object obj, Field field) {
        try {
            java.sql.Date val = ((java.sql.Date) field.get(obj));
            val.setYear(val.getYear() + 1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    @SuppressWarnings("deprecated")
    public static void modifySqlTimeField(Object obj, Field field) {
        try {
            Time val = ((Time) field.get(obj));
            val.setHours((val.getHours() + 1) % 24);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    @SuppressWarnings("deprecated")
    public static void modifySqlTimestampField(Object obj, Field field) {
        try {
            Timestamp val = ((Timestamp) field.get(obj));
            val.setYear(val.getYear() + 1);
            val.setHours((val.getHours() + 1) % 24);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyLocalDateField(Object obj, Field field) {
        try {
            LocalDate val = ((LocalDate) field.get(obj)).plusYears(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyLocalTimeField(Object obj, Field field) {
        try {
            LocalTime val = ((LocalTime) field.get(obj)).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyLocalDateTimeField(Object obj, Field field) {
        try {
            LocalDateTime val = ((LocalDateTime) field.get(obj)).plusYears(1).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyInstantField(Object obj, Field field) {
        try {
            Instant val = ((Instant) field.get(obj)).plusSeconds(10);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyDurationField(Object obj, Field field) {
        try {
            Duration val = ((Duration) field.get(obj)).plusSeconds(10);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyOffsetDateTimeField(Object obj, Field field) {
        try {
            OffsetDateTime val = ((OffsetDateTime) field.get(obj)).plusYears(1).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyOffsetTimeField(Object obj, Field field) {
        try {
            OffsetTime val = ((OffsetTime) field.get(obj)).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyZonedDateTimeField(Object obj, Field field) {
        try {
            ZonedDateTime val = ((ZonedDateTime) field.get(obj)).plusYears(1).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyTimeZoneField(Object obj, Field field) {
        try {
            TimeZone val = ((TimeZone) field.get(obj));
            val.setRawOffset(val.getRawOffset() + 3600);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyCurrencyField(Object obj, Field field) {
        try {
            Currency[] currencies = (Currency[]) Currency.getAvailableCurrencies().toArray();
            Currency val = (Currency) field.get(obj);
            for (int i = 0; i < currencies.length; i++) {
                if (currencies[i].getCurrencyCode().equals(val.getCurrencyCode())) {
                    i = (i + 1) % currencies.length;
                    val = currencies[i];
                    break;
                }
            }
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyLocaleField(Object obj, Field field) {
        try {
            Locale[] locales = (Locale[]) Locale.getAvailableLocales();
            Locale val = (Locale) field.get(obj);
            for (int i = 0; i < locales.length; i++) {
                if (locales[i].equals(val)) {
                    i = (i + 1) % locales.length;
                    val = locales[i];
                    break;
                }
            }
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyNetURLField(Object obj, Field field) {
        try {
            URL val = (URL) field.get(obj);
            val = new URL(val.getProtocol(), val.getHost(), val.getPort() + 1, val.getPath());
            field.set(obj, val);
        } catch (IllegalAccessException | MalformedURLException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    public static void modifyUUIDField(Object obj, Field field) {
        try {
            UUID val = (UUID) field.get(obj);
            val = new UUID(val.getMostSignificantBits() * 1000, val.getLeastSignificantBits() * 10);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param chars
     */
    public static void reverseArray(char[] chars) {
        for (int i = 0; i < chars.length / 2; i++) {
            char t = chars[i];
            chars[i] = chars[chars.length - 1 - i];
            chars[chars.length - 1 - i] = t;
        }
    }

    /**
     * @param bytes
     */
    public static void reverseArray(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; i++) {
            byte t = bytes[i];
            bytes[i] = bytes[bytes.length - 1 - i];
            bytes[bytes.length - 1 - i] = t;
        }
    }

    /**
     * @param items
     * @param <T>
     */
    public static <T> void reverseArray(T[] items) {
        for (int i = 0; i < items.length / 2; i++) {
            T t = items[i];
            items[i] = items[items.length - 1 - i];
            items[items.length - 1 - i] = t;
        }
    }


    /**
     * @param item
     * @param rangeStart
     * @param rangeEnd
     * @param <T>
     * @return
     */
    public static <T extends Comparable<T>> boolean isBetween(T item, T rangeStart, T rangeEnd) {
        if (item.compareTo(rangeStart) < 0) return false;
        return item.compareTo(rangeEnd) < 0;
    }

}