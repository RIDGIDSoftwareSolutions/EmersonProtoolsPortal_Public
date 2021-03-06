package com.ridgid.oss.message.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;

import java.util.Map;

/**
 * SPI to create and subscribe to topics on the implementation of the Message Bus
 *
 * NOTE: Implementations must have and work correctly with a no argument constructor. Implementations
 * should perform whatever configuration they need completely independently of the client using the
 * MessageBus interface. The client should have NO KNOWLEDGE of the implementation details of the
 * implementation.
 */
@SuppressWarnings("ClassNameSameAsAncestorName")
public interface MessageBus
{
    /**
     * Configure the message bus (if needed)
     *
     * @param configuration map containing implementation-specific configuration parameters
     * @throws MessageBusException if the MessageBus implementation does not support configuration or if invalid configuration parameters or combinations of parameters are given
     */
    default void config(Map<String, Object> configuration) throws MessageBusException {
        throw new MessageBusException("Configuration of this MessageBus Type not supported");
    }

    /**
     * Create a topic on the message bus and obtain the TopicSender for the Topic.
     * If the topic already exists and the topic is not required to be single-producer, then the returned
     * sender will use the existing topic. If the topic already exists and is required to be single-producer,
     * then throws the MessageBusException.
     *
     * @param topic   to create and obtain a TopicSender for
     * @param <Topic> enumeration that implements the TopicEnum interface (Names the Topic to create)
     * @return TopicSender for topic
     * @throws MessageBusException if unable to create the topic or if topic already exists and is exclusive to a single sender
     */
    <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicSender<Topic>
    create(Topic topic)
        throws MessageBusException;

    /**
     * Subscribe to a topic on the message bus and return the TopicReceiver.
     * If the topic already exists and is required to have only a single subscriber/consumer and a subscriber/consumer
     * is already registered for the topic, then throws the MessageBusException. If the topic has not already been
     * created on the back-end, it is created without a designated sender and a sender may then register as the first
     * (or additional if permitted) sender at a later time. Until a sender registers and begins sending messages,
     * the Topic will simply always return nothing available to deliver until a Sender registers and sends some messages.
     *
     * @param topic   to subscribe to an obtain a TopicReceiver for
     * @param <Topic> enumeration that implements the TopicEnum interface (Names the Topic to subscribe to)
     * @return TopicReceiver for the Topic
     * @throws MessageBusException if unable to subscribe to the topic due to internal errors or if the topic is intended to have only a single subscriber and already has one
     */
    <Topic extends Enum<Topic> & TopicEnum<Topic>> TopicReceiver<Topic>
    subscribe(Topic topic)
        throws MessageBusException;

    /**
     * Thrown by the MessageBus SPI interface when there is a failure to create or subscribe to a topic.
     */
    @SuppressWarnings({"PublicInnerClass", "JavaDoc", "ClassWithTooManyConstructors"})
    class MessageBusException extends Exception
    {
        private static final long serialVersionUID = 20526990285999056L;

        /**
         * The topic on which the exception was raised.
         */
        @SuppressWarnings("PublicField")
        public final TopicEnum<? extends Enum<?>> topic;

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        MessageBusException(Topic topic) {
            super();
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        MessageBusException(Topic topic,
                            String message)
        {
            super(message);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        MessageBusException(Topic topic,
                            String message,
                            Throwable cause)
        {
            super(message, cause);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        MessageBusException(Topic topic,
                            Throwable cause)
        {
            super(cause);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        MessageBusException(Topic topic,
                            String message,
                            Throwable cause,
                            boolean enableSuppression,
                            boolean writableStackTrace)
        {
            super(message, cause, enableSuppression, writableStackTrace);
            this.topic = topic;
        }

        @SuppressWarnings({"WeakerAccess", "AssignmentToNull"})
        public MessageBusException(String message) {
            super(message);
            topic = null;
        }
    }
}
