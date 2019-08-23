package com.ridgid.oss.common.jdbc.transform;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("WeakerAccess")
public abstract class ImpliedDecimalConverter implements AttributeConverter<BigDecimal, Integer>
{

    private final BigDecimal multiplier;

    protected ImpliedDecimalConverter(int numberOfDecimalPlaces) {
        this.multiplier = BigDecimal.valueOf(Math.pow(10, numberOfDecimalPlaces)).setScale(numberOfDecimalPlaces,
                                                                                           RoundingMode.HALF_EVEN);
    }

    @Override
    public Integer convertToDatabaseColumn(BigDecimal entityValue) {
        return entityValue.multiply(multiplier).intValue();
    }

    @Override
    public BigDecimal convertToEntityAttribute(Integer dbValue) {
        return BigDecimal.valueOf(dbValue).divide(multiplier,
                                                  RoundingMode.HALF_EVEN);
    }
}
