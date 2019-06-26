package com.ridgid.oss.orm.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@SuppressWarnings("unused")
public class ByteIdPK implements Serializable, Comparable<ByteIdPK> {
    private static final long serialVersionUID = 894985165165165698L;

    @Column(name = "Id", precision = 2)
    private byte id = 0;

    public ByteIdPK() {
    }

    public ByteIdPK(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ByteIdPK)) return false;
        ByteIdPK that = (ByteIdPK) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(id);
    }

    @Override
    public String toString() {
        return "ByteIdPKImpl{" +
                "id=" + id +
                '}';
    }

    @Override
    public int compareTo(ByteIdPK o) {
        return id - o.id;
    }
}
