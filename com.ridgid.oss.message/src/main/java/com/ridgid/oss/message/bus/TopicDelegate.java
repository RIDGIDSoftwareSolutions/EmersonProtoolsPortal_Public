package com.ridgid.oss.message.bus;

import com.ridgid.oss.message.bus.spi.MessageBus.MessageBusException;
import com.ridgid.oss.message.bus.spi.TopicReceiver;
import com.ridgid.oss.message.bus.spi.TopicSender;
import com.ridgid.oss.message.bus.spi.TopicSender.TopicSenderException;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Delegagte for {@code TopicEnum} enumerations.
 *
 * @param <TE> Enumeration Type of the topic
 * @param <TG> Enumeration Type of the topic group
 */
@SuppressWarnings({"ClassHasNoToStringMethod", "unused", "PublicMethodNotExposedInInterface"})
public class TopicDelegate<TE extends Enum<TE> & TopicEnum<TE>, TG extends Enum<TG> & TopicGroupEnum<TG>>
    implements Topic,
               AutoCloseable,
               Serializable
{
    private static final long serialVersionUID = -3622572293182923594L;

    private static final Object globalResetLock = new Object();
    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final        Object resetLock       = new Object();

    private final TE                  topic;
    private final TG                  group;
    private final String              topicName;
    private final ReceiverCardinality receiverCardinality;
    private final SenderCardinality   senderCardinality;
    private final DeliveryRequirement deliveryRequirement;

    private TopicReceiver<TE> receiver;
    private TopicSender<TE>   sender;

    /**
     * Construct a {@code TopicEnum<TE,TG>} delegate
     *
     * @param topic {@code TopicEnum} delegating to this
     * @param group for topic
     */
    public TopicDelegate(TE topic,
                         TG group)
    {
        this(topic,
             group,
             ReceiverCardinality.MANY,
             SenderCardinality.MANY,
             DeliveryRequirement.ALL);
    }

    /**
     * Constuct a {@code TopicEnum<TE,TG>} delegate
     *
     * @param topic               delegated
     * @param group               for topic
     * @param receiverCardinality required
     * @param senderCardinality   required
     * @param deliveryRequirement required
     */
    @SuppressWarnings({
                          "StringConcatenation",
                          "SingleCharacterStringConcatenation",
                          "WeakerAccess"
                      })
    public TopicDelegate(TE topic,
                         TG group,
                         ReceiverCardinality receiverCardinality,
                         SenderCardinality senderCardinality,
                         DeliveryRequirement deliveryRequirement)
    {
        this.topic               = topic;
        this.group               = group;
        this.receiverCardinality = receiverCardinality;
        this.senderCardinality   = senderCardinality;
        this.deliveryRequirement = deliveryRequirement;
        topicName                = group.name() + "-" + topic.name();
    }

    /**
     * Reset the MessageBus for all the CacheTopic topics.
     * <p>
     * After a call to this method, a subsequent call to send or clear, clearKey, or subscribe on a CacheTopic will retrieve a new MessageBus for that Topic
     *
     * @param <TE>      enum to reset the message buses on
     * @param enumClass class of the enum type
     */
    @SuppressWarnings("SynchronizationOnStaticField")
    public static <TE extends Enum<TE> & TopicEnum<TE>> void resetAllMessageBuses(Class<TE> enumClass) {
        synchronized ( globalResetLock ) {
            for ( TE topic : enumClass.getEnumConstants() )
                topic.resetMessageBus();
        }
    }

    /**
     * Reset the MessageBus for this Topic
     * <p>
     * After a call to this method, a subsequent call to clear, clearKey, or subscribe on this CacheTopic will retrieve a new MessageBus.
     */
    @Override
    @SuppressWarnings("AssignmentToNull")
    public final void resetMessageBus() {
        synchronized ( resetLock ) {
            close(receiver);
            close(sender);
            receiver = null;
            sender   = null;
        }
    }

    /**
     * Send a message to the message bus for this Topic
     *
     * @param message to send to the topic
     * @throws MessageBusException  if problem obtaining a message bus
     * @throws TopicSenderException if problem sending on the message bus
     */
    @SuppressWarnings({"PublicMethodNotExposedInInterface", "MethodWithTooExceptionsDeclared"})
    public final void send(Serializable message)
        throws MessageBusException,
               TopicSenderException
    {
        currentSender().send(message);
    }

    /**
     * Subscribe to the Topic on the available default MessageBus and listen for messages from the Topic.
     *
     * @param <MT>        message type
     * @param messageType class of message type
     * @param consumer    to accept messages from the bus
     * @return listener that can be closed when no ready to no longer listen for messages
     * @throws MessageBusException if unable to obtain receiver or listen for messages
     */
    public <MT extends Serializable>
    TopicReceiverListener<TE, ? super MT> listen(Class<? extends MT> messageType,
                                                 BiConsumer<? super TE, ? super MT> consumer)
        throws MessageBusException
    {
        return currentReceiver().listen(messageType,
                                        consumer);
    }

    /**
     * @return name of the tooic group, if any, for this topic
     */
    @Override
    public final Optional<String> getGroupName() {
        return Optional.of(group.name());
    }

    /**
     * @return topic name of the topic
     */
    @Override
    public final String getTopicName() {
        return topicName;
    }

    /**
     * @return stream of Class entries for Serializable classes that represent the Message Types permitted to
     * send/receive on this Topic
     */
    @Override
    public final Stream<Class<? extends Serializable>> getMessageTypes() {
        return topic.getMessageTypes();
    }

    /**
     * @return indicates the number of simultaneous receivers permitted for the topic
     */
    @Override
    public final ReceiverCardinality getReceiverCardinality() {
        return receiverCardinality;
    }

    /**
     * @return indicates the number of simultaneous senders permitted for the topic
     */
    @Override
    public final SenderCardinality getSenderCardinality() {
        return senderCardinality;
    }

    /**
     * @return requirements for delivery of messages for the topic (how many of possible multiple receivers must
     * receive the message to consider it delivered)
     */
    @Override
    public final DeliveryRequirement getDeliveryRequirement() {
        return deliveryRequirement;
    }

    private TopicSender<TE> currentSender()
        throws MessageBusException
    {
        TopicSender<TE> currentSender;
        synchronized ( resetLock ) {
            if ( sender == null ) sender = MessageBusService.instance()
                                                            .defaultProvider()
                                                            .create(topic);
            currentSender = sender;
        }
        return currentSender;
    }

    private TopicReceiver<TE> currentReceiver()
        throws MessageBusException
    {
        TopicReceiver<TE> currentReceiver;
        synchronized ( resetLock ) {
            if ( receiver == null ) receiver = MessageBusService.instance()
                                                                .defaultProvider()
                                                                .subscribe(topic);
            currentReceiver = receiver;
        }
        return currentReceiver;
    }

    private static void close(AutoCloseable closeable) {
        if ( closeable != null ) try {closeable.close();} catch ( Exception ignore ) {}
    }

    @Override
    public void close() {
        resetMessageBus();
    }

}
