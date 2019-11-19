package com.ridgid.oss.common.exception;

/**
 * Unchecked Exception for remapping checked exceptions to unchecked exceptions
 */
@SuppressWarnings({"WeakerAccess", "NonConstantFieldWithUpperCaseName", "PublicField"})
public class CapturedCheckedException extends RuntimeException
{
    private static final long serialVersionUID = 3919218179330542155L;

    /**
     * Exception type this exception is catching and remapping
     */
    public final Class<? extends Exception> CAPTURE_EXCEPTION_TYPE;

    /**
     * @param captureExceptionType class for type of checked exception that was caught
     * @param message              @see {@code RuntimeException}
     * @param cause                @see {@code RuntimeException}
     * @param <CET>                type of checked exception to catch
     */
    public <CET extends Exception> CapturedCheckedException(Class<? extends CET> captureExceptionType,
                                                            String message,
                                                            CET cause)
    {
        super(message, cause);
        CAPTURE_EXCEPTION_TYPE = captureExceptionType;
    }

    /**
     * @param captureExceptionType class for type of checked exception that was caught
     * @param cause                @see {@code RuntimeException}
     * @param <CET>                type of checked exception to catch
     */
    public <CET extends Exception> CapturedCheckedException(Class<? extends CET> captureExceptionType,
                                                            CET cause)
    {
        super(cause);
        CAPTURE_EXCEPTION_TYPE = captureExceptionType;
    }

    /**
     * @param captureExceptionType class for type of checked exception that was caught
     * @param message              @see {@code RuntimeException}
     * @param cause                @see {@code RuntimeException}
     * @param enableSuppression    @see {@code RuntimeException}
     * @param writableStackTrace   @see {@code RuntimeException}
     * @param <CET>                type of checked exception to catch
     */
    protected <CET extends Exception> CapturedCheckedException(Class<? extends CET> captureExceptionType,
                                                               String message,
                                                               CET cause,
                                                               boolean enableSuppression,
                                                               boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        CAPTURE_EXCEPTION_TYPE = captureExceptionType;
    }
}
