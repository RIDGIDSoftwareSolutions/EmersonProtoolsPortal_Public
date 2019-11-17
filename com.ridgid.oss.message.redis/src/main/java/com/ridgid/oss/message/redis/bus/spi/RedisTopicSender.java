package com.ridgid.oss.message.redis.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.function.BiConsumer;


public class RedisTopicSender<Topic extends Enum<Topic> & TopicEnum<Topic>> implements TopicSender<Topic> {
    private final Topic topic;
    private RTopic redisTopic;

    /*
    TODO: Features that aren't implemented yet, but not critical for functionality:
      - Supported WireFormats
      - SenderCardinality
      - ReceiverCardinality
      - DeliveryRequirement
      - ReliabilityRequirement: Currently will always behave as NONDURABLE_GUARANTEED
     */
    public RedisTopicSender(Topic topic,
                            RedissonClient redissonClient) {
        guardAgainstMultipleMessageTypes(topic);
        this.topic = topic;
        this.redisTopic = redissonClient.getTopic(topic.getTopicName());
    }

    private void guardAgainstMultipleMessageTypes(Topic topic) {
        if (topic.getMessageTypes().count() != 1) {
            throw new IllegalArgumentException("Only supports one message type at this time");
        }
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @Override
    public <MessageType> void send(MessageType message) throws TopicSenderException {
        try {
            redisTopic.publishAsync(message)
                    .onComplete((receiverCount, throwable) -> {
                        if (throwable != null) {
                            throw new RuntimeException(throwable);
                        }
                    });
        } catch (Exception e) {
            throw new TopicSenderException(topic, e);
        }

    }

    @Override
    public void close() throws Exception {
        redisTopic = null;
    }
}
