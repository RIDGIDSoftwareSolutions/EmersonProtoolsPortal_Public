package com.ridgid.oss.common.exception;

/**
 * Captured Checked exception in a context where the specific exception caught was not expected
 */
@SuppressWarnings("JavaDoc")
public class UnexpectedCapturedCheckedException extends CapturedCheckedException
{
    private static final long serialVersionUID = -683877372865267265L;

    /**
     * @param captureExceptionType class for type of checked exception that was caught
     * @param message              @see RuntimeException
     * @param cause                @see RuntimeException
     */
    public <CET extends Exception> UnexpectedCapturedCheckedException(Class<? extends CET> captureExceptionType,
                                                                      String message, CET cause)
    {
        super(captureExceptionType, message, cause);
    }

    /**
     * @param captureExceptionType class for type of checked exception that was caught
     * @param cause                @see RuntimeException
     */
    public <CET extends Exception> UnexpectedCapturedCheckedException(Class<? extends CET> captureExceptionType,
                                                                      CET cause)
    {
        super(captureExceptionType, cause);
    }

    /**
     * @param captureExceptionType class for type of checked exception that was caught
     * @param message              @see RuntimeException
     * @param cause                @see RuntimeException
     * @param enableSuppression    @see RuntimeException
     * @param writableStackTrace   @see RuntimeException
     */
    protected <CET extends Exception> UnexpectedCapturedCheckedException(Class<? extends CET> captureExceptionType,
                                                                         String message,
                                                                         CET cause,
                                                                         boolean enableSuppression,
                                                                         boolean writableStackTrace)
    {
        super(captureExceptionType, message, cause, enableSuppression, writableStackTrace);
    }
}
