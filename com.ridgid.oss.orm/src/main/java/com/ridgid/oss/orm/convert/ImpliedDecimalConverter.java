package com.ridgid.oss.orm.convert;

import java.math.BigDecimal;

public abstract class ImpliedDecimalConverter implements AttributeConverter<BigDecimal, Integer> {

    private final BigDecimal multiplier;

    protected ImpliedDecimalConverter(int numberOfDecimalPlaces) {
        this.multiplier = BigDecimal.valueOf(10 * numberOfDecimalPlaces).setScale(numberOfDecimalPlaces);
    }

    @Override
    public Integer convertToDatabaseColumn(BigDecimal entityValue) {
        return entityValue.multiply(multiplier).intValue();
    }

    @Override
    public BigDecimal convertToEntityAttribute(Integer dbValue) {
        return BigDecimal.valueOf(dbValue).divide(multiplier);
    }
}
