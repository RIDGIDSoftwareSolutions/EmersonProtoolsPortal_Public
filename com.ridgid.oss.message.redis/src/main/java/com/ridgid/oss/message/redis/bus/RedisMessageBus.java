package com.ridgid.oss.message.redis.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.Redisson;

public class RedisMessageBus implements MessageBus {
    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicSender<Topic> create(Topic topic) throws MessageBusException {
        return new RedisTopicSender<>(topic, Redisson.create());
    }

    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicReceiver<Topic> subscribe(Topic topic) throws MessageBusException {
        return new RedisTopicReceiver<>(topic, Redisson.create());
    }
}
