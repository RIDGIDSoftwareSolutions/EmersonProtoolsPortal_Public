package com.ridgid.oss.message.redis.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.Redisson;
import org.redisson.config.Config;

import java.text.MessageFormat;

public class RedisMessageBus implements MessageBus {
    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicSender<Topic> create(Topic topic) throws MessageBusException {
        return new RedisTopicSender<>(topic);
    }

    @Override
    public <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicReceiver<Topic> subscribe(Topic topic) throws MessageBusException {
        Config config = new Config();
        config.useSingleServer()
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2)
                .setTimeout(1000000)
                .setAddress(buildAddress())
                .setPassword(System.getProperty("redis.password", null));

        return new RedisTopicReceiver<>(topic, Redisson.create(config));
    }

    private String buildAddress() {
        String protocol = Boolean.parseBoolean(System.getProperty("redis.ssl", "false")) ? "rediss" : "redis";
        String host = System.getProperty("redis.host", "127.0.0.1");
        String port = System.getProperty("redis.port", "6379");

        return MessageFormat.format("{0}://{1}:{2}", protocol, host, port);
    }
}
