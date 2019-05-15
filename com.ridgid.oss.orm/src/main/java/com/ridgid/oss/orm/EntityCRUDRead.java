package com.ridgid.oss.orm;

import java.util.List;

public interface EntityCRUDRead<T extends PrimaryKeyedEntity<PKT>, PKT> extends EntityCRUD<T,PKT> {
    T find(PKT pk);

    List<T> findAll(int offset, int limit);
}
