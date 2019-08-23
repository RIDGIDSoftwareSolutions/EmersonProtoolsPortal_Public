package com.ridgid.oss.common.jdbc.transform;

import java.util.UUID;

@SuppressWarnings("unused")
public class UUIDConverter implements AttributeConverter<UUID, String>
{
    @Override
    public String convertToDatabaseColumn(UUID entityValue) {
        return entityValue.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String dbValue) {
        return UUID.fromString(dbValue);
    }
}
