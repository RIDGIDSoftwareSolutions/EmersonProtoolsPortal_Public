package com.ridgid.oss.message.bus;

/**
 * Message Wire Formats
 */
public enum WireFormat
{
    /**
     * JSON Wire-Formatted Message
     */
    JSON,

    /**
     * Java Object Serialization Formatted Message
     */
    JAVA_SERIALIZED,

    /**
     * XML Formatted Message
     */
    XML,

    /**
     * SOAP Formatted Message
     */
    SOAP
}
