package com.ridgid.oss.message.inmemory.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicSender;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-Memory Implementation (In-Process/Single JVM)of the InMemoryMessageBus
 */
public class InMemoryMessageBus implements MessageBus
{
    @SuppressWarnings("StaticCollection")
    private static final
    ConcurrentMap<TopicEnum<? extends Enum<?>>, InMemoryTopic<? extends TopicEnum<? extends Enum<?>>>>
        topics = new ConcurrentHashMap<>(100);

    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>>
    TopicSender<Topic> create(Topic topic)
        throws MessageBusException
    {
        return inMemoryTopicFor(topic).create();
    }

    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>>
    TopicReceiver<Topic> subscribe(Topic topic)
        throws MessageBusException
    {
        return inMemoryTopicFor(topic).subscribe();
    }

    @SuppressWarnings("MethodReturnOfConcreteClass")
    private static <Topic extends Enum<Topic> & TopicEnum<Topic>>
    InMemoryTopic<Topic> inMemoryTopicFor(Topic topic) {
        //noinspection unchecked,CastToConcreteClass
        return (InMemoryTopic<Topic>)
            topics.computeIfAbsent(topic,
                                   t -> new InMemoryTopic<>(topic));
    }

}
