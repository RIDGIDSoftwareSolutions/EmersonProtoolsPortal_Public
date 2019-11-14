package com.ridgid.oss.message.bus;

import java.time.temporal.ChronoUnit;

@SuppressWarnings({"JavaDoc", "InterfaceNeverImplemented"})
public interface ReliabilityRequirement
{
    default Reliability getReliability() {
        return Reliability.NONDURABLE_GUARANTEED;
    }

    @SuppressWarnings("NewMethodNamingConvention")
    default int getMaximumFailedDeliveryCountPerTimeInterval() {
        return 1;
    }

    default ChronoUnit getReliablityInterval() {
        return ChronoUnit.HOURS;
    }
}
