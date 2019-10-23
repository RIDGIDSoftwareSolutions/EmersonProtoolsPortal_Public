package com.ridgid.oss.orm.jpa.entity;

import com.ridgid.oss.orm.entity.CreateModifyTracking;
import org.hibernate.annotations.LazyGroup;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import java.io.Serializable;
import java.time.LocalDateTime;

import static java.util.Objects.hash;

/**
 *
 */
@SuppressWarnings("unused")
@Embeddable
public class JPACreateModifyTracking
    implements CreateModifyTracking,
               Comparable<CreateModifyTracking>,
               Serializable
{
    private static final long serialVersionUID = -5500843342793078255L;

    @Column(name = "Created", updatable = false, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup("CreateModifyTracking")
    private LocalDateTime created;

    @Column(name = "CreatedBy", length = 64, updatable = false, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup("CreateModifyTracking")
    private String createdBy;

    @Column(name = "Modified", nullable = false)
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup("CreateModifyTracking")
    private LocalDateTime modified;

    @Column(name = "ModifiedBy", length = 64, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup("CreateModifyTracking")
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

    @SuppressWarnings({
                          "LocalVariableOfConcreteClass",
                          "NonFinalFieldReferenceInEquals",
                          "CallToSuspiciousStringMethod"
                      })
    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof JPACreateModifyTracking) ) return false;
        JPACreateModifyTracking other = (JPACreateModifyTracking) o;
        return created.equals(other.created) &&
               createdBy.equals(other.createdBy) &&
               modified.equals(other.modified) &&
               modifiedBy.equals(other.modifiedBy);
    }

    @SuppressWarnings({"ObjectInstantiationInEqualsHashCode", "NonFinalFieldReferencedInHashCode"})
    @Override
    public int hashCode() {
        return hash(created, createdBy, modified, modifiedBy);
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

    @SuppressWarnings({"CompareToUsesNonFinalVariable", "ReuseOfLocalVariable", "CallToSuspiciousStringMethod"})
    @Override
    public int compareTo(CreateModifyTracking o) {
        int rv = modified.compareTo(o.getModified());
        if ( rv != 0 ) return rv;
        rv = created.compareTo(o.getCreated());
        if ( rv != 0 ) return rv;
        rv = modifiedBy.compareTo(o.getModifiedBy());
        if ( rv != 0 ) return rv;
        return createdBy.compareTo(o.getCreatedBy());
    }
}
