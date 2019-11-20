package com.ridgid.oss.spi;


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
     * @return the implementation of the SPI  interface as given by the Class Name in the 'system property' corresponding
     * to {@code defaultProviderPropertyName()} , or, if the system property is not defined, then it returns the first available
     * implementation of the MessageBus interface that is found on the class-path that follows the Java SPI specification
     * for providing an implementation of the SPI Service via the META-INF/services/ meta-data.
     * @throws SE if either the system property does not point to a valid implementation of the ServiceBus interface, or,
     *            if there is no available implementation found when the system property is not given.
     */
    SI defaultProvider() throws SE;

    /**
     * Clears the currently set default provider. If {@code defaultProvider()} is called, then, the system property
     * is changed and {@code defaultProvider()} will still return the same provider as originally returned.
     * If this method is called, the currently loaded default is discarded, and then subsequent calls to
     * {@code defaultProvider()} will return the provider corresponding to the system property value.
     *
     * @throws SE when it cannot clear the default provider
     */
    void clearDefaultProvider() throws SE;

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
        //noinspection StringToUpperCaseOrToLowerCaseWithoutLocale
        return serviceInterface()
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


}
