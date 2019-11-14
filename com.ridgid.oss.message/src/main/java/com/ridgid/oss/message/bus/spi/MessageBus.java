package com.ridgid.oss.message.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;

/**
 * SPI to create and subscribe to topics on the implementation of the Message Bus
 */
@SuppressWarnings("InterfaceNeverImplemented")
public interface MessageBus
{
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
     * If the topic already exists and is required to have only a single subscriber/consumer and a subsriber/consuer
     * is already registered for the topic, then throws the MessageBusException.
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
    @SuppressWarnings({"PublicInnerClass", "JavaDoc", "WeakerAccess"})
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
    }
}
