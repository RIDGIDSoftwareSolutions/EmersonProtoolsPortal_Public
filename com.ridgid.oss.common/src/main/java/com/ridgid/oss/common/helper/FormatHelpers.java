package com.ridgid.oss.common.helper;

import java.math.BigDecimal;
import java.util.Locale;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class FormatHelpers {

    private FormatHelpers() {}

    public static String formatCurrencyValue(BigDecimal amount, int numberOfDecimalPlaces, Locale locale, String currencyCode) {
        return currencyCode + " " + formatDecimalValue(amount, numberOfDecimalPlaces, locale);
    }

    public static String formatDecimalValue(BigDecimal amount, int numberOfDecimalPlaces, Locale locale) {
        return String.format(locale, "%." + numberOfDecimalPlaces + "f", amount );
    }
}
