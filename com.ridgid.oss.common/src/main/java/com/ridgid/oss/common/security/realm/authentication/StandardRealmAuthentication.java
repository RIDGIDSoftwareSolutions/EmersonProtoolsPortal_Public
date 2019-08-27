package com.ridgid.oss.common.security.realm.authentication;

import java.net.InetAddress;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public final class StandardRealmAuthentication<RIDT, IDT, ATT>
    implements RealmAuthentication<RIDT, IDT, ATT>
{
    private final long        extensionTimeMillis;
    private final RIDT        realmId;
    private final IDT         id;
    private final ATT         authenticationToken;
    private final InetAddress clientNetworkAddress;

    private long    expiresSystemTimeMillis;
    private boolean needsPersisted;

    public StandardRealmAuthentication(long expiresSystemTimeMillis,
                                       long extensionMillis,
                                       RIDT realmId,
                                       IDT id,
                                       ATT authenticationToken,
                                       InetAddress clientNetworkAddress)
    {
        this.extensionTimeMillis  = extensionMillis;
        this.realmId              = realmId;
        this.id                   = id;
        this.authenticationToken  = authenticationToken;
        this.clientNetworkAddress = clientNetworkAddress;

        this.expiresSystemTimeMillis = expiresSystemTimeMillis;
        this.needsPersisted          = false;
    }

    @Override
    public final RIDT getRealmId() {
        return realmId;
    }

    @Override
    public final IDT getId() {
        return id;
    }

    @Override
    public final ATT getAuthenticationToken() {
        return authenticationToken;
    }

    @Override
    public final InetAddress getClientNetworkAddress() {
        return clientNetworkAddress;
    }

    @Override
    public final long getExpiresSystemTimeMillis() {
        return expiresSystemTimeMillis;
    }

    @Override
    public final long getExtensionTimeMillis() {
        return extensionTimeMillis;
    }

    @Override
    public RealmAuthentication<RIDT, IDT, ATT> extendAuthentication() {
        needsPersisted          = true;
        expiresSystemTimeMillis = System.currentTimeMillis() + extensionTimeMillis;
        return this;
    }

    @Override
    public boolean needsPersisted() { return needsPersisted; }

    @Override
    public void persisted() {
        needsPersisted = false;
    }
}
