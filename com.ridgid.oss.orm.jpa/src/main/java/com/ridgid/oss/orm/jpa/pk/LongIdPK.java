package com.ridgid.oss.orm.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@SuppressWarnings("unused")
public class LongIdPK implements Serializable, Comparable<LongIdPK> {
    private static final long serialVersionUID = 894985165165165698L;

    @Column(name = "Id", precision = 16)
    private long id = 0;

    public LongIdPK() {
    }

    public LongIdPK(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongIdPK)) return false;
        LongIdPK that = (LongIdPK) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "LongIdPKImpl{" +
                "id=" + id +
                '}';
    }

    @Override
    public int compareTo(LongIdPK o) {
        long cmp = id - o.id;
        return cmp < 0
                ? -1
                : cmp > 0
                ? 1
                : 0;
    }
}
