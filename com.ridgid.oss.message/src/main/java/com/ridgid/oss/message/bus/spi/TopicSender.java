package com.ridgid.oss.message.bus.spi;

import com.ridgid.oss.message.bus.TopicEnum;

/**
 * Topic Sender that permits sending messages to a topic.
 *
 * @param <Topic> that the Sender will send messages to
 */
@SuppressWarnings("InterfaceNeverImplemented")
public interface TopicSender<Topic extends Enum<Topic> & TopicEnum<Topic>>
    extends AutoCloseable
{
    /**
     * Gets the Topic {@code Topic extends Enum<Topic> & TopicEnum<Topic>}
     *
     * @return TopicEnum instance for the Topic
     */
    Topic getTopic();

    /**
     * Send a message to the topic.
     * <p>
     * NOTE: Correct implementations of this method may never block. They must do one of the following:
     * 1. Send the message without delay and return immediately
     * 2. Queue the message internally without delay and return immediately and then send within some minimal (as per the Topic requirements) amount of time as long as the Topic is not guaranteed delivery
     * 3. Drop the message and return immediately (provided the Topic permits dropping messages)
     * 4. throw an exception if the message cannot be sent
     *
     * @param message       message to send
     * @param <MessageType> of message
     * @throws TopicSenderException if the MessageType is not compatible/sendable for the Topic or if there is an internal error.
     */
    <MessageType> void send(MessageType message) throws TopicSenderException;

    /**
     * Thrown by the MessageBus SPI interface when there is a failure to create or subscribe to a topic.
     */
    @SuppressWarnings({"PublicInnerClass", "JavaDoc", "WeakerAccess"})
    class TopicSenderException extends Exception
    {
        private static final long serialVersionUID = 20526990285999056L;

        /**
         * The topic on which the exception was raised.
         */
        @SuppressWarnings("PublicField")
        public final TopicEnum<? extends Enum<?>> topic;

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        TopicSenderException(Topic topic) {
            super();
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        TopicSenderException(Topic topic,
                             String message)
        {
            super(message);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        TopicSenderException(Topic topic,
                             String message,
                             Throwable cause)
        {
            super(message, cause);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        TopicSenderException(Topic topic,
                             Throwable cause)
        {
            super(cause);
            this.topic = topic;
        }

        public <Topic extends Enum<Topic> & TopicEnum<Topic>>
        TopicSenderException(Topic topic,
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
