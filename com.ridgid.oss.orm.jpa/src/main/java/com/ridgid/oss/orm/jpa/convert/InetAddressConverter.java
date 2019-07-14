package com.ridgid.oss.orm.jpa.convert;

import javax.persistence.AttributeConverter;
import java.net.InetAddress;

@SuppressWarnings("unused")
public class InetAddressConverter
        extends com.ridgid.oss.orm.convert.InetAddressConverter
        implements AttributeConverter<InetAddress, byte[]> {
}
