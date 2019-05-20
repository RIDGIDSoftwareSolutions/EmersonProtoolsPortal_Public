package com.ridgid.oss.orm;

import java.util.List;

public interface EntityCRUDRead<ET extends PrimaryKeyedEntity<PKT>, PKT>
        extends EntityCRUD<ET, PKT> {
    ET find(PKT pk);

    List<ET> findAll(int offset, int limit);
}
