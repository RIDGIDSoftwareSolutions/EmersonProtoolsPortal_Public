package com.ridgid.oss.common.jdbc.transform;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SuppressWarnings("unused")
public class InetAddressConverter implements AttributeConverter<InetAddress, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(InetAddress entityValue) {
        return entityValue.getAddress();
    }

    @Override
    public InetAddress convertToEntityAttribute(byte[] dbValue) {
        try {
            return InetAddress.getByAddress(dbValue);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
