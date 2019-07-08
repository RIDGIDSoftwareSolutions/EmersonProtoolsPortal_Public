package com.ridgid.oss.orm.jpa.convert;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;

@Converter
public class ImpliedDecimalScale1Converter
        extends com.ridgid.oss.orm.convert.ImpliedDecimalScale1Converter
        implements AttributeConverter<BigDecimal, Integer> {
}
