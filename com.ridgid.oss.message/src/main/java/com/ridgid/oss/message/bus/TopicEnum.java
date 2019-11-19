package com.ridgid.oss.message.bus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface for an Enum of Topics that provides meta-data about the topic for use by the MessageBus implementation
 *
 * @param <E> enum type that implements this interface
 */
@SuppressWarnings("InterfaceNeverImplemented")
public interface TopicEnum<E extends Enum<E> & TopicEnum<E>>
{
    /**
     * @return name of the tooic group, if any, for this topic
     */
    Optional<String> getGroupName();

    /**
     * @return topic name of the topic
     */
    String getTopicName();

    /**
     * @return stream of Class entries for Serializable classes that represent the Message Types permitted to send/receive on this Topic
     */
    Stream<Class<? extends Serializable>> getMessageTypes();

    /**
     * @return stream of Wire Formats that all the Message Type classes will work for
     */
    default Stream<WireFormat> getSupportedWireFormats() {
        return Stream.of(WireFormat.JAVA_SERIALIZED);
    }

    /**
     * @param formats supported by the implementation
     * @return first format supported by the messages of the topic compatible with an implementation supported format or an empty optional if none are supported
     */
    default Optional<WireFormat> getFirstSupported(WireFormat... formats) {
        return getSupportedWireFormats().filter(wf1 -> Arrays.stream(formats)
                                                             .anyMatch(wf2 -> wf1 == wf2))
                                        .findFirst();
    }

    /**
     * @return indicates the number of simultaneous receivers permitted for the topic
     */
    ReceiverCardinality getReceiverCardinality();

    /**
     * @return indicates the number of simultaneous senders permitted for the topic
     */
    SenderCardinality getSenderCardinality();

    /**
     * @return requirements for delivery of messages for the topic (how many of possible multiple receivers must receive the message to consider it delivered)
     */
    DeliveryRequirement getDeliveryRequirement();

    /**
     * @return reliability requirements for the topic (whether or not messages may be dropped and whether undelivered messages must persist across restarts)
     */
    default ReliabilityRequirement getReliabilityRequirement() {
        return new DefaultReliabilityRequirement();
    }

    /**
     * The default reliability requirement which will require guaranteed delivery that does not need to persist across restarts
     */
    @SuppressWarnings("PublicInnerClass")
    class DefaultReliabilityRequirement implements ReliabilityRequirement {}
}
