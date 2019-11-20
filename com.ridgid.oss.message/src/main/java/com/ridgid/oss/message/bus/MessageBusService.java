package com.ridgid.oss.message.bus;

import com.ridgid.oss.message.bus.MessageBusService.MessageBusServiceException;
import com.ridgid.oss.message.bus.spi.MessageBus;
import com.ridgid.oss.spi.SPIService;
import com.ridgid.oss.spi.SPIServiceBase;

/**
 * Loads the configured or available MessageBus Service Provider and returns and instance of the Bus.
 */
@SuppressWarnings({
                      "Singleton",
                      "WeakerAccess"
                  })
public final class MessageBusService
    extends SPIServiceBase<MessageBus, MessageBusServiceException>
{
    /**
     * Obtain a non-singleton instance of the MessageBusService.
     * <p>
     * Prefer calling {@code instance} to obtain a thread-safe singleton instance instead
     */
    public MessageBusService() {
        super(MessageBus.class,
              MessageBusServiceException.class);
    }

    /**
     * @return singleton instance of service loader from MessageBus
     */
    @SuppressWarnings("MethodReturnOfConcreteClass")
    public static MessageBusService instance() {
        return SPIService.instance(MessageBusService.class);
    }

    @SuppressWarnings({"JavaDoc", "PublicInnerClass"})
    public static class MessageBusServiceException extends SPIServiceException
    {
        private static final long serialVersionUID = -1139401520129659844L;

        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public MessageBusServiceException() {
            super();
        }

        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public MessageBusServiceException(String message) {
            super(message);
        }

        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A <tt>null</tt> value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public MessageBusServiceException(String message, Throwable cause) {
            super(message, cause);
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
        public MessageBusServiceException(Throwable cause) {
            super(cause);
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
         * @since 1.7
         */
        public MessageBusServiceException(String message,
                                          Throwable cause,
                                          boolean enableSuppression,
                                          boolean writableStackTrace)
        {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
