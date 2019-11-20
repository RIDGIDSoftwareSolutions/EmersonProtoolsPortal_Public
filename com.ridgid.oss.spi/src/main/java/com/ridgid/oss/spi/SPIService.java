package com.ridgid.oss.spi;

import com.ridgid.oss.spi.SPIService.SPIServiceException;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.ridgid.oss.common.function.Suppliers.uncheck;

/**
 * @param <SI> SPI enabled Service-Interface to Look-Up Implementations for
 * @param <SE> exception thrown for Service Look-Up Errors
 */
@SuppressWarnings({"ClassNamePrefixedWithPackageName", "StringConcatenation"})
public interface SPIService<SI, SE extends SPIServiceException>
{
    @SuppressWarnings({"JavaDoc", "PublicInnerClass", "StaticCollection"})
    final class SERVICES
    {
        private SERVICES() {}

        private static final ConcurrentHashMap<Class<? super Object>, ? super Object> services
            = new ConcurrentHashMap<>(250);
    }

    /**
     * @param <SI>         Service Interface Type
     * @param <SE>         Service Interface Exception Type
     * @param serviceClass class of service implementation
     * @param <S>          type of service implementation
     * @return singleton instance of S
     */
    static <S extends SPIService<SI, SE>, SI, SE extends SPIServiceException>
    S instance(Class<? super S> serviceClass) {
        //noinspection unchecked
        return
            (S) SERVICES.services
                .computeIfAbsent((Class<Object>) serviceClass,
                                 key -> uncheck(Exception.class, serviceClass::newInstance).get());
    }

    /**
     * @return the implementation of the MessageBus interface as given by the Class Name in the 'com.ridgid.oss.message.
     * bus.service.class' system property, or, if the system property is not defined, then it returns the first available
     * implementation of the MessageBus interface that is found.
     * @throws SE if either the system property does not point to a valid implementation of the ServiceBus interface, or,
     *            if there is no available implementation found when the system property is not given.
     */
    SI defaultProvider() throws SE;

    /**
     * @return the {@code Class<SI>} of the interface for this service
     */
    Class<SI> serviceInterface();

    /**
     * @return the {@code Class<SE>} for the exception that this service may raise when looking up providers
     */
    Class<SE> serviceException();

    /**
     * @return the property (System.Properties) for configuring the default implementation for this service
     */
    default String defaultProviderPropertyName() {
        return serviceInterface()
                   .getPackage()
                   .getName()
               + ".service.class";
    }

    /**
     * @return value (Class Name) configured for the {@code defaultProviderPropertyName()} via {@see System.Properties}
     */
    default Optional<String> defaultProviderPropertyValue() {
        return Optional.ofNullable(System.getProperty(defaultProviderPropertyName()));
    }

    /**
     * @return stream of all available providers for this service
     */
    Stream<SI> streamProviders();

    /**
     * Get a {@code Supplier} for a {@code SE} Exception
     *
     * @param msg  error message
     * @param args arguments to the error message following the {@see String.format} conventions
     * @return supplier of the exception
     */
    @SuppressWarnings({"DuplicateStringLiteralInspection", "OverloadedVarargsMethod"})
    default Supplier<SE> serviceException(String msg, Object... args) {
        return () -> {
            try {
                return serviceException()
                    .getConstructor(String.class)
                    .newInstance(String.format(msg, args));
            } catch ( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
                throw new SPIServiceException(String.format("Unable to throw %s for error: %s",
                                                            serviceException().getName(),
                                                            String.format(msg, args)),
                                              e);
            }
        };
    }

    /**
     * Get a {@code Supplier} for a {@code SE} Exception
     *
     * @param cause underlying cause of the exception
     * @param msg   error message
     * @param args  arguments to the error message following the {@see String.format} conventions
     * @return supplier of the exception
     */
    @SuppressWarnings({"DuplicateStringLiteralInspection", "OverloadedVarargsMethod"})
    default Supplier<SE> serviceException(Throwable cause, String msg, Object... args) {
        return () -> {
            try {
                return serviceException()
                    .getConstructor(String.class, Throwable.class)
                    .newInstance(String.format(msg, args), cause);
            } catch ( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
                throw new SPIServiceException(String.format("Unable to throw %s for error: %s",
                                                            serviceException().getName(),
                                                            String.format(msg, args)),
                                              cause,
                                              e);
            }
        };
    }

    /**
     * Exception thrown if unable to locate a suitable implementation of the MessageBus interface
     */
    @SuppressWarnings({
                          "PublicInnerClass",
                          "OverloadedVarargsMethod",
                          "AssignmentOrReturnOfFieldWithMutableType",
                          "PublicMethodNotExposedInInterface"
                      })
    class SPIServiceException extends RuntimeException
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

}
