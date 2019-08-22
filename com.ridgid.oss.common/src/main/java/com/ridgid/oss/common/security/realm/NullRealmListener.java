package com.ridgid.oss.common.security.realm;

import com.ridgid.oss.common.callback.BiHandler;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class NullRealmListener<RIDT, IDT, ST, ATT> implements RealmListener<RIDT, IDT, ST, ATT>
{
    @Override
    public boolean register(BiHandler<RIDT, IDT, Boolean> eventHandler) {
        return true;
    }

    @Override
    public boolean unregister(BiHandler<RIDT, IDT, Boolean> eventHandler) {
        return true;
    }
}
