package com.ridgid.oss.message;

import java.io.Serializable;

/**
 * Implementing this interface entails that the implementation provides and overrides consistent and correct
 * implementations of the following methods:
 * <p>
 * {@code Object.equals(Object o}
 * {@code Object.hashCode()}
 * <p>
 * Also, the implementation should override {@code compareTo(T o)} if specific ordering is needed, but, the default
 * implementation provided by this interface is acceptable for anything that relies on this interface.
 * *
 *
 * @param <T> Type that is implementing SerializableKey
 */
@SuppressWarnings({"unused", "NewMethodNamingConvention", "InterfaceNeverImplemented"})
public interface SerializableKey<T> extends Serializable, Comparable<T>
{
    /**
     * Obtain an empty/default instance of the {@code SerializableKey<T>}.
     *
     * @param serializableKeyClass class to obtain default/empty instance for
     * @param <T>                  type class of key
     * @return an empty/default instance of they key
     * @throws NoArgumentConstructorFailed if the {@code SerializableKey<T>} class does not have a no-argument constructor
     */
    @SuppressWarnings({"OverlyBroadCatchBlock", "ClassNewInstance"})
    static <T> T EMPTY(Class<T> serializableKeyClass)
        throws NoArgumentConstructorFailed
    {
        try {
            return serializableKeyClass.newInstance();
        } catch ( Exception e ) {
            throw new NoArgumentConstructorFailed(serializableKeyClass, e);
        }
    }

    @SuppressWarnings({"NestedConditionalExpression", "CallToSuspiciousStringMethod"})
    @Override
    default int compareTo(T o) {
        if ( o == null ) return -1;
        if ( equals(o) ) return 0;
        return hashCode() < o.hashCode()
               ? -1
               : hashCode() > o.hashCode()
                 ? 1
                 : toString().compareTo(o.toString());
    }

    /**
     * @return casted value
     */
    @SuppressWarnings("unchecked")
    default T unwrap() {
        return (T) this;
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object o);


    /**
     * Thrown when there is not a no-argument constructor available for type T and an attempt to instantiate fails due to that
     */
    @SuppressWarnings({
                          "PublicInnerClass",
                          "ExceptionClassNameDoesntEndWithException",
                          "PublicMethodNotExposedInInterface"
                      })
    final class NoArgumentConstructorFailed extends RuntimeException
    {
        private static final long serialVersionUID = -2870636374487261731L;

        private final Class<?> failedToInstantiateClass;

        private <T> NoArgumentConstructorFailed(Class<T> failedToInstantiateClass,
                                                Exception cause)
        {
            super(cause);
            this.failedToInstantiateClass = failedToInstantiateClass;
        }

        public Class<?> getFailedToInstantiateClass() {
            return failedToInstantiateClass;
        }
    }
}
