package com.ridgid.oss.common.jdbc.transform;

/**
 * Interface that all Attribute Converters must implement
 *
 * @param <TField> type of the internal Java representation for the Field
 * @param <TDatabaseColumn> type (Java Type) of how the field should be persisted into the database column
 */
@SuppressWarnings("unused")
public interface AttributeConverter<TField, TDatabaseColumn> {
    TDatabaseColumn convertToDatabaseColumn(TField entityValue);

    TField convertToEntityAttribute(TDatabaseColumn dbValue);
}
