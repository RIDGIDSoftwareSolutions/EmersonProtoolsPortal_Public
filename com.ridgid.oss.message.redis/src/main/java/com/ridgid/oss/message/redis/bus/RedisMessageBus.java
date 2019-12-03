package com.ridgid.oss.message.redis.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.Redisson;
import org.redisson.config.Config;

public class RedisMessageBus implements MessageBus {
    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicSender<Topic> create(Topic topic) throws MessageBusException {
        return new RedisTopicSender<>(topic);
    }

    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicReceiver<Topic> subscribe(Topic topic) throws MessageBusException {
        Config config = new Config();
        config.useSingleServer()
                .setConnectionPoolSize(2)
                .setTimeout(1000000)
                .setAddress("redis://127.0.0.1:6379");
        return new RedisTopicReceiver<>(topic, Redisson.create(config));
    }
}
