package com.ridgid.oss.cache.redis;

import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;

public class HibernateSupportedKryo extends KryoReflectionFactorySupport {
    private static Class<?> HIBERNATE_ABSTRACT_COLLECTION_CLASS;

    static {
        try {
            HIBERNATE_ABSTRACT_COLLECTION_CLASS = Class.forName("org.hibernate.collection.AbstractPersistentCollection");
        } catch (ClassNotFoundException e) {
            try {
                HIBERNATE_ABSTRACT_COLLECTION_CLASS = Class.forName("org.hibernate.collection.internal.AbstractPersistentCollection");
            } catch (ClassNotFoundException e2) {
                HIBERNATE_ABSTRACT_COLLECTION_CLASS = null;
            }
        }
    }

    @Override
    public Serializer getDefaultSerializer(Class type) {
        if (HIBERNATE_ABSTRACT_COLLECTION_CLASS != null &&
                HIBERNATE_ABSTRACT_COLLECTION_CLASS.isAssignableFrom(type)) {
            return new FieldSerializer(this, type);
        }

        return super.getDefaultSerializer(type);
    }
}
