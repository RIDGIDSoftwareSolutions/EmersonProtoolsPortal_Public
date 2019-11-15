package com.ridgid.oss.message.bus;

/**
 * Describes the Cardinality of receivers the topic permits.
 */
public enum ReceiverCardinality
{
    /**
     * Only one subscribed receivers is permitted at a time to this topic. Additional subscription requests
     * to the topic must be denied by the Message Bus once a single receiver is subscribed.
     */
    ONE,

    /**
     * Many subscribers to the topic are permitted.
     */
    MANY
}
