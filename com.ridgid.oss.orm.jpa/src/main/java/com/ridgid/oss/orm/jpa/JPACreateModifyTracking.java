package com.ridgid.oss.orm.jpa;

import com.ridgid.oss.common.helper.CopyableModel;
import com.ridgid.oss.orm.CreateModifyTracking;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 */
@SuppressWarnings("unused")
@Embeddable
public class JPACreateModifyTracking
        implements CreateModifyTracking, Comparable<CreateModifyTracking>, CopyableModel {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JPACreateModifyTracking)) return false;
        JPACreateModifyTracking that = (JPACreateModifyTracking) o;
        return created.equals(that.created) &&
                createdBy.equals(that.createdBy) &&
                modified.equals(that.modified) &&
                modifiedBy.equals(that.modifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, createdBy, modified, modifiedBy);
    }

    @Override
    public String toString() {
        return "JPACreateModifyTracking{" +
                "created=" + created +
                ", createdBy='" + createdBy + '\'' +
                ", modified=" + modified +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }

    @Override
    public int compareTo(CreateModifyTracking o) {
        int rv = modified.compareTo(o.getModified());
        if (rv != 0) return rv;
        rv = created.compareTo(o.getCreated());
        if (rv != 0) return rv;
        rv = modifiedBy.compareTo(o.getModifiedBy());
        if (rv != 0) return rv;
        return createdBy.compareTo(o.getCreatedBy());
    }
}
