package com.ridgid.oss.common.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.ridgid.oss.common.helper.ArrayHelpers.reverseArray;
import static com.ridgid.oss.common.helper.ExceptionHelpers.throwAsRuntimeExceptionUnableToSetField;
import static com.ridgid.oss.common.helper.FieldReflectionHelpers.getFieldOrThrowRuntimeException;
import static com.ridgid.oss.common.helper.TemporalType.*;

/**
 *
 */
@SuppressWarnings({"WeakerAccess", "unused", "JavaDoc", "SpellCheckingInspection"})
public final class FieldModificationHelpers {

    private FieldModificationHelpers() {
    }

    /**
     * @param obj
     * @param fieldNames
     */
    public static void deterministicallyModifyFields(Object obj,
                                                     List<String> fieldNames) {
        deterministicallyModifyFields
                (
                        obj,
                        fieldNames,
                        Collections.emptySet()
                );
    }

    /**
     * @param obj
     * @param fieldNames
     * @param fieldNamesToExclude
     */
    public static void deterministicallyModifyFields(Object obj,
                                                     List<String> fieldNames,
                                                     Set<String> fieldNamesToExclude) {
        deterministicallyModifyFields
                (
                        obj,
                        fieldNames,
                        fieldNamesToExclude,
                        f -> false
                );
    }

    /**
     * @param obj
     * @param fieldNames
     * @param fieldExclusionPredicate
     */
    public static void deterministicallyModifyFields(Object obj,
                                                     List<String> fieldNames,
                                                     Predicate<Field> fieldExclusionPredicate) {
        deterministicallyModifyFields
                (
                        obj,
                        fieldNames,
                        Collections.emptySet(),
                        fieldExclusionPredicate
                );
    }

    /**
     * @param obj
     * @param fieldNames
     * @param fieldNamesToExclude
     * @param fieldExclusionPredicate
     */
    public static void deterministicallyModifyFields(Object obj,
                                                     List<String> fieldNames,
                                                     Set<String> fieldNamesToExclude,
                                                     Predicate<Field> fieldExclusionPredicate) {
        deterministicallyModifyFields
                (
                        obj,
                        fieldNames,
                        fieldNamesToExclude,
                        fieldExclusionPredicate,
                        f -> TIMESTAMP
                );
    }

