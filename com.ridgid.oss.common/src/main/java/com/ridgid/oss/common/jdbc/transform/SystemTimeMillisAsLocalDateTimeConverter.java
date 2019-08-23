package com.ridgid.oss.common.jdbc.transform;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unused")
public class SystemTimeMillisAsLocalDateTimeConverter implements AttributeConverter<Long, LocalDateTime>
{
    @Override
    public LocalDateTime convertToDatabaseColumn(Long entityValue) {
        return LocalDateTime.now()
                            .plus(entityValue - System.currentTimeMillis(),
                                  ChronoUnit.MILLIS);
    }

    @Override
    public Long convertToEntityAttribute(LocalDateTime dbValue) {
        return LocalDateTime.now()
                            .until(dbValue,
                                   ChronoUnit.MILLIS)
               + System.currentTimeMillis();
    }
}
