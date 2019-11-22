package com.ridgid.oss.message.bus;

/**
 * Topic Group enumeration.
 *
 * @param <TGE> enum type implementing this interface
 */
@SuppressWarnings({"InterfaceNeverImplemented", "MarkerInterface", "WeakerAccess"})
public interface TopicGroupEnum<TGE extends Enum<TGE> & TopicGroupEnum<TGE>>
{
}
