package com.ridgid.oss.common.security.realm;

import com.ridgid.oss.common.callback.TriHandler;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class NullRealmListener<RIDT, IDT, ST, ATT> implements RealmListener<RIDT, IDT, ST, ATT>
{
    @Override
    public boolean register(TriHandler<RIDT, IDT, ATT, Boolean> eventHandler) {
        return true;
    }

    @Override
    public boolean unregister(TriHandler<RIDT, IDT, ATT, Boolean> eventHandler) {
        return true;
    }
}
