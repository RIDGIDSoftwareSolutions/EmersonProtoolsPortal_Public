package com.ridgid.oss.message.bus;

import com.ridgid.oss.message.bus.spi.TopicReceiver.TopicReceiverException;

@SuppressWarnings({"InterfaceMayBeAnnotatedFunctional", "JavaDoc"})
public interface TopicReceiverListener<Topic, MessageType> extends AutoCloseable
{
    @Override
    void close() throws TopicReceiverException;
}
