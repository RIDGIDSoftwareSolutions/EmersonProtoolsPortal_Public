package com.ridgid.oss.common.function;

/**
 * @param <T>  type of input
 * @param <ET> type of exception thrown
 */
@FunctionalInterface
public interface ThrowingConsumer<T, ET extends Exception>
{
    /**
     * @param arg input value
     * @throws ET exception type that the function may throw
     */
    void apply(T arg) throws ET;
}
