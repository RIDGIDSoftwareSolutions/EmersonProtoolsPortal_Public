package com.ridgid.oss.common.function;

/**
 * @param <T>  type of input
 * @param <ET> type of exception thrown
 */
@SuppressWarnings("InterfaceNeverImplemented")
@FunctionalInterface
public interface ThrowingSupplier<T, ET extends Exception>
{
    /**
     * @return supplied value
     * @throws ET exception type that the function may throw
     */
    T get() throws ET;
}
