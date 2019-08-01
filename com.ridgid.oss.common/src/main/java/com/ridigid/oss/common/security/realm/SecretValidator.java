package com.ridigid.oss.common.security.realm;

import java.util.Optional;

/**
 * @param <RIDT> Realm ID Type
 * @param <IDT>  Id Type
 * @param <ST>   Secret Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface SecretValidator<RIDT, IDT, ST, ATT> {
    Optional<ATT> authenticate(RIDT realmId, IDT userId, ST userSecret);
}
