package com.ridgid.oss.message.redis.bus;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.TopicSender;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.io.Serializable;
import java.time.Duration;


@SuppressWarnings({"DuplicateStringLiteralInspection", "JavaDoc", "ClassHasNoToStringMethod", "WeakerAccess"})
public class RedisTopicSender<Topic extends Enum<Topic> & TopicEnum<Topic>> implements TopicSender<Topic>
{
    private final Topic  topic;
    private final RedissonClient client;
    private int pendingTransactionCount = 0;

    /*
    TODO: Features that aren't implemented yet, but not critical for functionality:
      - Supported WireFormats
      - SenderCardinality
      - ReceiverCardinality
      - DeliveryRequirement
      - ReliabilityRequirement: Currently will always behave as NONDURABLE_GUARANTEED
     */
    public RedisTopicSender(Topic topic,
                            RedissonClient redissonClient)
    {
        this.topic = topic;
        this.client = redissonClient;
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    @SuppressWarnings({"OverlyBroadCatchBlock", "ProhibitedExceptionThrown"})
    @Override
    public <MessageType extends Serializable>
    void send(MessageType message)
        throws TopicSenderException
    {
        try {
            pendingTransactionCount++;
            client.getTopic(topic.getTopicName())
                    .publishAsync(message)
                    .onComplete((receiverCount, throwable) -> {
                        pendingTransactionCount--;

                        if ( throwable != null ) throw new RuntimeException(throwable);
                    });
        } catch ( Exception e ) {
            throw new TopicSenderException(topic, e);
        }
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void close() throws Exception {
        long currentTime = System.currentTimeMillis();
        long shutdownDeadline = currentTime + Duration.ofSeconds(5).toMillis();

        while (pendingTransactionCount != 0 && currentTime < shutdownDeadline) {
            Thread.sleep(100L);
            currentTime = System.currentTimeMillis();
        }

        client.shutdown();
    }
}
