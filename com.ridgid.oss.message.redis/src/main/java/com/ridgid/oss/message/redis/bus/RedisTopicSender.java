package com.ridgid.oss.message.redis.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.Serializable;
import java.text.MessageFormat;


@SuppressWarnings({"DuplicateStringLiteralInspection", "JavaDoc", "ClassHasNoToStringMethod", "WeakerAccess"})
public class RedisTopicSender<Topic extends Enum<Topic> & TopicEnum<Topic>> implements TopicSender<Topic> {
    private final Topic topic;

    /*
    TODO: Features that aren't implemented yet, but not critical for functionality:
      - Supported WireFormats
      - SenderCardinality
      - ReceiverCardinality
      - DeliveryRequirement
      - ReliabilityRequirement: Currently will always behave as NONDURABLE_GUARANTEED
     */
    public RedisTopicSender(Topic topic) {
        this.topic = topic;
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @SuppressWarnings({"OverlyBroadCatchBlock", "ProhibitedExceptionThrown"})
    @Override
    public <MessageType extends Serializable>
    void send(MessageType message)
            throws TopicSenderException {
        try {
            new Thread(() -> {
                Config config = new Config();
                config.useSingleServer()
                        .setConnectionPoolSize(1)
                        .setConnectionMinimumIdleSize(1)
                        .setTimeout(1000000)
                        .setAddress(buildAddress())
                        .setPassword(System.getProperty("redis.password", null));
                RedissonClient client = Redisson.create(config);
                client.getTopic(topic.getTopicName())
                        .publish(message);
                client.shutdown();
            }).start();
        } catch (Exception e) {
            throw new TopicSenderException(topic, e);
        }
    }

    private String buildAddress() {
        String protocol = Boolean.parseBoolean(System.getProperty("redis.ssl", "false")) ? "rediss" : "redis";
        String host = System.getProperty("redis.url", "127.0.0.1");
        String port = System.getProperty("redis.port", "6379");

        return MessageFormat.format("{0}://{1}:{2}", protocol, host, port);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void close() throws Exception {
    }
}
