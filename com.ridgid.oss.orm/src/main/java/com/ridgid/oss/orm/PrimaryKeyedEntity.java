package com.ridgid.oss.orm;

import java.io.Serializable;

public interface PrimaryKeyedEntity<PKT> extends Serializable {
    PKT getPk();
    void setPk(PKT pk);
}
