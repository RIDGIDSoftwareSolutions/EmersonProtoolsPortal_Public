package com.ridgid.oss.queue.impl.inmemory;

import com.ridgid.oss.queue.spi.MultiChannelFIFOQueue;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.util.Comparator.comparingLong;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toConcurrentMap;

/**
 * In-Memory Implementation of a MultiChannelFIFOQueue
 *
 * @param <BaseMessageType> of messages sent and received through the queue
 */
@SuppressWarnings({"WeakerAccess", "NewMethodNamingConvention", "ClassNamePrefixedWithPackageName"})
public class InMemoryMultiChannelFIFOQueue<BaseMessageType extends Serializable>
    implements MultiChannelFIFOQueue<BaseMessageType>
{
    private final AtomicLong nextTimestamp = new AtomicLong(Long.MIN_VALUE);

    private final
    Class<BaseMessageType> baseMessageType;

    private final
    ConcurrentMap<Class<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>
        queues;

    /**
     * Construct a single-channel queue where the single channel will handle all messages
     *
     * @param baseMessageType of the messages to send and receive through the queue
     */
    @SuppressWarnings("BoundedWildcard")
    public InMemoryMultiChannelFIFOQueue(Class<BaseMessageType> baseMessageType) {
        this.baseMessageType = baseMessageType;
        queues               = makeEmptyConcurrentQueues(Stream.of(baseMessageType));
    }

    /**
     * Construct a multi-channel queue where all of the messages extend the base message type, but, there exists
     * multiple channels corresponding to the given list of message types
     *
     * @param baseMessageType of all messages in the queue across all channels
     * @param queues          of the messages for each channel
     */
    @SuppressWarnings("BoundedWildcard")
    public InMemoryMultiChannelFIFOQueue(Class<BaseMessageType> baseMessageType,
                                         Collection<Class<? extends BaseMessageType>> queues)
    {
        this.baseMessageType = baseMessageType;
        this.queues          = makeEmptyConcurrentQueues(queues.stream());
    }

    /**
     * Construct a multi-channel queue where all of the messages extend the base message type, but, there exists
     * multiple channels corresponding to the given list of message types
     *
     * @param baseMessageType of all messages in the queue across all channels
     * @param queues          of the messages for each channel
     */
    @SuppressWarnings({"OverloadedVarargsMethod", "BoundedWildcard"})
    @SafeVarargs
    public InMemoryMultiChannelFIFOQueue(Class<BaseMessageType> baseMessageType,
                                         Class<? extends BaseMessageType>... queues)
    {
        this.baseMessageType = baseMessageType;
        this.queues          = makeEmptyConcurrentQueues(Arrays.stream(queues));
    }

    /**
     * Construct a multi-channel queue where all of the messages extend the base message type, but, there exists
     * multiple channels corresponding to the given list of message types
     *
     * @param baseMessageType of all messages in the queue across all channels
     * @param queues          of the messages for each channel
     */
    @SuppressWarnings("BoundedWildcard")
    public InMemoryMultiChannelFIFOQueue(Class<BaseMessageType> baseMessageType,
                                         Stream<Class<? extends BaseMessageType>> queues)
    {
        this.baseMessageType = baseMessageType;
        this.queues          = makeEmptyConcurrentQueues(queues);
    }

    private static <BaseMessageType extends Serializable>
    ConcurrentMap<Class<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>
    makeEmptyConcurrentQueues(Stream<Class<? extends BaseMessageType>> queues)
    {
        return queues.collect(toConcurrentMap(identity(),
                                              InMemoryMultiChannelFIFOQueue::makeEmptyConcurrentQueue));
    }

    private static <BaseMessageType> ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>
    makeEmptyConcurrentQueue(Class<? extends BaseMessageType> messageType)
    {
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Class<? extends BaseMessageType> getBaseMessageType() {
        return baseMessageType;
    }

    @Override
    public Stream<Class<? extends BaseMessageType>> streamChannelMessageTypes() {
        return queues.keySet().stream();
    }

    @Override
    public <MessageType extends BaseMessageType>
    Optional<? extends MessageType> pollUnchecked(Class<? extends MessageType> messageType,
                                                  long maxWaitMillis)
        throws MultiChannelFIFOQueueException
    {
        return Optional.ofNullable
            (
                waitForNextAvailable
                    (
                        messageType,
                        currentTimeMillis() + maxWaitMillis
                    )
            );
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private <MessageType extends BaseMessageType>
    MessageType waitForNextAvailable(Class<? extends MessageType> messageType,
                                     long endTimeMillis)
        throws MultiChannelFIFOQueueException
    {
        try {
            MessageType available;
            do available = nextAvailableMessageForRequestedMessageType(messageType);
            while
            (
                available == null
                &&
                waitingForMessagesUntil(endTimeMillis)
            );
            return available;
        } catch ( Exception e ) {
            throw new MultiChannelFIFOQueueException(e);
        }
    }

    @SuppressWarnings("NakedNotify")
    @Override
    public <MessageType extends BaseMessageType>
    void sendUnchecked(MessageType message)
        throws MultiChannelFIFOQueueException
    {
        queueMessageToMostAppropriateQueue(message);
        synchronized ( queues ) {
            queues.notifyAll();
        }
    }

    private <MessageType extends BaseMessageType>
    void queueMessageToMostAppropriateQueue(MessageType message)
        throws MultiChannelFIFOQueueException
    {
        Stream.concat(exactQueueForMessageType(message),
                      acceptableQueuesForMessageType(message))
              .findFirst()
              .map(Entry::getValue)
              .filter(messageQueued(message))
              .orElseThrow
                  (
                      () -> new MultiChannelFIFOQueueException
                          (
                              String.format("Message not supported on any channel: %s, %s",
                                            message.getClass().getName(),
                                            message)
                          )
                  );
    }

    private <MessageType extends BaseMessageType>
    Stream<Entry<Class<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>>
    acceptableQueuesForMessageType(MessageType message)
    {
        return queues
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().isAssignableFrom(message.getClass()));
    }

    private <MessageType extends BaseMessageType>
    Stream<Entry<Class<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>>
    exactQueueForMessageType(MessageType message)
    {
        return queues
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey()
                                  .isAssignableFrom(message.getClass())
                             && message.getClass()
                                       .isAssignableFrom(entry.getKey()));
    }

    @SuppressWarnings("LocalVariableOfConcreteClass")
    private <MessageType extends BaseMessageType>
    Predicate<ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>
    messageQueued(MessageType message)
    {
        Timestamped<MessageType> msg = new Timestamped<>(message, nextTimestamp);
        return queue -> queue.offer(msg);
    }

    private <MessageType extends BaseMessageType>
    MessageType nextAvailableMessageForRequestedMessageType(Class<? extends MessageType> messageType)
    {
        return
            nextMessageFromQueueOfExactMessageType(messageType)
                .map(msg -> (MessageType) msg)
                .orElseGet(() -> earliestMessageWhereSuperTypeIs(messageType));
    }

    @SuppressWarnings({"WaitNotInLoop", "OverlyNestedMethod", "BooleanMethodNameMustStartWithQuestion"})
    private <MessageType extends BaseMessageType>
    boolean waitingForMessagesUntil(long untilMillis)
    {
        long maxWaitMillis = untilMillis - currentTimeMillis();
        if ( maxWaitMillis > 0 )
            synchronized ( queues ) {
                try { queues.wait(maxWaitMillis); } catch ( InterruptedException ignore ) {}
            }
        return currentTimeMillis() < untilMillis;
    }

    @SuppressWarnings({"unchecked", "NewMethodNamingConvention"})
    private <MessageType extends BaseMessageType> Optional<? extends MessageType>
    nextMessageFromQueueOfExactMessageType(Class<? extends MessageType> messageType)
    {
        return Optional.ofNullable(queues.get(messageType))
                       .map(ConcurrentLinkedQueue::poll)
                       .map(Timestamped::unwrap)
                       .map(msg -> (MessageType) msg);
    }

    @SuppressWarnings("LocalVariableOfConcreteClass")
    private <MessageType extends BaseMessageType>
    MessageType earliestMessageWhereSuperTypeIs(Class<? extends MessageType> messageType) {
        for
        (
            Entry<Timestamped<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>
                nextApplicableQueue : headsOfQueuesSortedByFIFOOrder(messageType)
        ) {
            Timestamped<? extends BaseMessageType> val = nextApplicableQueue.getValue().poll();
            if ( val != null ) return messageType.cast(val.unwrap());
        }
        return null;
    }

    private <MessageType extends BaseMessageType>
    Iterable<SimpleEntry<Timestamped<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>>
    headsOfQueuesSortedByFIFOOrder(Class<? extends MessageType> messageType)
    {
        return
            queuesForBaseMessageType(messageType)
                .map(Entry::getValue)
                .map(toHeadOfQueueAndQueue())
                .filter(whereNonNullHeadOfQueue())
                .sorted(comparingLong(entry -> entry.getKey().getTimestamp()))
                ::iterator;
    }

    private <MessageType extends BaseMessageType>
    Stream<? extends Entry<Class<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>>
    queuesForBaseMessageType(Class<? extends MessageType> messageType)
    {
        return queues.entrySet()
                     .stream()
                     .filter(whereAssignableTo(messageType));
    }

    private Predicate<SimpleEntry<Timestamped<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>>
    whereNonNullHeadOfQueue()
    {
        return entry -> Objects.nonNull(entry.getKey());
    }

    private <MessageType extends BaseMessageType>
    Predicate<Entry<Class<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>>
    whereAssignableTo(Class<? extends MessageType> messageType)
    {
        return entry -> messageType.isAssignableFrom(entry.getKey());
    }

    private Function<ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>, SimpleEntry<Timestamped<? extends BaseMessageType>, ConcurrentLinkedQueue<Timestamped<? extends BaseMessageType>>>>
    toHeadOfQueueAndQueue()
    {
        return queue -> new SimpleEntry<>(queue.peek(), queue);
    }

    /**
     * Note: this class has a natural ordering that is inconsistent with equals.
     */
    private static final class Timestamped<T>
        implements Comparable<Timestamped<T>>
    {
        private final T    obj;
        private final long timestamp;

        private Timestamped(T obj, AtomicLong nextTimestamp) {
            this.obj  = obj;
            timestamp = nextTimestamp.getAndIncrement();
        }

        public T unwrap() {
            return obj;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @SuppressWarnings("MethodParameterOfConcreteClass")
        @Override
        public int compareTo(Timestamped<T> o) {
            return Long.compare(timestamp, o.timestamp);
        }

        @Override
        public String toString() {
            return "Timestamped{" +
                   "obj=" + obj +
                   ", timestamp=" + timestamp +
                   '}';
        }

    }

    @Override
    public String toString() {
        return "InMemoryMultiChannelFIFOQueue{" +
               "nextTimestamp=" + nextTimestamp +
               ", baseMessageType=" + baseMessageType +
               ", queues=" + queues +
               '}';
    }
}
