package com.ridgid.oss.message.bus;

import com.ridgid.oss.message.bus.spi.TopicReceiver.TopicReceiverException;

/**
 * @param <Topic>       type of the topic enumeration
 * @param <MessageType> type of the message the listener is expected to receive.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface TopicReceiverListener<Topic extends Enum<Topic> & TopicEnum<Topic>, MessageType> extends AutoCloseable
{
    @Override
    void close() throws TopicReceiverException;
}
