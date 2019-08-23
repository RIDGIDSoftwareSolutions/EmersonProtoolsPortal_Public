package com.ridgid.oss.orm.jpa.convert;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalTime;

@Converter
public class Hh24MmSsTimeOfDayConverter
    extends com.ridgid.oss.common.jdbc.transform.Hh24MmSsTimeOfDayConverter
        implements AttributeConverter<LocalTime, Integer> {
}
