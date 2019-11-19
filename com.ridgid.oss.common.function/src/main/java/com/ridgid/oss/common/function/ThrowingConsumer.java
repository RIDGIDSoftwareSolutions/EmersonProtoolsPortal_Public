package com.ridgid.oss.common.function;

/**
 * @param <T>  type of input
 * @param <R>  output type
 * @param <ET> type of exception thrown
 */
@SuppressWarnings("InterfaceNeverImplemented")
@FunctionalInterface
public interface ThrowingConsumer<T, ET extends Exception>
{
    /**
     * @param arg input value
     * @throws ET exception type that the function may throw
     */
    void apply(T arg) throws ET;
}
