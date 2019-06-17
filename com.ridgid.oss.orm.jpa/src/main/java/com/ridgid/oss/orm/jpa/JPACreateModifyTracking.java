package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.orm.CreateModifyTracking;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 *
 */
@SuppressWarnings("unused")
@Embeddable
public class JPACreateModifyTracking
        implements CreateModifyTracking {

    @Column(name = "Created", updatable = false, nullable = false)
    private LocalDateTime created;

    @Column(name = "CreatedBy", length = 64, updatable = false, nullable = false)
    private String createdBy;

    @Column(name = "Modified", nullable = false)
    private LocalDateTime modified;

    @Column(name = "ModifiedBy", length = 64, nullable = false)
    private String modifiedBy;

    @Override
    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public LocalDateTime getModified() {
        return modified;
    }

    @Override
    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    @Override
    public String getModifiedBy() {
        return modifiedBy;
    }

    @Override
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
