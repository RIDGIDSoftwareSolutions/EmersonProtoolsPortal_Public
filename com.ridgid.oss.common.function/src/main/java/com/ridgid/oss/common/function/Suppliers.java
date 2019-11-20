package com.ridgid.oss.common.function;

import com.ridgid.oss.common.exception.CapturedCheckedException;
import com.ridgid.oss.common.exception.UnexpectedCapturedCheckedException;

import java.util.function.Supplier;

/**
 * Factory methods for functional composition of suppliers
 */
@SuppressWarnings({
                      "unused",
                      "UtilityClassCanBeEnum",
                  })
public final class Suppliers
{
    private Suppliers() {}

    /**
     * Wraps a {@code ThrowingConsumer} as a {@code Consumer}
     *
     * @param exception class of checked exception that the ThrowingFunction is expected to throw
     * @param supplier  supplier that throws ET that should be wrapped
     * @param <T>       type of input to the supplier
     * @param <ET>      type of the exception the throwing supplier is expected to throw
     * @return {@code Function} that has the same input and outputs as the {@code ThrowingFunction}, but, all checked exceptions are mapped to unchecked exceptions
     */
    @SuppressWarnings({"OverlyBroadCatchBlock", "OverlyLongLambda"})
    public static <T, ET extends Exception>
    Supplier<T> uncheck(Class<? extends ET> exception,
                        ThrowingSupplier<? extends T, ? extends ET> supplier)
    {
        return () -> {
            try {
                return supplier.get();
            } catch ( Exception ex ) {
                if ( exception.isAssignableFrom(ex.getClass()) )
                    throw new CapturedCheckedException(exception, ex);
                throw new UnexpectedCapturedCheckedException(exception, ex);
            }
        };
    }

}
