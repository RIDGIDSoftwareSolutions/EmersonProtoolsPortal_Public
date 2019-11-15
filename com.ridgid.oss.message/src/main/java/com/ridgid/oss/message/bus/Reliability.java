package com.ridgid.oss.message.bus;

/**
 * Indicates the reliability requirements for delivery for the Topic
 */
public enum Reliability
{
    /**
     * All sent messages are guaranteed to be delivered according to the DeliveryRequirement. Undelivered messages
     * persist across restarts (and crashes) and are guaranteed to not be lost without being delivered.
     */
    DURABLE_GUARANTEED,

    /**
     * All sent messages are guaranteed to be delivered unless the system crashes or is restarted before the message
     * is delivered.
     */
    NONDURABLE_GUARANTEED,

    /**
     * Messages on the topic are not guaranteed to be delivered. The implementation should make a "Best Effort" to deliver
     * the messages according to the DeliveryRequirement for the topic; however, the implementation is free to discard
     * undeliverable messages by whatever policy it chooses and also is free to drop messages if volume is too high or
     * the system is too busy. How exactly and when messages are discarded is up to policy imposed by the implementation.
     * The implementation should try to honor the constraints set in the ReliabilityRequirement for the message, but,
     * is not required to do so.
     */
    UNRELIABLE
}
