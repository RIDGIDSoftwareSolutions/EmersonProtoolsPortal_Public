package com.ridgid.oss.queue.spi;

import com.ridgid.oss.spi.SPIServiceException;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import static com.ridgid.oss.common.function.Predicates.whereEither;

/**
 * A multi-channel, thread-safe, FIFO queue.
 * <p>
 * The queue supporta multiple channels (1 per message type) and permits getting the next message in the queue
 * without considering channel, or getting the next message for a specific channel.
 *
 * @param <BaseMessageType> of messages in the queue. All message sent to the queue must extend from this type.
 */
public interface MultiChannelFIFOQueue<BaseMessageType extends Serializable>
{
    /**
     * @return the class of the BaseMessageType
     */
    Class<? extends BaseMessageType> getBaseMessageType();

    /**
     * @return message type classes of the message types of the configured channels
     */
    Stream<Class<? extends BaseMessageType>> streamChannelMessageTypes();

    /**
     * Checks if a given Class Type is supported as a message type of this queue.
     *
     * @param messageType   class to check if is supported by this queue
     * @param <MessageType> class Type of message to check
     * @throws MultiChannelFIFOQueueException if the message type is not compatible with the queue
     */
    default <MessageType extends BaseMessageType>
    void validatePollMessageType(Class<? extends MessageType> messageType)
        throws MultiChannelFIFOQueueException
    {
        streamChannelMessageTypes()
            .filter
                (
                    whereEither
                        (
                            messageType::isAssignableFrom,
                            mt -> mt.isAssignableFrom(messageType)
                        )
                )
            .findAny()
            .orElseThrow(MultiChannelFIFOQueueException::new);
    }

    /**
     * @param message       message to check
     * @param <MessageType> class Type of message to check
     * @throws MultiChannelFIFOQueueException if the message type is not compatible with the queue
     */
    default <MessageType extends BaseMessageType>
    void validateSendMessageType(MessageType message)
        throws MultiChannelFIFOQueueException
    {
        streamChannelMessageTypes()
            .filter(mt -> mt.isAssignableFrom(message.getClass()))
            .findAny()
            .orElseThrow(MultiChannelFIFOQueueException::new);
    }

    /**
     * @return next message in FIFO order regardless of channel
     * @throws MultiChannelFIFOQueueException for any error resulting in lost messages
     */
    default Optional<? extends BaseMessageType> poll()
        throws MultiChannelFIFOQueueException
    {
        return poll(getBaseMessageType(), 0);
    }

    /**
     * @param maxWaitMillis time in milliseconds to wait for a message to appear
     * @return next message in FIFO order regardless of channel
     * @throws MultiChannelFIFOQueueException for any error resulting in lost messages
     */
    default Optional<? extends BaseMessageType> poll(long maxWaitMillis)
        throws MultiChannelFIFOQueueException
    {
        return poll(getBaseMessageType(), maxWaitMillis);
    }

    /**
     * @param <MessageType> of the message channel to poll
     * @param messageType   Class of the message channel to poll
     * @return next message of the requested message type in FIFO order
     * @throws MultiChannelFIFOQueueException for any error resulting in lost messages
     */
    default <MessageType extends BaseMessageType>
    Optional<? extends MessageType> poll(Class<? extends MessageType> messageType)
        throws MultiChannelFIFOQueueException
    {
        return poll(messageType, 0);
    }

    /**
     * @param <MessageType> of the message channel to poll
     * @param messageType   Class of the message channel to poll
     * @param maxWaitMillis time in milliseconds to wait for a message to appear
     * @return next message of the requested message type in FIFO order
     * @throws MultiChannelFIFOQueueException for any error resulting in lost messages
     */
    default <MessageType extends BaseMessageType>
    Optional<? extends MessageType> poll(Class<? extends MessageType> messageType,
                                         long maxWaitMillis)
        throws MultiChannelFIFOQueueException
    {
        validatePollMessageType(messageType);
        return pollUnchecked(messageType, maxWaitMillis);
    }

    /**
     * @param <MessageType> of the message channel to poll
     * @param messageType   Class of the message channel to poll
     * @param maxWaitMillis time in milliseconds to wait for a message to appear
     * @return next message of the requested message type in FIFO order
     * @throws MultiChannelFIFOQueueException for any error resulting in lost messages
     */
    <MessageType extends BaseMessageType>
    Optional<? extends MessageType> pollUnchecked(Class<? extends MessageType> messageType,
                                                  long maxWaitMillis)
        throws MultiChannelFIFOQueueException;

    /**
     * @param <MessageType> of the message channel to poll
     * @param message       to send
     * @throws MultiChannelFIFOQueueException for any error resulting in the loss of messages
     */
    default <MessageType extends BaseMessageType>
    void send(MessageType message) throws MultiChannelFIFOQueueException {
        validateSendMessageType(message);
        sendUnchecked(message);
    }

    /**
     * @param <MessageType> of the message channel to poll
     * @param message       to send
     * @throws MultiChannelFIFOQueueException for any error resulting in the loss of messages
     */
    <MessageType extends BaseMessageType>
    void sendUnchecked(MessageType message) throws MultiChannelFIFOQueueException;

    /**
     * Exception thrown by MultiChannelFIFOQueue
     */
    @SuppressWarnings({"PublicInnerClass", "OverloadedVarargsMethod", "ClassWithTooManyConstructors"})
    final class MultiChannelFIFOQueueException
        extends SPIServiceException
    {
        private static final long serialVersionUID = 8095908195733551195L;

        /**
         * No argument constructor
         */
        @SuppressWarnings("WeakerAccess")
        public MultiChannelFIFOQueueException() {
            super();
        }

        /**
         * @param cause of exception
         */
        public MultiChannelFIFOQueueException(Exception cause) {
            super(cause);
        }

        /**
         * @param message explanation of error
         */
        public MultiChannelFIFOQueueException(String message) {
            super(message);
        }

        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         *
         * @param secondaryCauses optional additional causes to attach to the exception
         */
        public MultiChannelFIFOQueueException(Throwable... secondaryCauses) {
            super(secondaryCauses);
        }

        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message         the detail message (which is saved for later retrieval
         *                        by the {@link #getMessage()} method).
         * @param cause           the cause (which is saved for later retrieval by the
         *                        {@link #getCause()} method).  (A <tt>null</tt> value is
         *                        permitted, and indicates that the cause is nonexistent or
         *                        unknown.)
         * @param secondaryCauses optional additional causes to attach to the exception
         * @since 1.4
         */
        public MultiChannelFIFOQueueException(String message,
                                              Throwable cause,
                                              Throwable... secondaryCauses)
        {
            super(message, cause, secondaryCauses);
        }

        /**
         * Constructs a new runtime exception with the specified detail
         * message, cause, suppression enabled or disabled, and writable
         * stack trace enabled or disabled.
         *
         * @param message            the detail message.
         * @param cause              the cause.  (A {@code null} value is permitted,
         *                           and indicates that the cause is nonexistent or unknown.)
         * @param enableSuppression  whether or not suppression is enabled
         *                           or disabled
         * @param writableStackTrace whether or not the stack trace should
         *                           be writable
         * @param secondaryCauses    optional additional causes to attach to the exception
         * @since 1.7
         */
        public MultiChannelFIFOQueueException(String message,
                                              Throwable cause,
                                              boolean enableSuppression,
                                              boolean writableStackTrace, Throwable... secondaryCauses)
        {
            super(message, cause, enableSuppression, writableStackTrace, secondaryCauses);
        }
    }
}
