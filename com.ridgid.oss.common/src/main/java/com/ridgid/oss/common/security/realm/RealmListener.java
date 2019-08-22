package com.ridgid.oss.common.security.realm;

import com.ridgid.oss.common.callback.BiHandler;

/**
 * @param <RIDT> Realm ID Type
 * @param <IDT>  ID Type
 * @param <ST>   Secret Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface RealmListener<RIDT, IDT, ST, ATT>
{
    boolean register(BiHandler<RIDT, IDT, Boolean> eventHandler);
    boolean unregister(BiHandler<RIDT, IDT, Boolean> eventHandler);
}
