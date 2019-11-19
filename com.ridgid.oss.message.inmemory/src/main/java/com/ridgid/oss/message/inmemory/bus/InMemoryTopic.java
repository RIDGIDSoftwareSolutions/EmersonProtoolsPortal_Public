package com.ridgid.oss.message.inmemory.bus;

import com.ridgid.oss.message.bus.ReceiverCardinality;
import com.ridgid.oss.message.bus.SenderCardinality;
import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.spi.MessageBus.MessageBusException;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicReceiver.TopicReceiverException;
import com.ridgid.oss.message.bus.spi.TopicSender;
import com.ridgid.oss.message.bus.spi.TopicSender.TopicSenderException;
import com.ridgid.oss.queue.InMemoryMultiChannelFIFOQueue;
import com.ridgid.oss.queue.spi.MultiChannelFIFOQueue;
import com.ridgid.oss.queue.spi.MultiChannelFIFOQueue.MultiChannelFIFOQueueException;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("FieldNotUsedInToString")
class InMemoryTopic<Topic extends Enum<Topic> & TopicEnum<Topic>>
{
    private final Topic                                       topic;
    private final MultiChannelFIFOQueue<? super Serializable> queues;
    private final AtomicLong                                  producers = new AtomicLong(0);
    private final AtomicLong                                  consumers = new AtomicLong(0);

    private final AtomicLong nextTimestamp = new AtomicLong(Long.MIN_VALUE);

    InMemoryTopic(Topic topic) {
        this.topic = topic;
        queues     = initQueues(topic);
    }

    private static <Topic extends Enum<Topic> & TopicEnum<Topic>>
    MultiChannelFIFOQueue<Serializable> initQueues(Topic topic) {
        return new InMemoryMultiChannelFIFOQueue<>(Serializable.class, topic.getMessageTypes());
    }

    TopicSender<Topic> create() throws MessageBusException {
        incrementProducers();
        return new InMemoryTopicSender<>(this);
    }

    TopicReceiver<Topic> subscribe() throws MessageBusException {
        incrementConsumers();
        return new InMemoryTopicReceiver<>(this);
    }

    Topic getTopic() {
        return topic;
    }

    <MessageType extends Serializable> void send(MessageType message)
        throws TopicSenderException
    {
        try {
            queues.send(message);
        } catch ( MultiChannelFIFOQueueException e ) {
            throw new TopicSenderException(topic, e);
        }
        if ( nextTimestamp.get() == Long.MIN_VALUE )
            throw new TopicSenderException(topic, "Wrap-Around on Message Time-Stamps - Re-Start Advisable");
    }

    @SuppressWarnings("MethodParameterOfConcreteClass")
    void closeProducer(InMemoryTopicSender<Topic> sender)
        throws MessageBusException
    {
        decrementProducers();
    }

    <MessageType extends Serializable>
    Optional<? extends MessageType> poll(Class<? extends MessageType> messageType,
                                         long maxWaitMilliSeconds)
        throws TopicReceiverException
    {
        try {
            return queues.poll(messageType, maxWaitMilliSeconds);
        } catch ( MultiChannelFIFOQueueException e ) {
            throw new TopicReceiverException(topic, e);
        }
    }

    @SuppressWarnings("MethodParameterOfConcreteClass")
    void closeConsumer(InMemoryTopicReceiver<Topic> receiver) throws MessageBusException {
        decrementConsumers();
    }

    @SuppressWarnings("DuplicateStringLiteralInspection")
    @Override
    public String toString() {
        return "InMemoryTopic{" +
               "topic=" + topic +
               '}';
    }

    @SuppressWarnings({"UnclearExpression", "HardcodedFileSeparator"})
    private void incrementProducers()
        throws MessageBusException
    {
        long currentProducers = producers.incrementAndGet();
        if ( currentProducers != 1 && topic.getSenderCardinality() == SenderCardinality.ONE )
            decrementProducersAndThrowError("Only 1 Producer/Sender permitted for this topic");
    }

    @SuppressWarnings("SameParameterValue")
    private void decrementProducersAndThrowError(String errorMsg) throws MessageBusException {
        decrementProducers();
        throw new MessageBusException(topic, errorMsg);
    }

    private void decrementProducers() throws MessageBusException {
        long producerCount = producers.decrementAndGet();
        ensureProducersCountIsValid(producerCount);
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    private void ensureProducersCountIsValid(long producerCount) throws MessageBusException {
        if ( producerCount >= 0 ) return;
        for ( int i = 0; (producerCount < 0) && (i < 1000); i++ )
              producerCount = producers.compareAndSet(producerCount, 0)
                              ? 0
                              : producers.get();
        if ( producerCount < 0 )
            throw new MessageBusException(topic,
                                          "Number of producers out-of-sync - unable to correct - Programming Error - Should never happen");
    }

    @SuppressWarnings({"UnclearExpression", "HardcodedFileSeparator"})
    private void incrementConsumers()
        throws MessageBusException
    {
        long currentConsumers = consumers.incrementAndGet();
        if ( currentConsumers != 1 && topic.getReceiverCardinality() == ReceiverCardinality.ONE )
            decrementConsumersAndThrowError("Only 1 Consumer/Receiver permitted for this topic");
    }

    @SuppressWarnings("SameParameterValue")
    private void decrementConsumersAndThrowError(String errorMsg) throws MessageBusException {
        decrementConsumers();
        throw new MessageBusException(topic, errorMsg);
    }

    private void decrementConsumers() throws MessageBusException {
        ensureConsumersCountIsValid(consumers.decrementAndGet());
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    private void ensureConsumersCountIsValid(long consumerCount) throws MessageBusException {
        if ( consumerCount >= 0 ) return;
        for ( int i = 0; (consumerCount < 0) && (i < 1000); i++ )
              consumerCount = consumers.compareAndSet(consumerCount, 0)
                              ? 0
                              : consumers.get();
        if ( consumerCount < 0 )
            throw new MessageBusException(topic,
                                          "Number of consumers out-of-sync - unable to correct - Programming Error - Should never happen");
    }

}
