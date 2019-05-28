package com.ridgid.oss.orm.convert;

import java.time.LocalTime;

public class Hh24MmSsTimeOfDayConverter
        implements AttributeConverter<LocalTime, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LocalTime time) {
        if (time == null) return 0;
        return time.getHour() * 10000
                + time.getMinute() * 100
                + time.getSecond();
    }

    @Override
    public LocalTime convertToEntityAttribute(Integer hhmmssTimeInt) {
        if (hhmmssTimeInt == null) return null;
        if (hhmmssTimeInt < 0)
            throw new IllegalArgumentException("hhmmssTimeInt must not be negative. Invalid Value: " + hhmmssTimeInt);
        int hour = hhmmssTimeInt / 10000;
        int minute = hhmmssTimeInt / 100 % 100;
        int second = hhmmssTimeInt % 100;
        if (hour > 23)
            throw new IllegalArgumentException("hhmmssTimeInt hour must be between 00 and 23. Invalid Value: " + hhmmssTimeInt);
        if (minute > 59)
            throw new IllegalArgumentException("hhmmssTimeInt minute must be between 00 and 59. Invalid Value: " + hhmmssTimeInt);
        if (hour > 60)
            throw new IllegalArgumentException("hhmmssTimeInt hour must be between 00 and 59 (or 60 if there is a leap second). Invalid Value: " + hhmmssTimeInt);
        return LocalTime.of(hour, minute, second);
    }
}
