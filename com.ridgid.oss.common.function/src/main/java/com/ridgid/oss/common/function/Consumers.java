package com.ridgid.oss.common.function;

import com.ridgid.oss.common.exception.CapturedCheckedException;
import com.ridgid.oss.common.exception.UnexpectedCapturedCheckedException;

import java.util.function.Consumer;

/**
 * Factory methods for functional composition of consumers
 */
@SuppressWarnings({
                      "unused",
                      "UtilityClassCanBeEnum",
                  })
public final class Consumers
{
    private Consumers() {}

    /**
     * Wraps a {@code ThrowingConsumer} as a {@code Consumer}
     *
     * @param exception class of checked exception that the ThrowingFunction is expected to throw
     * @param consumer  consumer that throws ET that should be wrapped
     * @param <T>       type of input to the consumer
     * @param <ET>      type of the exception the throwing consumer is expected to throw
     * @return {@code Function} that has the same input and outputs as the {@code ThrowingFunction}, but, all checked exceptions are mapped to unchecked exceptions
     */
    @SuppressWarnings({"OverlyBroadCatchBlock", "OverlyLongLambda"})
    public static <T, ET extends Exception>
    Consumer<T> uncheck(Class<? extends ET> exception,
                        ThrowingConsumer<? super T, ? extends ET> consumer)
    {
        return t -> {
            try {
                consumer.apply(t);
            } catch ( Exception ex ) {
                if ( exception.isAssignableFrom(ex.getClass()) )
                    throw new CapturedCheckedException(exception, ex);
                throw new UnexpectedCapturedCheckedException(exception, ex);
            }
        };
    }

}
