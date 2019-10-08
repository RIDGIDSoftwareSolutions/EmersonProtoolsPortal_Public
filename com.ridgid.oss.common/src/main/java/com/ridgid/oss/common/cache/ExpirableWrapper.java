package com.ridgid.oss.common.cache;

import java.util.function.Function;

/**
 * Wrapper for any object that will add the expirable interface needed by the ExpirableCache interface
 *
 * @param <T> object type to wrap
 *            {@code
 *            ExpirableCache<Integer,ExpirableWrapper<String>> cache = new InMemoryExpirableCache<>();
 *            ...
 *            cache.add( 3, new ExpirableWrapper( "Hello" ) );
 *            }
 */
@SuppressWarnings({"unused", "SpellCheckingInspection", "WeakerAccess"})
public class ExpirableWrapper<T> implements Expirable
{
    private final T    wrapped;
    private final long expirationTimeMillis;

    /**
     * Create an ExpirableWrapper for T object that will expire at the given expirationTimeMillis with respect to the
     * System time (System.currentTimeMillis())
     *
     * @param wrapped              the object to wrap in the ExpirableWrapper
     * @param expirationTimeMillis the system time in milliseconds to expire
     */
    public ExpirableWrapper(T wrapped,
                            long expirationTimeMillis)
    {
        this.wrapped              = wrapped;
        this.expirationTimeMillis = expirationTimeMillis;
    }

    @Override
    public boolean isExpired()
    {
        return expirationTimeMillis < System.currentTimeMillis();
    }

    /**
     * Unwraps and returns the wrapped object
     *
     * @return the object of type T that was wrapped
     */
    public T unwrap()
    {
        return wrapped;
    }

    public static <T> ExpirableWrapper<T> expiringSecondsFromNow(T t, int seconds) {
        return new ExpirableWrapper<>(t, System.currentTimeMillis() + seconds * 1_000);
    }

    public static <T> ExpirableWrapper<T> expiringMinutesFromNow(T t, int minutes) {
        return new ExpirableWrapper<>(t, System.currentTimeMillis() + minutes * 60_000);
    }

    public static <T> ExpirableWrapper<T> expiringHoursFromNow(T t, int hours) {
        return new ExpirableWrapper<>(t, System.currentTimeMillis() + hours * 3_600_000);
    }

    public static <T> Function<T, ExpirableWrapper<T>> expiringSecondsFromNow(int seconds) {
        return t -> expiringSecondsFromNow(t, seconds);
    }

    public static <T> Function<T, ExpirableWrapper<T>> expiringMinutesFromNow(int minutes) {
        return t -> expiringMinutesFromNow(t, minutes);
    }

    public static <T> Function<T, ExpirableWrapper<T>> expiringHoursFromNow(int hours) {
        return t -> expiringHoursFromNow(t, hours);
    }
}
