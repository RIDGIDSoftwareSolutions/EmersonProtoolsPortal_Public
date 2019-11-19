package com.ridgid.oss.message.bus;

import com.ridgid.oss.message.bus.spi.TopicReceiver.TopicReceiverException;

import java.io.Serializable;

/**
 * @param <Topic>       type of the topic enumeration
 * @param <MessageType> type of the message the listener is expected to receive.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface TopicReceiverListener<Topic extends Enum<? super Topic> & TopicEnum<? super Topic>, MessageType extends Serializable>
    extends AutoCloseable
{
    @Override
    void close() throws TopicReceiverException;
}
