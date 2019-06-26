package com.ridgid.oss.orm.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@SuppressWarnings("unused")
public class ShortIdPK implements Serializable, Comparable<ShortIdPK> {
    private static final long serialVersionUID = 894985165165165698L;

    @Column(name = "Id", precision = 4)
    private short id = 0;

    public ShortIdPK() {
    }

    public ShortIdPK(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortIdPK)) return false;
        ShortIdPK that = (ShortIdPK) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Short.hashCode(id);
    }

    @Override
    public String toString() {
        return "ShortIdPKImpl{" +
                "id=" + id +
                '}';
    }

    @Override
    public int compareTo(ShortIdPK o) {
        return id - o.id;
    }
}
