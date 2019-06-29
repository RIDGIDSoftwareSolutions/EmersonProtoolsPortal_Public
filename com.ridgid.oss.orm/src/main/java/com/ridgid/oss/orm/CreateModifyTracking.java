package com.ridgid.oss.orm;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public interface CreateModifyTracking extends Comparable<CreateModifyTracking> {
    LocalDateTime getCreated();

    void setCreated(LocalDateTime created);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    LocalDateTime getModified();

    void setModified(LocalDateTime modified);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}
