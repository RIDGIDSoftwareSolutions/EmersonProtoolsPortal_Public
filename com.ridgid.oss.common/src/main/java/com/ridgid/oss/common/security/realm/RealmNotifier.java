package com.ridgid.oss.common.security.realm;

/**
 * @param <RIDT> Realm ID Type
 * @param <IDT>  ID Type
 * @param <ST>   Secret Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface RealmNotifier<RIDT, IDT, ST, ATT>
{
    void notify(RIDT realmId, IDT id);
}
