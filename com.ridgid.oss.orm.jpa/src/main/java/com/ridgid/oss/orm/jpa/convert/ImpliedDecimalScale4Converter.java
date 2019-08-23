package com.ridgid.oss.orm.jpa.convert;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;

@Converter
public class ImpliedDecimalScale4Converter
    extends com.ridgid.oss.common.jdbc.transform.ImpliedDecimalScale4Converter
        implements AttributeConverter<BigDecimal, Integer> {
}
