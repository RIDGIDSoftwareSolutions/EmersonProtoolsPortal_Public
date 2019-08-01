package com.ridigid.oss.common.security.realm;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @param <RIDT> Realm ID Type
 * @param <IDT>  ID Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface AuthenticationStorage<RIDT, IDT, ATT>
        extends RealmAuthenticatinConstructor<RIDT, IDT, ATT> {

    void store(RealmAuthentication<RIDT, IDT, ATT> auth) throws SQLException;

    Optional<RealmAuthentication<RIDT, IDT, ATT>> retrieve(RIDT realmId,
                                                           IDT id) throws SQLException, UnknownHostException;

    void remove(RIDT realmId, IDT id) throws SQLException;
}
