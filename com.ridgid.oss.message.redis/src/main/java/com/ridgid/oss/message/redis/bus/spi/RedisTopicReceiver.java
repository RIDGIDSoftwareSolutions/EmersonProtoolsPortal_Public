package com.ridgid.oss.message.redis.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.TopicReceiverListener;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.util.Optional;
import java.util.function.BiConsumer;

public class RedisTopicReceiver<Topic extends Enum<Topic> & TopicEnum<Topic>> implements TopicReceiver<Topic> {
    private final Topic topic;
    private final RTopic redisTopic;

    // Keeps a hold of all incoming messages during the polling period
    // TODO: Only supports one MessageType for now
    //private final Queue<Object> messageQueue;

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
        this.redisTopic = redissonClient.getTopic(topic.getTopicName());

        guardAgainstMultipleMessageTypes(topic);
    }

    @Override
    public <MessageType> TopicReceiverListener<Topic, MessageType> listen(Class<? extends MessageType> messageType, BiConsumer<? super Topic, ? super MessageType> handler) {
        MessageListener listener = (channel, msg) -> {
            //noinspection unchecked
            handler.accept(getTopic(), (MessageType) msg);
        };
        //noinspection unchecked
        redisTopic.addListenerAsync(messageType, listener);

        return () -> redisTopic.removeListener(listener);
    }

    private void guardAgainstMultipleMessageTypes(Topic topic) {
        if (topic.getMessageTypes().count() > 1) {
            throw new IllegalArgumentException("Only supports one message type at this time");
        }
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @Override
    public <MessageType> Optional<MessageType> poll(Class<? extends MessageType> messageType, long maxWaitMilliSeconds) throws TopicReceiverException {
        throw new UnsupportedOperationException("This receiver does not support polling.");
    }

    @Override
    public void close() throws Exception {
    }
}
