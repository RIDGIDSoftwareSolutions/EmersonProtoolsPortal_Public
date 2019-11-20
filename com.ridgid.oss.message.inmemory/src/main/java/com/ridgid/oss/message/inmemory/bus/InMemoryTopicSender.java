package com.ridgid.oss.message.inmemory.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus.MessageBusException;
import com.ridgid.oss.message.bus.spi.TopicSender;

import java.io.Serializable;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
class InMemoryTopicSender<Topic extends Enum<Topic> & TopicEnum<Topic>>
    implements TopicSender<Topic>
{
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private final InMemoryTopic<Topic> topic;

    @SuppressWarnings("MethodParameterOfConcreteClass")
    InMemoryTopicSender(InMemoryTopic<Topic> topic) {
        this.topic = topic;
    }

    @Override
    public Topic getTopic() {
        return topic.getTopic();
    }

    @Override
    public <MessageType extends Serializable> void send(MessageType message) throws TopicSenderException {
        topic.send(message);
    }

    @Override
    public void close() throws MessageBusException {
        topic.closeProducer(this);
    }

    @SuppressWarnings("DuplicateStringLiteralInspection")
    @Override
    public String toString() {
        return "InMemoryTopicSender{" +
               "topic=" + topic +
               '}';
    }
}
