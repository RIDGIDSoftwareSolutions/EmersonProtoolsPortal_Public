package com.ridgid.oss.message.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;
import com.ridgid.oss.message.bus.TopicReceiverListener;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Topic Receiver to receive messages synchronously or asynchronously from a Topic
 *
 * @param <Topic> {@code TopicEnum<Topic>} for this instance
 */
public interface TopicReceiver<Topic extends Enum<Topic> & TopicEnum<Topic>>
    extends AutoCloseable
{
    /**
     * Gets the Topic {@code Topic extends Enum<Topic> & TopicEnum<Topic>}
     * S
     *
     * @return TopicEnum instance for the Topic
     */
    Topic getTopic();

    /**
     * Unsubscribe from the topic. If called repeatedly, second and subsequent calls may throw and exception of any kind,
     * but, are not guaranteed to do so. After calling unsubscribe, calling any other method on the TopicReceiver will
     * throw an exception. After calling unsubscribe, the owner of the instance should discard it. Calling unsubscribe
     * will, by default, call the close() method of the extended AutoClosable interface that must be implemented.
     *
     * @throws TopicReceiverException if the underlying implementation of either close() or unsubscribe() would throw an exception. An exception will never be thrown for an unclosed/not unsubscribed TopicReceiver.
     */
    default void unsubscribe()
        throws TopicReceiverException
    {
        try {
            close();
        } catch ( Exception e ) {
            throw new TopicReceiverException(getTopic(), e);
        }
    }

    /**
     * Poll the Topic for a message to process.
     *
     * @param maxWaitMilliseconds to wait for a message before returning an empty Optional
     * @return Optional Object representing the received message; empty Optional if timed-out before message received
     * @throws TopicReceiverException if the Topic has been unsubscribed or there is an internal, unrecoverable error.
     */
    default Optional<? extends Serializable> poll(long maxWaitMilliseconds)
        throws TopicReceiverException
    {
        return poll(Serializable.class, maxWaitMilliseconds);
    }

    /**
     * {@code maxWaitMilliseconds} defaults to 0.
     *
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default Optional<? extends Serializable> poll()
        throws TopicReceiverException
    {
        return poll(0);
    }

    /**
     * {@code maxWaitMilliseconds} is maxWait x 1,000.
     *
     * @param maxWait maximum time to wait in seconds
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default Optional<? extends Serializable> pollWaitSeconds(int maxWait)
        throws TopicReceiverException
    {
        return poll(Duration.ofSeconds(maxWait).toMillis());
    }

    /**
     * {@code maxWaitMilliseconds} is maxWait x 60,000.
     *
     * @param maxWait maximum time to wait in minutes
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default Optional<? extends Serializable> pollWaitMinutes(short maxWait)
        throws TopicReceiverException
    {
        return poll(Duration.ofMinutes(maxWait).toMillis());
    }

    /**
     * {@code maxWaitMilliseconds} is maxWait x 3,600,000.
     *
     * @param maxWait maximum time to wait in hours
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default Optional<? extends Serializable> pollWaitHours(byte maxWait)
        throws TopicReceiverException
    {
        return poll(Duration.ofHours(maxWait).toMillis());
    }

    /**
     * Poll for a specific message type. Only messages of the requested type should be processed.
     * Any messages not of the requested type must remain in the undelivered queue managed by the implementation
     * of the message bus.
     * <p>
     * {@code maxWaitMilliseconds} defaults to 0.
     *
     * @param <MessageType> that is expected to be returned from poll. The MessageType must be one of the Message Types supported by the Topic or a Superclass thereof.
     * @param messageType   Class type of message to return
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default <MessageType extends Serializable>
    Optional<? extends MessageType> poll(Class<? extends MessageType> messageType)
        throws TopicReceiverException
    {
        return poll(messageType, 0);
    }

    /**
     * Poll for a specific message type. Only messages of the requested type should be processed.
     * Any messages not of the requested type must remain in the undelivered queue managed by the implementation
     * of the message bus.
     * <p>
     * NOTE: Implementations of this interface MUST implement this method properly. It must only remove messages from the delivery queue
     * that are of the requested type or sub-class thereof. Messages awaiting deliver not of the requested type, must remain in the queue undelivered.
     *
     * @param <MessageType> that is expected to be returned from poll. The MessageType must be one of the Message Types supported by the Topic or a Superclass thereof.
     * @param messageType   Class type of message to return
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    <MessageType extends Serializable>
    Optional<? extends MessageType> poll(Class<? extends MessageType> messageType,
                                         long maxWaitMilliSeconds)
        throws TopicReceiverException;

    /**
     * Poll for a specific message type. Only messages of the requested type should be processed.
     * Any messages not of the requested type must remain in the undelivered queue managed by the implementation
     * of the message bus.
     * <p>
     * {@code maxWaitMilliseconds} is maxWait * 1,000 milliseconds.
     *
     * @param <MessageType> that is expected to be returned from poll. The MessageType must be one of the Message Types supported by the Topic or a Superclass thereof.
     * @param messageType   Class type of message to return
     * @param maxWait       in seconds
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default <MessageType extends Serializable>
    Optional<? extends MessageType> pollWaitSeconds(Class<? extends MessageType> messageType,
                                                    int maxWait)
        throws TopicReceiverException
    {
        return poll(messageType, Duration.ofSeconds(maxWait).toMillis());
    }

    /**
     * Poll for a specific message type. Only messages of the requested type should be processed.
     * Any messages not of the requested type must remain in the undelivered queue managed by the implementation
     * of the message bus.
     * <p>
     * {@code maxWaitMilliseconds} is maxWait * 60,000 milliseconds.
     *
     * @param <MessageType> that is expected to be returned from poll. The MessageType must be one of the Message Types supported by the Topic or a Superclass thereof.
     * @param messageType   Class type of message to return
     * @param maxWait       in minutes
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default <MessageType extends Serializable>
    Optional<? extends MessageType> pollWaitMinutes(Class<? extends MessageType> messageType,
                                                    short maxWait)
        throws TopicReceiverException
    {
        return poll(messageType, Duration.ofMinutes(maxWait).toMillis());
    }

    /**
     * Poll for a specific message type. Only messages of the requested type should be processed.
     * Any messages not of the requested type must remain in the undelivered queue managed by the implementation
     * of the message bus.
     * <p>
     * {@code maxWaitMilliseconds} is maxWait * 3,600,000 milliseconds.
     *
     * @param <MessageType> that is expected to be returned from poll. The MessageType must be one of the Message Types supported by the Topic or a Superclass thereof.
     * @param messageType   Class type of message to return
     * @param maxWait       in hours
     * @see #poll(long)
     */
    @SuppressWarnings("JavaDoc")
    default <MessageType extends Serializable>
    Optional<? extends MessageType> pollWaitHours(Class<? extends MessageType> messageType,
                                                  byte maxWait)
        throws TopicReceiverException
    {
        return poll(messageType, Duration.ofHours(maxWait).toMillis());
    }

    /**
     * Listen for a specific message type. Only messages of the requested type should be processed by this listener.
     * Any messages not of the requested type must remain in the undelivered queue managed by the implementation
     * of the message bus.
     * <p>
     * Listen for messages asynchronously from the topic. The returned listener can be closed which does not directly
     * cancel/close the subscription to the topic for this TopicReceiver.
     *
     * @param <MessageType> that is expected to be returned from poll. The MessageType must be one of the Message Types supported by the Topic or a Superclass thereof.
     * @param handler       to invoke when a message is received
     * @param messageType   class of the expected message
     * @return closeable listener
     */
    default <MessageType extends Serializable>
    TopicReceiverListener<Topic, ? super MessageType> listen(Class<? extends MessageType> messageType,
                                                             BiConsumer<? super Topic, ? super MessageType> handler)
    {
        return new TopicReceiverListenerImpl<>(this, messageType, handler);
    }

    /**
     * Thrown by the MessageBus SPI interface when there is a failure to create or subscribe to a topic.
     */
    @SuppressWarnings({"PublicInnerClass", "JavaDoc"})
    class TopicReceiverException extends Exception
    {
        private static final long serialVersionUID = 20526990285999056L;

        /**
         * The topic on which the exception was raised.
         */
        @SuppressWarnings("PublicField")
        public final Enum<? extends TopicEnum<?>> topic;

        public <Topic extends Enum<Topic> & TopicEnum<? super Topic>>
        TopicReceiverException(Topic topic) {
            super();
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<? super Topic>>
        TopicReceiverException(Topic topic,
                               String message)
        {
            super(message);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<? super Topic>>
        TopicReceiverException(Topic topic,
                               String message,
                               Throwable cause)
        {
            super(message, cause);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<? super Topic>>
        TopicReceiverException(Topic topic,
                               Throwable cause)
        {
            super(cause);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<? super Topic>>
        TopicReceiverException(Topic topic,
                               String message,
                               Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace)
        {
            super(message, cause, enableSuppression, writableStackTrace);
            this.topic = topic;
        }
    }

    /**
     * Listener for a TopicReceiver.
     * <p>
     * This permits registering a call-back {@code handler} to receive messages asynchronously from the Topic.
     *
     * @param <Topic>       handled by the listener
     * @param <MessageType> that will be sent to the handler
     */
    @SuppressWarnings({
                          "PublicInnerClass",
                          "OverlyNestedMethod",
                          "ClassHasNoToStringMethod",
                          "CallToThreadStartDuringObjectConstruction",
                          "WeakerAccess"
                      })
    class TopicReceiverListenerImpl<Topic extends Enum<Topic> & TopicEnum<? super Topic>, MessageType extends Serializable>
        implements TopicReceiverListener<Topic, MessageType>
    {
        private final TopicReceiver<? extends Topic>                 topicReceiver;
        private final Class<? extends MessageType>                   messageType;
        private final BiConsumer<? super Topic, ? super MessageType> handler;
        private final Thread                                         pollThread;
        private final Logger                                         logger;

        private boolean cancel = false;

        /**
         * @param topicReceiver that this listener is listening on
         * @param messageType   that the handler will receive from the topic
         * @param handler       that will be invoked whenever a new message is received
         */
        protected TopicReceiverListenerImpl(TopicReceiver<? extends Topic> topicReceiver,
                                            Class<? extends MessageType> messageType,
                                            BiConsumer<? super Topic, ? super MessageType> handler)
        {
            this.topicReceiver = topicReceiver;
            this.messageType   = messageType;
            this.handler       = handler;
            logger             = Logger.getLogger(TopicReceiver.class.getName());
            pollThread         = new Thread(this::poll);
            pollThread.start();
        }

        private void poll() {
            Consumer<? super MessageType> process        = msg -> handler.accept(topicReceiver.getTopic(), msg);
            int                           exceptionCount = 0;
            while ( true )
                try {
                    if ( cancel ) return;
                    topicReceiver.poll(messageType,
                                       1_000)
                                 .ifPresent(process);
                    exceptionCount = 0;
                } catch ( TopicReceiverException e ) {
                    //noinspection ValueOfIncrementOrDecrementUsed,MagicNumber
                    if ( ++exceptionCount > 30 ) {
                        logger.log(Level.INFO,
                                   String.format("TopicReceiverListenerImpl Failed: %s - %s",
                                                 topicReceiver.getTopic(),
                                                 e.getMessage())
                                  );
                        throw new ThreadDeath();
                    }
                    try {
                        //noinspection BusyWait
                        Thread.sleep(1000);
                    } catch ( InterruptedException ignore ) {
                    }
                }
        }

        @Override
        public void close() throws TopicReceiverException {
            cancel = true;
            try {
                //noinspection MagicNumber
                pollThread.join(2_000);
            } catch ( InterruptedException e ) {
                //noinspection unchecked
                throw new TopicReceiverException(topicReceiver.getTopic(), e);
            }
        }
    }
}
