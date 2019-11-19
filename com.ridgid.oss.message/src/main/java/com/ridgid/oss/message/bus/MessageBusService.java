package com.ridgid.oss.message.bus;

import com.ridgid.oss.message.bus.spi.MessageBus;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Loads the configured or available MessageBus Service Provider and returns and instance of the Bus.
 */
@SuppressWarnings({
                      "PublicMethodNotExposedInInterface",
                      "ClassHasNoToStringMethod",
                      "Singleton",
                      "CallToSuspiciousStringMethod"
                  })
public final class MessageBusService
{
    private static final Object initLock = new Object();

    @SuppressWarnings("StaticCollection")
    private static final ConcurrentMap<Class<MessageBus>, MessageBus> buses = new ConcurrentHashMap<>(10);

    @SuppressWarnings("StaticVariableOfConcreteClass")
    private static MessageBusService service = null;

    private final    ServiceLoader<MessageBus> loader;
    private volatile MessageBus                defaultBus;

    private MessageBusService() {
        loader = ServiceLoader.load(MessageBus.class);
    }

    /**
     * Obtain thread-safe MessageBusService instance
     *
     * @return instance of the MessageBusService (likely Singleton)
     */
    @SuppressWarnings({
                          "SynchronizationOnStaticField",
                          "MethodReturnOfConcreteClass"
                      })
    public static MessageBusService instance() {
        synchronized ( initLock ) {
            if ( service == null ) service = new MessageBusService();
            return service;
        }
    }


    /**
     * @return the implementation of the MessageBus interface as given by the Class Name in the 'com.ridgid.oss.message.bus.service.class' system property, or,
     * if the system property is not defined, then it returns the first available implementation of the MessageBus interface that is found.
     * @throws MessageBusServiceException if either the system property does not point to a valid implementation of the ServiceBus interface, or, if there is
     *                                    no available implementation found when the system property is not given.
     */
    public MessageBus ofBus() throws MessageBusServiceException {
        synchronized ( loader ) {
            if ( defaultBus == null )
                //noinspection LambdaParameterNamingConvention
                defaultBus
                    = Optional.ofNullable(System.getProperty("com.ridgid.oss.message.bus.service.class"))
                              .map
                                  (
                                      configuredServiceClassName ->
                                          loaderStream().filter(lc -> lc.getClass()
                                                                        .getName()
                                                                        .equals(configuredServiceClassName))
                                                        .findFirst()
                                                        .orElseThrow(MessageBusServiceException::new)
                                  )
                              .orElseGet
                                  (
                                      () -> loaderStream().findFirst()
                                                          .orElseThrow(MessageBusServiceException::new)
                                  );
            return defaultBus;
        }
    }

//    public MessageBus ofBus(Class<? extends MessageBus> busClass) {
//    }
//
//    public MessageBus ofBus(Class<? extends MessageBus> busClass, Map<String, Object> config) {
//    }

    private Stream<MessageBus> loaderStream() {
        return StreamSupport.stream(loader.spliterator(),
                                    false);
    }

    /**
     * Exception thrown if unable to locate a suitable implementation of the MessageBus interface
     */
    @SuppressWarnings({"WeakerAccess", "PublicInnerClass"})
    public static class MessageBusServiceException extends RuntimeException
    {
        private static final long serialVersionUID = 2074092232499183526L;
    }
}
