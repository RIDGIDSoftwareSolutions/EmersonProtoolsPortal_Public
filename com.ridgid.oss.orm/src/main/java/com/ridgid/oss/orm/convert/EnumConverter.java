package com.ridgid.oss.orm.convert;

import com.ridgid.oss.common.enumutil.ConvertibleEnum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * General Attribute Converter for Enums which implement the DBConvertibleEnum interface.
 * <p>
 * Example Usage:
 * <pre>
 * {@code
 *      public enum FooEnum implements DBConvertibleEnum<FooEnum,String> {
 *          ...
 *      }
 *
 *      {@literal @}
 *      {@literal @}Entity
 *      public BarEntity {
 *
 *          {@literal @}Convert(converter = EnumConverter.class)
 *      }
 * }
 * </pre>
 *
 * @param <ET>   Enum Type of the Enum to Convert
 * @param <DBCT> Java Type to use when persisting the Enum value to the Database
 */
@SuppressWarnings({"unused", "SpellCheckingInspection", "FieldCanBeLocal"})
public class EnumConverter<ET extends ConvertibleEnum<? extends Enum, DBCT>, DBCT>
        implements AttributeConverter<ET, DBCT> {

    private final Class<ET> entityValueClass;
    private final Class<DBCT> dbColumnValueClass;
    private final Method convertFromDBValueMethod;

    public EnumConverter(Class<ET> entityValueClass,
                         Class<DBCT> dbColumnValueClass) {
        this.entityValueClass = entityValueClass;
        this.dbColumnValueClass = dbColumnValueClass;
        try {
            convertFromDBValueMethod = entityValueClass.getMethod("from", dbColumnValueClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Class, '" + entityValueClass
                            + "', does not have a public static method 'from' that takes database column value type and returns an enum type of "
                            + entityValueClass,
                    e);
        }
    }

    @Override
    public DBCT convertToDatabaseColumn(ET entityValue) {
        return entityValue.into();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ET convertToEntityAttribute(DBCT dbValue) {
        try {
            return (ET) convertFromDBValueMethod.invoke(null, dbValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                    "Class, '" + entityValueClass
                            + "', does not have a public static method 'from' that takes database column value type and returns an enum type of "
                            + entityValueClass,
                    e);
        }
    }
}
