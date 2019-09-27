package com.ridgid.oss.common.cache;

/**
 * Wrapper for any object that will add the expirable interface needed by the ExpirableCache interface
 *
 * @param <T> object type to wrap
 * @code {
 * ExpirableCache<Integer,ExpirableWrapper<String>> cache = new InMemoryExpirableCache<>();
 * ...
 * cache.add( 3, new ExpirableWrapper( "Hello" ) );
 * }
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class ExpirableWrapper<T> implements Expirable
{
    private final T    wrapped;
    private final long expirationTimeMillis;

    /**
     * Create an ExpirableWrapper for T object that will expire at the given expirationTimeMillis with respect to the
     * System time (System.currentTimeMillis())
     *
     * @param wrapped the object to wrap in the ExpirableWrapper
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
}
