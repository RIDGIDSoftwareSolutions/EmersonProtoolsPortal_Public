package com.ridgid.oss.message.bus;

/**
 * Interface for an Enum of Topics that provides meta-data about the topic for use by the MessageBus implementation
 *
 * @param <E> enum type that implements this interface
 */
@SuppressWarnings({"InterfaceNeverImplemented", "MarkerInterface"})
public interface TopicEnum<E extends Enum<E> & TopicEnum<E>> extends Topic
{
}
