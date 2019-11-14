package com.ridgid.oss.message.bus;

import com.ridgid.oss.message.WireFormat;

import java.io.Serializable;
import java.util.stream.Stream;

public interface TopicEnum<E extends Enum<E>>
{
    Stream<Class<? super Serializable>> getMessageTypes();

    default Stream<WireFormat> getSupportedWireFormats() {
        return Stream.of(WireFormat.JAVA_SERIALIZED);
    }

    ReceiverCardinality getReceiverCardinality();
    SenderCardinality getSenderCardinality();
    DeliveryRequirement getDeliveryRequirement();

    default ReliabilityRequirement getReliabilityRequirement() {
        return new DefaultReliabilityRequirement();
    }

    @SuppressWarnings({"PublicInnerClass", "JavaDoc"})
    class DefaultReliabilityRequirement implements ReliabilityRequirement {}
}
