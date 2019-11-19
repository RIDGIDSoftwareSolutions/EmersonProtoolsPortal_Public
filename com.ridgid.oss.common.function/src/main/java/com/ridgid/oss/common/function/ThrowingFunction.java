package com.ridgid.oss.common.function;

/**
 * @param <T>  type of input
 * @param <R>  output type
 * @param <ET> type of exception thrown
 */
@SuppressWarnings("InterfaceNeverImplemented")
@FunctionalInterface
public interface ThrowingFunction<T, R, ET extends Exception>
{
    /**
     * @param arg input value
     * @return value returned by the function when the function is applied to the input value
     * @throws ET exception type that the function may throw
     */
    R apply(T arg) throws ET;
}
