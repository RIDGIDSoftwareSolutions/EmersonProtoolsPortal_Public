package com.ridgid.oss.message.redis.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

public class RedisMessageBus implements MessageBus {
    private final RedissonClient redissonClient;

    public RedisMessageBus() {
        this.redissonClient = Redisson.create();
    }

    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicSender<Topic> create(Topic topic) throws MessageBusException {
        return new RedisTopicSender<>(topic, redissonClient);
    }

    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicReceiver<Topic> subscribe(Topic topic) throws MessageBusException {
        return new RedisTopicReceiver<>(topic, redissonClient);
    }
}
