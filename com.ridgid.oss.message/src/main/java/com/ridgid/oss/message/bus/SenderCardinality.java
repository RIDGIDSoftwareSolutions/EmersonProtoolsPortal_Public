package com.ridgid.oss.message.bus;

/**
 * Number of senders permitted for the topic
 */
public enum SenderCardinality
{
    /**
     * Topic may ony have one sender. The implementation must deny subsequent senders from registering on the topic.
     */
    ONE,

    /**
     * Topic is permitted to have more than one sender.
     */
    MANY
}
