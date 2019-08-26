package com.ridgid.oss.common.jdbc.transform;

import java.math.BigDecimal;

@SuppressWarnings("WeakerAccess")
public abstract class ImpliedDecimalConverter implements AttributeConverter<BigDecimal, Integer>
{

    private final BigDecimal multiplier;

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    protected ImpliedDecimalConverter(int numberOfDecimalPlaces) {
        this.multiplier = BigDecimal.valueOf(Math.pow(10, numberOfDecimalPlaces))
                                    .setScale(numberOfDecimalPlaces);
    }

    @Override
    public Integer convertToDatabaseColumn(BigDecimal entityValue) {
        return entityValue.multiply(multiplier).intValue();
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    @Override
    public BigDecimal convertToEntityAttribute(Integer dbValue) {
        return BigDecimal.valueOf(dbValue).divide(multiplier);
    }
}
