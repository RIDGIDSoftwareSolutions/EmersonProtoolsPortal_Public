package com.ridgid.oss.orm.convert;

import java.time.LocalDate;

public class Y1900JulianDateConverter
        implements AttributeConverter<LocalDate, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LocalDate date) {
        if (date == null) return 0;
        return (date.getYear() - 1900) * 1000 + date.getDayOfYear();
    }

    @Override
    public LocalDate convertToEntityAttribute(Integer julianDate) {
        if (julianDate == null || julianDate == 0) return null;
        if (julianDate < 0)
            throw new IllegalArgumentException("Julian date integer must be positive and of the form yyyddd where yyy = year - 1900 and ddd = day of year. Invalid Value: " + julianDate);
        int year = 1900 + julianDate / 1000;
        int doy = julianDate % 1000;
        return LocalDate.ofYearDay(year, doy);
    }
}
