package com.ridgid.oss.common.cache;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class ExpirableWrapper<T> implements Expirable
{
    private final T    wrapped;
    private final long expirationTimeMillis;

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

    public T unwrap()
    {
        return wrapped;
    }
}
