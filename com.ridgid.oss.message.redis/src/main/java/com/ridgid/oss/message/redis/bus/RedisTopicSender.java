package com.ridgid.oss.message.redis.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.Serializable;


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
                        .setTimeout(1000000)
                        .setAddress("redis://127.0.0.1:6379");
                RedissonClient client = Redisson.create(config);
                client.getTopic(topic.getTopicName())
                        .publish(message);
                client.shutdown();
            }).start();
        } catch (Exception e) {
            throw new TopicSenderException(topic, e);
        }
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void close() throws Exception {
    }
}
