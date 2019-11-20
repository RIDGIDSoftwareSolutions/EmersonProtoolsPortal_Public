package com.ridgid.oss.message.bus;

import java.time.temporal.ChronoUnit;

/**
 * Defines the reliability for delivery of messages the Topic requires.
 */
public interface ReliabilityRequirement
{
    /**
     * @return indicates the reliability level required for deliver of messages
     */
    default Reliability getReliability() {
        return Reliability.NONDURABLE_GUARANTEED;
    }

    /**
     * @return how many failed delivery attempts the implementation should allow within a time interval. The implementation should make a "Best Effort" to ensure no more than this number of messages are discarded within the configured interval @see #getReliablityInterval()
     */
    @SuppressWarnings("NewMethodNamingConvention")
    default int getMaximumFailedDeliveryCountPerTimeInterval() {
        return 1;
    }

    /**
     * @return time interval to maintain a running total of failed delivery attempts
     */
    default ChronoUnit getReliablityInterval() {
        return ChronoUnit.HOURS;
    }
}
