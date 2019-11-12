package com.ridgid.oss.cache.redis;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ridgid.oss.cache.redis.serializer.InetAddressSerializer;
import de.javakaffee.kryoserializers.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RidgidKryoCodec extends BaseCodec {

    public interface KryoPool {

        Kryo get();

        void yield(Kryo kryo);

        ClassLoader getClassLoader();

        List<Class<?>> getClasses();

    }

    public static class KryoPoolImpl implements KryoPool {

        private final Queue<Kryo> objects = new ConcurrentLinkedQueue<Kryo>();
        private final List<Class<?>> classes;
        private final ClassLoader classLoader;

        public KryoPoolImpl(List<Class<?>> classes, ClassLoader classLoader) {
            this.classes = classes;
            this.classLoader = classLoader;
        }

        public Kryo get() {
            Kryo kryo = objects.poll();
            if (kryo == null) {
                kryo = createInstance();
            }
            return kryo;
        }

        public void yield(Kryo kryo) {
            objects.offer(kryo);
        }

        /**
         * Sub classes can customize the Kryo instance by overriding this method
         *
         * @return create Kryo instance
         */
        protected Kryo createInstance() {
            Kryo kryo = new HibernateSupportedKryo();

            /*
               There are several situations where Kryo cannot properly build a certain collection (such as an ArrayList from Arrays.asList).
               These additional serializers help solve this issue. More serializers can be added/created as needed.
             */
            //noinspection ArraysAsListWithZeroOrOneArgument
            kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
            kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
            kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
            kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
            kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
            kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
            kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
            kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
            kryo.register(InvocationHandler.class, new JdkProxySerializer());
            kryo.register(Inet4Address.class, new InetAddressSerializer());
            kryo.register(Inet6Address.class, new InetAddressSerializer());
            UnmodifiableCollectionsSerializer.registerSerializers(kryo);
            SynchronizedCollectionsSerializer.registerSerializers(kryo);

            if (classLoader != null) {
                kryo.setClassLoader(classLoader);
            }
            kryo.setReferences(true);
            for (Class<?> clazz : classes) {
                kryo.register(clazz);
            }
            return kryo;
        }

        public List<Class<?>> getClasses() {
            return classes;
        }

        @Override
        public ClassLoader getClassLoader() {
            return classLoader;
        }

    }

    public class RidgidKryoCodecException extends RuntimeException {

        private static final long serialVersionUID = 9172336149805414947L;

        public RidgidKryoCodecException(Throwable cause) {
            super(cause.getMessage(), cause);
            setStackTrace(cause.getStackTrace());
        }
    }

    private final RidgidKryoCodec.KryoPool kryoPool;

    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf buf, State state) throws IOException {
            Kryo kryo = null;
            try {
                kryo = kryoPool.get();
                return kryo.readClassAndObject(new Input(new ByteBufInputStream(buf)));
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RidgidKryoCodecException(e);
            } finally {
                if (kryo != null) {
                    kryoPool.yield(kryo);
                }
            }
        }
    };

    private final Encoder encoder = new Encoder() {

        @Override
        public ByteBuf encode(Object in) throws IOException {
            Kryo kryo = null;
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try {
                ByteBufOutputStream baos = new ByteBufOutputStream(out);
                Output output = new Output(baos);
                kryo = kryoPool.get();
                kryo.writeClassAndObject(output, in);
                output.close();
                return baos.buffer();
            } catch (Exception e) {
                out.release();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RidgidKryoCodecException(e);
            } finally {
                if (kryo != null) {
                    kryoPool.yield(kryo);
                }
            }
        }
    };

    public RidgidKryoCodec() {
        this(Collections.<Class<?>>emptyList());
    }

    public RidgidKryoCodec(ClassLoader classLoader) {
        this(Collections.<Class<?>>emptyList(), classLoader);
    }

    public RidgidKryoCodec(ClassLoader classLoader, RidgidKryoCodec codec) {
        this(codec.kryoPool.getClasses(), classLoader);
    }

    public RidgidKryoCodec(List<Class<?>> classes) {
        this(classes, null);
    }

    public RidgidKryoCodec(List<Class<?>> classes, ClassLoader classLoader) {
        this(new RidgidKryoCodec.KryoPoolImpl(classes, classLoader));
    }

    public RidgidKryoCodec(RidgidKryoCodec.KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (kryoPool.getClassLoader() != null) {
            return kryoPool.getClassLoader();
        }
        return super.getClassLoader();
    }

}
