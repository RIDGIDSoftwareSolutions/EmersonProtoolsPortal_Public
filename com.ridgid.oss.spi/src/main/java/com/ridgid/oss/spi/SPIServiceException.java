package com.ridgid.oss.spi;

/**
 * Exception thrown if unable to locate a suitable implementation of the MessageBus interface
 */
@SuppressWarnings({
                      "OverloadedVarargsMethod",
                      "AssignmentOrReturnOfFieldWithMutableType",
                      "ClassNamePrefixedWithPackageName",
                      "PublicMethodNotExposedInInterface"
                  })
public class SPIServiceException extends RuntimeException
{
    private static final long        serialVersionUID       = 2074092232499183526L;
    private static final Throwable[] EMPTY_SECONDARY_CAUSES = new Throwable[0];

    private final Throwable[] secondaryCauses;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param secondaryCauses optional additional causes to attach to the exception
     */
    public SPIServiceException(Throwable... secondaryCauses) {
        super();
        this.secondaryCauses = secondaryCauses;
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public SPIServiceException(String message) {
        super(message);
        secondaryCauses = EMPTY_SECONDARY_CAUSES;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message         the detail message (which is saved for later retrieval
     *                        by the {@link #getMessage()} method).
     * @param cause           the cause (which is saved for later retrieval by the
     *                        {@link #getCause()} method).  (A <tt>null</tt> value is
     *                        permitted, and indicates that the cause is nonexistent or
     *                        unknown.)
     * @param secondaryCauses optional additional causes to attach to the exception
     * @since 1.4
     */
    public SPIServiceException(String message, Throwable cause, Throwable... secondaryCauses) {
        super(message, cause);
        this.secondaryCauses = secondaryCauses;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public SPIServiceException(Throwable cause) {
        super(cause);
        secondaryCauses = EMPTY_SECONDARY_CAUSES;
    }

    /**
     * Constructs a new runtime exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     *
     * @param message            the detail message.
     * @param cause              the cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     * @param secondaryCauses    optional additional causes to attach to the exception
     * @since 1.7
     */
    protected SPIServiceException(String message,
                                  Throwable cause,
                                  boolean enableSuppression,
                                  boolean writableStackTrace,
                                  Throwable... secondaryCauses)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        this.secondaryCauses = secondaryCauses;
    }

    /**
     * Secondary causes for this exception in the event an exception occurs during throwing of this exception
     *
     * @return secondary causes
     */
    public Throwable[] getSecondaryCauses() {
        return secondaryCauses;
    }
}

