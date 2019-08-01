package com.ridigid.oss.common.security.realm;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class NullRealmNotifier<RIDT, IDT, ST, ATT> implements RealmNotifier<RIDT, IDT, ST, ATT> {
    @Override
    public void notify(RIDT realmId, IDT id) {
        // do nothing
    }
}
