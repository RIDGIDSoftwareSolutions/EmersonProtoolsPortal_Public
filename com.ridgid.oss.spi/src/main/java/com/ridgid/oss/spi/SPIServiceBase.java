package com.ridgid.oss.spi;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 */
@SuppressWarnings({
                      "JavaDoc",
                      "ClassNamePrefixedWithPackageName",
                      "ClassHasNoToStringMethod",
                      "FieldCanBeLocal",
                      "CallToSuspiciousStringMethod"
                  })
public class SPIServiceBase<SI, SE extends SPIServiceException>
    implements SPIService<SI, SE>
{
    private final Class<SI>         serviceClass;
    private final Class<SE>         serviceException;
    private final ServiceLoader<SI> loader;

    private volatile SI defaultProvider;

    protected SPIServiceBase(Class<SI> serviceClass,
                             Class<SE> serviceException)
    {
        this(serviceClass, serviceException, false);
    }

    protected SPIServiceBase(Class<SI> serviceClass,
                             Class<SE> serviceException,
                             ClassLoader classLoader)
    {
        this.serviceClass     = serviceClass;
        this.serviceException = serviceException;
        loader                = ServiceLoader.load(serviceClass, classLoader);
    }

    protected SPIServiceBase(Class<SI> serviceClass,
                             Class<SE> serviceException,
                             boolean onlyInstalled)
    {
        this.serviceClass     = serviceClass;
        this.serviceException = serviceException;
        loader                = onlyInstalled
                                ? ServiceLoader.loadInstalled(serviceClass)
                                : ServiceLoader.load(serviceClass);
    }

    @Override
    public void clearDefaultProvider() throws SE {
        synchronized ( loader ) {
            //noinspection AssignmentToNull
            defaultProvider = null;
        }
    }

    @Override
    public SI defaultProvider() throws SE {
        synchronized ( loader ) {
            Optional<String> requestedImplementation = defaultProviderPropertyValue();
            if ( defaultProviderEmptyOrDifferentFrom(requestedImplementation) )
                defaultProvider
                    = requestedImplementation
                    .map
                        (
                            className -> streamProviders()
                                .filter(lc -> lc.getClass()
                                                .getName()
                                                .equals(className))
                                .findFirst()
                                .orElseThrow(serviceException("Configured Provider Class Not Found: %s", className))
                        )
                    .orElseGet
                        (
                            () -> streamProviders().findFirst()
                                                   .orElseThrow(serviceException("No Provider Class Found"))
                        );
            return defaultProvider;
        }
    }

    @SuppressWarnings({
                          "OptionalUsedAsFieldOrParameterType",
                          "BooleanMethodNameMustStartWithQuestion",
                          "NewMethodNamingConvention"
                      })
    private boolean defaultProviderEmptyOrDifferentFrom(Optional<String> defaultProviderPropertyValue) {
        return
            defaultProvider == null
            ||
            (
                defaultProviderPropertyValue.isPresent()
                &&
                defaultProviderPropertyValue.get().equals(defaultProvider.getClass().getName())
            );
    }

    @Override
    public Class<SI> serviceInterface() {
        return serviceClass;
    }

    @Override
    public Class<SE> serviceException() {
        return serviceException;
    }

    @Override
    public Stream<SI> streamProviders() {
        return StreamSupport.stream(loader.spliterator(),
                                    false);
    }
}
