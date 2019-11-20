package com.ridgid.oss.message.inmemory.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.TopicReceiver;

import java.io.Serializable;
import java.util.Optional;

class InMemoryTopicReceiver<Topic extends Enum<Topic> & TopicEnum<Topic>>
    implements TopicReceiver<Topic>
{
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private final InMemoryTopic<Topic> topic;

    @SuppressWarnings("MethodParameterOfConcreteClass")
    InMemoryTopicReceiver(InMemoryTopic<Topic> topic) {
        this.topic = topic;
    }

    @Override
    public Topic getTopic() {
        return topic.getTopic();
    }

    @Override
    public <MessageType extends Serializable>
    Optional<? extends MessageType> poll(Class<? extends MessageType> messageType,
                                         long maxWaitMilliSeconds)
        throws TopicReceiverException
    {
        return topic.poll(messageType, maxWaitMilliSeconds);
    }

    @Override
    public void close() throws Exception {
        topic.closeConsumer(this);
    }

    @SuppressWarnings("DuplicateStringLiteralInspection")
    @Override
    public String toString() {
        return "InMemoryTopicReceiver{" +
               "topic=" + topic +
               '}';
    }
}