    /**
     * @param obj
     * @param fieldNames
     * @param fieldNamesToExclude
     * @param fieldExclusionPredicate
     * @param ambiguousTemporalTypeMapper
     */
    public static void deterministicallyModifyFields(Object obj,
                                                     List<String> fieldNames,
                                                     Set<String> fieldNamesToExclude,
                                                     Predicate<Field> fieldExclusionPredicate,
                                                     Function<Field, TemporalType> ambiguousTemporalTypeMapper) {
        for (String fieldName : fieldNames) {
            if (fieldNamesToExclude.contains(fieldName)) continue;
            Field field = getFieldOrThrowRuntimeException
                    (
                            obj.getClass(),
                            fieldName
                    );
            deterministicallyModifyField
                    (
                            obj,
                            field,
                            fieldExclusionPredicate,
                            ambiguousTemporalTypeMapper
                    );
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyField(Object obj,
                                                    Field field) {
        deterministicallyModifyField
                (
                        obj,
                        field,
                        f -> false);
    }

    /**
     * @param obj
     * @param field
     * @param fieldExclusionPredicate =
     */
    public static void deterministicallyModifyField(Object obj,
                                                    Field field,
                                                    Predicate<Field> fieldExclusionPredicate) {
        deterministicallyModifyField
                (
                        obj,
                        field,
                        fieldExclusionPredicate,
                        f -> TIMESTAMP
                );
    }

    /**
     * @param obj
     * @param field
     * @param fieldExclusionPredicate
     * @param ambiguousTemporalTypeMappper
     */
    @SuppressWarnings("unchecked")
    public static void deterministicallyModifyField(Object obj,
                                                    Field field,
                                                    Predicate<Field> fieldExclusionPredicate,
                                                    Function<Field, TemporalType> ambiguousTemporalTypeMappper) {
        Class<?> ft = field.getType();
        if (!field.isAccessible()) field.setAccessible(true);
        if (Modifier.isFinal(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || fieldExclusionPredicate.test(field)) return;
        if (ft.isEnum())
            deterministicallyModifyEnumField(obj, field, (Class<? extends Enum>) ft);
            // Integral Numeric Primitive & Primitive Wrapper Types
        else if (ft.equals(Byte.class) || ft.equals(Byte.TYPE))
            deterministicallyModifyByteField(obj, field);
        else if (ft.equals(Short.class) || ft.equals(Short.TYPE))
            deterministicallyModifyShortField(obj, field);
        else if (ft.equals(Integer.class) || ft.equals(Integer.TYPE))
            deterministicallyModifyIntegerField(obj, field);
        else if (ft.equals(Long.class) || ft.equals(Long.TYPE))
            deterministicallyModifyLongField(obj, field);
        else if (ft.equals(BigInteger.class))
            deterministicallyModifyBigIntegerField(obj, field);
            // Floating-Point & Decimal Numeric Primitive & Primitive Wrapper Types
        else if (ft.equals(Float.class) || ft.equals(Float.TYPE))
            deterministicallyModifyFloatField(obj, field);
        else if (ft.equals(Double.class) || ft.equals(Double.TYPE))
            deterministicallyModifyDoubleField(obj, field);
        else if (ft.equals(BigDecimal.class))
            deterministicallyModifyBigDecimalField(obj, field);
            // Boolean Primitive and Primitive Wrapper Types
        else if (ft.equals(Boolean.class) || ft.equals(Boolean.TYPE))
            deterministicallyModifyBooleanField(obj, field);
            // Character Primitive and Primitive Wrapper Types
        else if (ft.equals(Character.class) || ft.equals(Character.TYPE))
            deterministicallyModifyCharacterField(obj, field);
            // String Type
        else if (ft.equals(String.class))
            deterministicallyModifyStringField(obj, field);
            // Array Types (Character & Byte Arrays)
        else if (ft.isArray() && (ft.getComponentType().equals(Character.class) || ft.getComponentType().equals(Character.TYPE)))
            deterministicallyModifyCharacterArrayField(obj, field);
        else if (ft.isArray() && (ft.getComponentType().equals(Byte.class) || ft.getComponentType().equals(Byte.TYPE)))
            deterministicallyModifyByteArrayField(obj, field);
            // Temporal Types
        else if (ft.equals(Date.class))
            deterministicallyModifyDateField(obj, field, ambiguousTemporalTypeMappper);
        else if (ft.equals(Calendar.class))
            deterministicallyModifyCalendarField(obj, field, ambiguousTemporalTypeMappper);
        else if (ft.equals(java.sql.Date.class))
            deterministicallyModifySqlDateField(obj, field);
        else if (ft.equals(Time.class))
            deterministicallyModifySqlTimeField(obj, field);
        else if (ft.equals(Timestamp.class))
            deterministicallyModifySqlTimestampField(obj, field);
        else if (ft.equals(LocalDate.class))
            deterministicallyModifyLocalDateField(obj, field);
        else if (ft.equals(LocalTime.class))
            deterministicallyModifyLocalTimeField(obj, field);
        else if (ft.equals(LocalDateTime.class))
            deterministicallyModifyLocalDateTimeField(obj, field);
        else if (ft.equals(Instant.class))
            deterministicallyModifyInstantField(obj, field);
        else if (ft.equals(Duration.class))
            deterministicallyModifyDurationField(obj, field);
        else if (ft.equals(OffsetDateTime.class))
            deterministicallyModifyOffsetDateTimeField(obj, field);
        else if (ft.equals(OffsetTime.class))
            deterministicallyModifyOffsetTimeField(obj, field);
        else if (ft.equals(ZonedDateTime.class))
            deterministicallyModifyZonedDateTimeField(obj, field);
        else if (ft.equals(TimeZone.class))
            deterministicallyModifyTimeZoneField(obj, field);
            // Localization Types
        else if (ft.equals(Currency.class))
            deterministicallyModifyCurrencyField(obj, field);
        else if (ft.equals(Locale.class))
            deterministicallyModifyLocaleField(obj, field);
            // Network Types
        else if (ft.equals(URL.class))
            deterministicallyModifyNetURLField(obj, field);
            // Misc Types
        else if (ft.equals(UUID.class))
            deterministicallyModifyUUIDField(obj, field);
    }

    /**
     * @param obj
     * @param field
     * @param ft
     * @param <T>
     */
    public static <T extends Enum> void deterministicallyModifyEnumField(Object obj,
                                                                         Field field,
                                                                         Class<T> ft) {
        int numEnumVals = ft.getEnumConstants().length;
        try {
            @SuppressWarnings("unchecked")
            int idx = (((T) field.get(obj)).ordinal() + 1) % ft.getEnumConstants().length;
            field.set(obj, ft.getEnumConstants()[idx]);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyByteField(Object obj, Field field) {
        try {
            field.set(obj, (byte) (((byte) field.get(obj)) + 1));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyShortField(Object obj, Field field) {
        try {
            field.set(obj, (short) (((short) field.get(obj)) + 1));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyIntegerField(Object obj, Field field) {
        try {
            field.set(obj, ((int) field.get(obj)) + 1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyLongField(Object obj, Field field) {
        try {
            field.set(obj, ((long) field.get(obj)) + 1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyBigIntegerField(Object obj, Field field) {
        try {
            field.set(obj, ((BigInteger) field.get(obj)).add(BigInteger.valueOf(1)));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyFloatField(Object obj, Field field) {
        try {
            field.set(obj, ((float) field.get(obj)) + 1.1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyDoubleField(Object obj, Field field) {
        try {
            field.set(obj, ((float) field.get(obj)) + 1.1);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyBigDecimalField(Object obj, Field field) {
        try {
            field.set(obj, ((BigDecimal) field.get(obj)).add(BigDecimal.valueOf(1.5)));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyBooleanField(Object obj, Field field) {
        try {
            field.set(obj, !(boolean) field.get(obj));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyCharacterField(Object obj, Field field) {
        try {
            field.set(obj, ((char) field.get(obj) - ' ' + 1) % 95 + ' ');
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyStringField(Object obj, Field field) {
        try {
            char[] chars = ((String) field.get(obj)).toCharArray();
            reverseArray(chars);
            field.set(obj, String.valueOf(chars));
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyCharacterArrayField(Object obj, Field field) {
        try {
            char[] chars = ((char[]) field.get(obj));
            reverseArray(chars);
            field.set(obj, chars);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyByteArrayField(Object obj, Field field) {
        try {
            byte[] bytes = ((byte[]) field.get(obj));
            reverseArray(bytes);
            field.set(obj, bytes);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    @SuppressWarnings({"deprecation"})
    public static void deterministicallyModifyDateField(Object obj,
                                                        Field field,
                                                        Function<Field, TemporalType> ambiguousTemporalTypeMapper) {
        try {
            Date val = ((Date) field.get(obj));
            TemporalType tt = ambiguousTemporalTypeMapper.apply(field);
            if (tt.equals(DATE) || tt.equals(TIMESTAMP))
                val.setYear(val.getYear() + 1);
            if (tt.equals(TIME) || tt.equals(TIMESTAMP))
                val.setHours((val.getHours() + 1) % 24);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyCalendarField(Object obj,
                                                            Field field,
                                                            Function<Field, TemporalType> ambiguousTemporalTypeMapper) {
        try {
            Calendar val = ((Calendar) field.get(obj));
            TemporalType tt = ambiguousTemporalTypeMapper.apply(field);
            if (tt.equals(DATE) || tt.equals(TIMESTAMP))
                val.add(Calendar.YEAR, 1);
            if (tt.equals(TIME) || tt.equals(TIMESTAMP))
                val.add(Calendar.HOUR, 1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    @SuppressWarnings("deprecation")
    public static void deterministicallyModifySqlDateField(Object obj, Field field) {
        try {
            java.sql.Date val = ((java.sql.Date) field.get(obj));
            val.setYear(val.getYear() + 1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    @SuppressWarnings("deprecation")
    public static void deterministicallyModifySqlTimeField(Object obj, Field field) {
        try {
            Time val = ((Time) field.get(obj));
            val.setHours((val.getHours() + 1) % 24);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    @SuppressWarnings("deprecation")
    public static void deterministicallyModifySqlTimestampField(Object obj, Field field) {
        try {
            Timestamp val = ((Timestamp) field.get(obj));
            val.setYear(val.getYear() + 1);
            val.setHours((val.getHours() + 1) % 24);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyLocalDateField(Object obj, Field field) {
        try {
            LocalDate val = ((LocalDate) field.get(obj)).plusYears(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyLocalTimeField(Object obj, Field field) {
        try {
            LocalTime val = ((LocalTime) field.get(obj)).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyLocalDateTimeField(Object obj, Field field) {
        try {
            LocalDateTime val = ((LocalDateTime) field.get(obj)).plusYears(1).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyInstantField(Object obj, Field field) {
        try {
            Instant val = ((Instant) field.get(obj)).plusSeconds(10);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyDurationField(Object obj, Field field) {
        try {
            Duration val = ((Duration) field.get(obj)).plusSeconds(10);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyOffsetDateTimeField(Object obj, Field field) {
        try {
            OffsetDateTime val = ((OffsetDateTime) field.get(obj)).plusYears(1).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyOffsetTimeField(Object obj, Field field) {
        try {
            OffsetTime val = ((OffsetTime) field.get(obj)).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyZonedDateTimeField(Object obj, Field field) {
        try {
            ZonedDateTime val = ((ZonedDateTime) field.get(obj)).plusYears(1).plusHours(1);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyTimeZoneField(Object obj, Field field) {
        try {
            TimeZone val = ((TimeZone) field.get(obj));
            val.setRawOffset(val.getRawOffset() + 3600);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyCurrencyField(Object obj, Field field) {
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

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyLocaleField(Object obj, Field field) {
        try {
            Locale[] locales = Locale.getAvailableLocales();
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

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyNetURLField(Object obj, Field field) {
        try {
            URL val = (URL) field.get(obj);
            val = new URL(val.getProtocol(), val.getHost(), val.getPort() + 1, val.getPath());
            field.set(obj, val);
        } catch (IllegalAccessException | MalformedURLException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

    /**
     * @param obj
     * @param field
     */
    public static void deterministicallyModifyUUIDField(Object obj, Field field) {
        try {
            UUID val = (UUID) field.get(obj);
            val = new UUID(val.getMostSignificantBits() * 1000, val.getLeastSignificantBits() * 10);
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            throwAsRuntimeExceptionUnableToSetField(obj, field, e);
        }
    }

}
