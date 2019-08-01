package com.ridigid.oss.common.security.realm;

import com.ridgid.oss.common.cache.Expirable;

import java.net.InetAddress;
import java.util.Objects;

/**
 * @param <RIDT> Realm ID Type
 * @param <IDT>  Identifier Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface RealmAuthentication<RIDT, IDT, ATT>
        extends Expirable {

    RIDT getRealmId();

    IDT getId();

    ATT getAuthenticationToken();

    InetAddress getClientNetworkAddress();

    long getExpiresSystemTimeMillis();

    long getExtensionTimeMillis();

    default boolean isAuthenticated(ATT authenticationToken,
                                    InetAddress clientNetworkAddress) {
        return !isExpired()
                && Objects.nonNull(authenticationToken)
                && Objects.equals(this.getAuthenticationToken(), authenticationToken)
                && Objects.nonNull(clientNetworkAddress)
                && Objects.equals(this.getClientNetworkAddress(), clientNetworkAddress);
    }

    default boolean isExpired() {
        return getExpiresSystemTimeMillis() < System.currentTimeMillis();
    }

    RealmAuthentication<RIDT, IDT, ATT> extendAuthentication();

}
