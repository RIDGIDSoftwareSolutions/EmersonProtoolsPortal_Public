package com.ridigid.oss.common.security;

import com.ridgid.oss.common.cache.Cache;
import com.ridgid.oss.common.cache.InMemoryKVExpirableLRUCache;

import java.net.InetAddress;

/**
 * Realm Authentication Manager
 *
 * @param <RIDT> Realm ID Type
 * @param <PIDT> Peer ID Type
 * @param <PST>  Peer Secret Type
 * @param <PATT> Peer Authentication Token Type
 * @param <UIDT> User ID Type
 * @param <UST>  User Secret Type
 * @param <UATT> User Authentication Token Type
 */
@SuppressWarnings({"SpellCheckingInspection", "FieldCanBeLocal", "unused"})
public abstract class RealmManager<RIDT, PIDT, PST, PATT, UIDT, UST, UATT> {

    private final RealmListener<RIDT, PIDT, UIDT> realmListener;
    private final RealmNotifier<RIDT, PIDT, UIDT> realmNotifier;
    private final Cache<PIDT, RealmAuthentication<PIDT, PATT>> peerAuthenticationCache;
    private final Cache<UIDT, RealmAuthentication<UIDT, UATT>> userAuthenticationCache;
    private final SecretValidator<RIDT, PIDT, PST> peerSecretValidator;
    private final SecretValidator<RIDT, UIDT, UST> userSecretValidator;
    private final AuthenticationStorage<RIDT, PIDT, PATT> peerAuthenticationStorage;
    private final AuthenticationStorage<RIDT, UIDT, UATT> userAuthenticationStorage;

    public RealmManager(RealmListener<RIDT, PIDT, UIDT> realmListener,
                        RealmNotifier<RIDT, PIDT, UIDT> realmNotifier,
                        SecretValidator<RIDT, PIDT, PST> peerSecretValidator,
                        AuthenticationStorage<RIDT, PIDT, PATT> peerAuthenticationStorage,
                        short maxPeerCacheSize,
                        SecretValidator<RIDT, UIDT, UST> userSecretValidator,
                        AuthenticationStorage<RIDT, UIDT, UATT> userAuthenticationStorage,
                        short maxUserCacheSize) {
        this.realmListener = realmListener;
        this.realmNotifier = realmNotifier;
        this.peerAuthenticationCache
                = new InMemoryKVExpirableLRUCache<>
                (
                        (short) 300,
                        (short) 10,
                        maxPeerCacheSize,
                        (short) (maxPeerCacheSize - 10)
                );
        this.peerAuthenticationStorage = peerAuthenticationStorage;
        this.userAuthenticationStorage = userAuthenticationStorage;
        this.userAuthenticationCache
                = new InMemoryKVExpirableLRUCache<>
                (
                        (short) 300,
                        (short) 50,
                        maxUserCacheSize,
                        (short) (maxUserCacheSize - 10)
                );
        this.peerSecretValidator = peerSecretValidator;
        this.userSecretValidator = userSecretValidator;
    }

    public UATT authenticateUserToRealm(PIDT realmId,
                                        UIDT userId,
                                        UST userSecret,
                                        InetAddress userClientNetworkAddress) {

    }

    public void removeUserFromRealm(PIDT realmId,
                                    UIDT userId) {

    }

    public boolean isUserAuthenticatedInRealm(PIDT realmId,
                                              UIDT userId,
                                              UATT userAuthenticationToken,
                                              InetAddress userClientNetworkAddress) {

    }

    public PATT authenticatePeerToRealm(PIDT realmId,
                                        PIDT peerId,
                                        PST peerSecret,
                                        InetAddress peerClientNetworkAddress) {

    }

    public void removePeerFromRealm(PIDT realmId,
                                    PIDT peerId) {

    }

    public boolean isPeerAuthenticatedInRealm(PIDT realmId,
                                              PIDT peerId,
                                              PATT peerAuthenticationToken,
                                              InetAddress peerClientNetworkAddress) {

    }

}
