package com.ridgid.oss.orm.entity;

import com.ridgid.oss.common.helper.CopyableModel;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public interface CreateModifyTracking extends CopyableModel {
    LocalDateTime getCreated();

    void setCreated(LocalDateTime created);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    LocalDateTime getModified();

    void setModified(LocalDateTime modified);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}
