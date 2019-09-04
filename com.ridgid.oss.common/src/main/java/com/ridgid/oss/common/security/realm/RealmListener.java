package com.ridgid.oss.common.security.realm;

import com.ridgid.oss.common.callback.TriHandler;

/**
 * @param <RIDT> Realm ID Type
 * @param <IDT>  ID Type
 * @param <ST>   Secret Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface RealmListener<RIDT, IDT, ST, ATT>
{
    boolean register(TriHandler<RIDT, IDT, ATT, Boolean> eventHandler);
    boolean unregister(TriHandler<RIDT, IDT, ATT, Boolean> eventHandler);
}
