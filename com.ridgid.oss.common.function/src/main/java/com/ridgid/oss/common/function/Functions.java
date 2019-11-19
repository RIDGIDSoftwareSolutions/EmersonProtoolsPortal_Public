package com.ridgid.oss.common.function;

import com.ridgid.oss.common.exception.CapturedCheckedException;
import com.ridgid.oss.common.exception.UnexpectedCapturedCheckedException;

import java.util.function.Function;

/**
 * Factory methods for functional composition
 */
@SuppressWarnings({
                      "unused",
                      "ClassNamePrefixedWithPackageName",
                      "UtilityClassCanBeEnum",
                      "StaticMethodOnlyUsedInOneClass"
                  })
public final class Functions
{
    private Functions() {}

    /**
     * Discards the input value and outputs the given output value instead
     *
     * @param replacement value
     * @param <T>         type of input value
     * @param <CT>        type of output value
     * @return the replacement value
     */
    public static <T, CT> Function<T, CT> replaceWith(CT replacement) {
        return t -> replacement;
    }

    /**
     * Wraps a {@code ThrowingFunction} as a {@code Function}
     *
     * @param exception class of checked exception that the ThrowingFunction is expected to throw
     * @param function  function that throws ET that should be wrapped
     * @param <T>       type of input to the function
     * @param <R>       type of output from the function
     * @param <ET>      type of the exception the throwing function is expected to throw
     * @return {@code Function} that has the same input and outputs as the {@code ThrowingFunction}, but, all checked exceptions are mapped to unchecked exceptions
     */
    @SuppressWarnings({"OverlyBroadCatchBlock", "OverlyLongLambda"})
    public static <T, R, ET extends Exception>
    Function<T, R> uncheck(Class<? extends ET> exception,
                           ThrowingFunction<? super T, ? extends R, ? extends ET> function)
    {
        return t -> {
            try {
                return function.apply(t);
            } catch ( Exception ex ) {
                if ( exception.isAssignableFrom(ex.getClass()) )
                    throw new CapturedCheckedException(exception, ex);
                throw new UnexpectedCapturedCheckedException(exception, ex);
            }
        };
    }

}
