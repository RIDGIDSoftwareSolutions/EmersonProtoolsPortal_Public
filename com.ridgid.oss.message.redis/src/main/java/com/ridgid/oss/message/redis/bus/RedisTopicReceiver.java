package com.ridgid.oss.message.redis.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.TopicReceiverListener;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings({
                      "RedundantThrows",
                      "DuplicateStringLiteralInspection",
                      "rawtypes",
                      "WeakerAccess",
                      "JavaDoc",
                      "ClassHasNoToStringMethod"
                      , "CallToSimpleGetterFromWithinClass"
                  })
public class RedisTopicReceiver<Topic extends Enum<Topic> & TopicEnum<Topic>> implements TopicReceiver<Topic>
{
    private final Topic  topic;
    private final RedissonClient client;

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
                              RedissonClient redissonClient)
    {
        this.topic = topic;
        this.client = redissonClient;
    }

    @Override
    public <MessageType extends Serializable>
    TopicReceiverListener<Topic, ? super MessageType> listen(Class<? extends MessageType> messageType,
                                                             BiConsumer<? super Topic, ? super MessageType> handler)
    {
        MessageListener listener = (channel, msg) -> {
            if (messageType.isAssignableFrom(msg.getClass())) {
                //noinspection unchecked
                handler.accept(getTopic(), (MessageType) msg);
            }
        };
        RTopic redisTopic = client.getTopic(this.topic.getTopicName());
        //noinspection unchecked
        redisTopic.addListenerAsync(messageType, listener);
        return () -> redisTopic.removeListener(listener);
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @Override
    public <MessageType extends Serializable>
    Optional<? extends MessageType> poll(Class<? extends MessageType> messageType,
                                         long maxWaitMilliSeconds)
        throws TopicReceiverException
    {
        throw new UnsupportedOperationException("This receiver does not support polling.");
    }

    @Override
    public void close() throws Exception {
        client.shutdown();
    }
}
