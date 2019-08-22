package com.ridgid.oss.common.security.realm.authentication.storage;

import com.ridgid.oss.common.security.realm.authentication.AuthenticationStorage;
import com.ridgid.oss.common.security.realm.authentication.RealmAuthentication;
import com.ridgid.oss.common.security.realm.authentication.StandardRealmAuthentication;

import java.net.InetAddress;
import java.util.function.Function;

@SuppressWarnings({"unused", "SpellCheckingInspection", "WeakerAccess"})
public abstract class StandardBaseAuthenticationStorage<RIDT, IDT, ATT>
    implements
    AuthenticationStorage<RIDT, IDT, ATT>
{
    private final Function<RIDT, Long> expirationPolicy;
    private final Function<RIDT, Long> extensionPolicy;

    public StandardBaseAuthenticationStorage(Function<RIDT, Long> expirationPolicy,
                                             Function<RIDT, Long> extensionPolicy)
    {
        this.expirationPolicy = expirationPolicy;
        this.extensionPolicy  = extensionPolicy;
    }

    protected Function<RIDT, Long> getExpirationPolicy() {
        return expirationPolicy;
    }

    protected Function<RIDT, Long> getExtensionPolicy() {
        return extensionPolicy;
    }

    @Override
    public RealmAuthentication<RIDT, IDT, ATT> construct(RIDT realmId,
                                                         IDT id,
                                                         ATT att,
                                                         InetAddress clientNetworkAddress)
    {
        return construct
            (
                extensionPolicy.apply(realmId),
                expirationPolicy.apply(realmId),
                realmId,
                id,
                att,
                clientNetworkAddress
            );
    }

    @Override
    public RealmAuthentication<RIDT, IDT, ATT> construct(long extensionTimeMillis,
                                                         long expiresSystemTimeMillis,
                                                         RIDT realmId,
                                                         IDT id,
                                                         ATT att,
                                                         InetAddress clientNetworkAddress)
    {
        return new StandardRealmAuthentication<>
            (
                extensionTimeMillis,
                expiresSystemTimeMillis,
                realmId,
                id,
                att,
                clientNetworkAddress
            );
    }
}
