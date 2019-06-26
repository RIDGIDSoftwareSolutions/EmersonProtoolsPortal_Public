package com.ridgid.oss.orm.jpa.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@SuppressWarnings("unused")
public class StringIdPK implements Serializable, Comparable<StringIdPK> {
    private static final long serialVersionUID = 894985165165165698L;

    @Column(name = "Id")
    private String id;

    public StringIdPK() {
    }

    public StringIdPK(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StringIdPK)) return false;
        StringIdPK that = (StringIdPK) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "StringIdPKImpl{" +
                "id=" + id +
                '}';
    }

    @Override
    public int compareTo(StringIdPK o) {
        return Objects.compare(this.id, o.id, String::compareTo);
    }
}
