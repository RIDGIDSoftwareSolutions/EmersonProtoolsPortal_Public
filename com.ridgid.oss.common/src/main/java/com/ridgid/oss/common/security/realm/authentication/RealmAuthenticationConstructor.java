package com.ridgid.oss.common.security.realm.authentication;

import java.net.InetAddress;

@SuppressWarnings("SpellCheckingInspection")
public interface RealmAuthenticationConstructor<RIDT, IDT, ATT>
{
    long DEFAULT_EXPIRES_SECONDS = 1800;

    default RealmAuthentication<RIDT, IDT, ATT> construct(RIDT realmId,
                                                          IDT id,
                                                          ATT att,
                                                          InetAddress clientNetworkAddress)
    {
        return construct
            (
                DEFAULT_EXPIRES_SECONDS * 1000,
                System.currentTimeMillis() + DEFAULT_EXPIRES_SECONDS * 1000,
                realmId,
                id,
                att,
                clientNetworkAddress
            );
    }

    RealmAuthentication<RIDT, IDT, ATT> construct(long extensionTimeMillis,
                                                  long expiresSystemTimeMillis,
                                                  RIDT realmId,
                                                  IDT id,
                                                  ATT att,
                                                  InetAddress clientNetworkAddress);
}
