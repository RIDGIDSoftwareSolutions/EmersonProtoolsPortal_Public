package com.ridgid.oss.message.test.mock;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicSender;

/**
 * Mock MessageBus for testing the MessageBusService Loader
 */
public class MessageBusMock implements MessageBus
{
    /**
     * Create a topic on the message bus and obtain the TopicSender for the Topic.
     * If the topic already exists and the topic is not required to be single-producer, then the returned
     * sender will use the existing topic. If the topic already exists and is required to be single-producer,
     * then throws the MessageBusException.
     *
     * @param topic to create and obtain a TopicSender for
     * @return TopicSender for topic
     * @throws MessageBusException if unable to create the topic or if topic already exists and is exclusive to a single sender
     */
    @SuppressWarnings({"ReturnOfNull", "RedundantThrows"})
    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicSender<Topic> create(Topic topic)
        throws MessageBusException
    {
        return null;
    }

    /**
     * Subscribe to a topic on the message bus and return the TopicReceiver.
     * If the topic already exists and is required to have only a single subscriber/consumer and a subscriber/consumer
     * is already registered for the topic, then throws the MessageBusException. If the topic has not already been
     * created on the back-end, it is created without a designated sender and a sender may then register as the first
     * (or additional if permitted) sender at a later time. Until a sender registers and begins sending messages,
     * the Topic will simply always return nothing available to deliver until a Sender registers and sends some messages.
     *
     * @param topic to subscribe to an obtain a TopicReceiver for
     * @return TopicReceiver for the Topic
     * @throws MessageBusException if unable to subscribe to the topic due to internal errors or if the topic is intended to have only a single subscriber and already has one
     */
    @SuppressWarnings({"ReturnOfNull", "RedundantThrows"})
    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicReceiver<Topic> subscribe(Topic topic)
        throws MessageBusException
    {
        return null;
    }
}
