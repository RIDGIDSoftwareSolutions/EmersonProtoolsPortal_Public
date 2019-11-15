package com.ridgid.oss.message.redis.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class RedisTopicReceiver<Topic extends Enum<Topic> & TopicEnum<Topic>> implements TopicReceiver<Topic> {
    private final Topic topic;
    private final RTopic redisTopic;
    private int listenerId;

    // Keeps a hold of all incoming messages during the polling period
    // TODO: Only supports one MessageType for now
    private final Queue<Object> messageQueue;

    /*
    TODO: Features that aren't implemented yet, but not critical for functionality:
      - Supported WireFormats
      - SenderCardinality
      - ReceiverCardinality
      - DeliveryRequirement
      - ReliabilityRequirement: Currently will always behave as NONDURABLE_GUARANTEED
     */
    public RedisTopicReceiver(Topic topic,
                              RedissonClient redissonClient) {
        this.topic = topic;
        this.messageQueue = new ArrayDeque<>();
        redisTopic = redissonClient.getTopic(topic.getTopicName());

        redisTopic.addListenerAsync(Object.class, (channel, msg) -> messageQueue.add(msg))
                .onComplete((integer, throwable) -> {
                    if (throwable != null) {
                        throw new RuntimeException(throwable);
                    }

                    listenerId = integer;
                });

    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @Override
    public <MessageType> Optional<MessageType> poll(Class<? extends MessageType> messageType, long maxWaitMilliSeconds) throws TopicReceiverException {
        try {
            long currentTime = System.currentTimeMillis();
            long timeoutTime = currentTime + maxWaitMilliSeconds;

            while (currentTime < timeoutTime) {
                if (!messageQueue.isEmpty() && messageType.isAssignableFrom(messageQueue.peek().getClass())) {
                    //noinspection unchecked
                    return Optional.of((MessageType) messageQueue.poll());
                }

                currentTime = System.currentTimeMillis();
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new TopicReceiverException(topic, e);
        }
    }

    @Override
    public void close() throws Exception {
        redisTopic.removeListener(listenerId);
    }
}
