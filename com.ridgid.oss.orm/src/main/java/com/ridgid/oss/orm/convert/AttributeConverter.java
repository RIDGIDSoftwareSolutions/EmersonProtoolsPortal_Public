package com.ridgid.oss.orm.convert;

/**
 * @param <TField>
 * @param <TDatabaseColumn>
 */
public interface AttributeConverter<TField, TDatabaseColumn> {
    TDatabaseColumn convertToDatabaseColumn(TField date);

    TField convertToEntityAttribute(TDatabaseColumn julianDate);
}
