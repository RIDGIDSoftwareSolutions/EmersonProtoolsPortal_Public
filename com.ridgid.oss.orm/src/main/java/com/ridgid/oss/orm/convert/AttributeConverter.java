package com.ridgid.oss.orm.convert;

/**
 * @param <TField>
 * @param <TDatabaseColumn>
 */
@SuppressWarnings("unused")
public interface AttributeConverter<TField, TDatabaseColumn> {
    TDatabaseColumn convertToDatabaseColumn(TField entityValue);

    TField convertToEntityAttribute(TDatabaseColumn dbValue);
}
