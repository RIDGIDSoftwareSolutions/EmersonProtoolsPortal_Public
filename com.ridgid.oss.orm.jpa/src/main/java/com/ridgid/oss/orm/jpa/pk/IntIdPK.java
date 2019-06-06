package com.ridgid.oss.orm.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IntIdPK implements Serializable, Comparable<IntIdPK> {

    private static final long serialVersionUID = 894985165165165698L;

    @Column(name = "Id", precision = 8)
    private int id = 0;

    public IntIdPK() {
    }

    public IntIdPK(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntIdPK)) return false;
        IntIdPK that = (IntIdPK) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IntIdPKImpl{" +
                "id=" + id +
                '}';
    }

    @Override
    public int compareTo(IntIdPK o) {
        return id - o.id;
    }
}
