package com.ridgid.oss.cache.redis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.net.InetAddress;
import java.net.UnknownHostException;

// Not serializing the hostname, as retrieving it results in a DNS lookup
public class InetAddressSerializer extends Serializer<InetAddress> {
    @Override
    public void write(Kryo kryo, Output output, InetAddress address) {
        output.writeInt(address.getAddress().length);
        output.writeBytes(address.getAddress());
    }

    @Override
    public InetAddress read(Kryo kryo, Input input, Class aClass) {
        int arrayLength = input.readInt();
        byte[] address = input.readBytes(arrayLength);

        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new KryoException(e);
        }
    }
}
