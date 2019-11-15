package com.ridgid.oss.message.bus;

/**
 * Defines the delivery requirements for a Topic
 */
public enum DeliveryRequirement
{
    /**
     * Requires that one, and only one, of possibly multiple receivers subscribed to the topic receive the message.
     * If this topic supports multiple subscribers, then only one of the subscribed receivers should have a
     * message sent to the topic delivered.
     */
    EXACTLY_ONE,

    /**
     * Requires that at least one of the possible multiple receivers subscribed to the topic receivers the message.
     * If there are multiple subscribers, the implementation is free to consider the message successfully delivered
     * as soon as at least one of the subscribers receives the message; however, the implementation is free to
     * also deliver the message to more than 1, or even all of the subscribers.
     */
    AT_LEAST_ONE,

    /**
     * Requires that all the subscribers to the topic receive a message sent to the topic before the implementation
     * may consider the message successfully delivered.
     */
    ALL
}
