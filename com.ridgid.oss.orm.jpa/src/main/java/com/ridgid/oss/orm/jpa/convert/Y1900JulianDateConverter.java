package com.ridgid.oss.orm.jpa.convert;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDate;

@Converter
public class Y1900JulianDateConverter
    extends com.ridgid.oss.common.jdbc.transform.Y1900JulianDateConverter
        implements AttributeConverter<LocalDate, Integer> {
}
