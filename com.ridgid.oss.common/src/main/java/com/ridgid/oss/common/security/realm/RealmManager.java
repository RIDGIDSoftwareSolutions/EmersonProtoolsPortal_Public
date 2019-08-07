package com.ridgid.oss.common.security.realm;

import com.ridgid.oss.common.cache.Cache;
import com.ridgid.oss.common.cache.Expirable;
import com.ridgid.oss.common.cache.InMemoryKVExpirableLRUCache;
import com.ridgid.oss.common.security.realm.authentication.AuthenticationStorage;
import com.ridgid.oss.common.security.realm.authentication.RealmAuthentication;
import com.ridgid.oss.common.security.realm.authentication.RealmAuthenticationException;
import com.ridgid.oss.common.security.realm.authentication.secret.SecretValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Realm Authentication Manager
 *
 * @param <RIDT> Realm ID Type
 * @param <IDT>  ID Type
 * @param <ST>   Secret Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"SpellCheckingInspection", "FieldCanBeLocal", "unused"})
public class RealmManager<RIDT, IDT, ST, ATT> {

    private static final short DEFAULT_MIN_CACHE_SIZE = 50;
    private static final short CACHE_TIMEOUT_CHECK_INTERVAL_SECONDS = 300;

    private final RealmListener<RIDT, IDT, ST, ATT> listener;
    private final RealmNotifier<RIDT, IDT, ST, ATT> notifier;
    private final ConcurrentMap<RIDT, Cache<IDT, RealmAuthentication<RIDT, IDT, ATT>>> authenticationCache
            = new ConcurrentHashMap<>();
    private final Cache<InetAddress, RequestCount> requestCounts
            = new InMemoryKVExpirableLRUCache<>
            (
                    (short) 300,
                    (short) 100,
                    (short) 1000,
                    (short) 900
            );
    private final SecretValidator<RIDT, IDT, ST, ATT> secretValidator;
    private final AuthenticationStorage<RIDT, IDT, ATT> storage;
    private final RealmLogger<RIDT, IDT> logger;
    private final short maxCacheSize;

    protected RealmManager(RealmListener<RIDT, IDT, ST, ATT> listener,
                         RealmNotifier<RIDT, IDT, ST, ATT> notifier,
                         SecretValidator<RIDT, IDT, ST, ATT> secretValidator,
                         AuthenticationStorage<RIDT, IDT, ATT> storage,
                         RealmLogger<RIDT, IDT> logger,
                         short maxCacheSize) {
        this.listener = listener;
        this.notifier = notifier;
        this.storage = storage;
        this.secretValidator = secretValidator;
        this.logger = logger;
        this.maxCacheSize = computeMaxCacheSize(DEFAULT_MIN_CACHE_SIZE, maxCacheSize);
        listener.register(this::handleListenerEvent);
    }

    @SuppressWarnings("SameParameterValue")
    private static short computeMaxCacheSize(short min, short requestedMax) {
        return (short) Math.max(min * 2, Math.max(1, requestedMax));
    }

    public Optional<RealmAuthentication<RIDT, IDT, ATT>> authenticate(RIDT realmId,
                                                                      IDT id,
                                                                      ST secret,
                                                                      InetAddress clientNetworkAddress)
            throws BlacklistedException {
        checkBlacklisted(clientNetworkAddress);
        Optional<RealmAuthentication<RIDT, IDT, ATT>> auth
                = secretValidator
                .authenticate(realmId, id, secret)
                .map(att -> buildAuthentication
                        (
                                realmId,
                                id,
                                att,
                                clientNetworkAddress
                        )
                )
                .map(this::cacheStoreAndNotify);
        if (!auth.isPresent())
            checkFailedAuthVisitOk(clientNetworkAddress);
        return auth;
    }

    public boolean extendAuthentication(RIDT realmId,
                                        IDT id,
                                        InetAddress clientNetworkAddress)
            throws BlacklistedException {

        checkBlacklisted(clientNetworkAddress);
        if (readThrougCache(realmId, id)
                .map(RealmAuthentication::extendAuthentication)
                .map(this::store)
                .map(this::notify)
                .isPresent())
            return true;
        checkFailedAuthVisitOk(clientNetworkAddress);
        return false;
    }

    public void revokeAuthentication(RIDT realmId,
                                     IDT id) {
        removeStored(realmId, id);
        removeCached(realmId, id);
        notify(realmId, id);
    }

    public boolean isAuthenticatedInRealm(RIDT realmId,
                                          IDT id,
                                          ATT authenticationToken,
                                          InetAddress clientNetworkAddress)
            throws BlacklistedException {
        checkBlacklisted(clientNetworkAddress);
        if (readThrougCache(realmId, id)
                .map(auth -> auth.isAuthenticated(authenticationToken, clientNetworkAddress))
                .isPresent())
            return true;
        checkFailedAuthVisitOk(clientNetworkAddress);
        return false;
    }

    private Boolean handleListenerEvent(RIDT realmId, IDT id) {
        try {
            refreshCache(realmId, id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private Optional<RealmAuthentication<RIDT, IDT, ATT>> refreshCache(RIDT realmId, IDT id) {
        ConcurrentMap<String, String> cm;
        return Optional.ofNullable
                (
                        authenticationCache
                                .computeIfAbsent
                                        (
                                                realmId,
                                                key -> makeAuthenticationCache()
                                        )
                                .compute
                                        (
                                                id,
                                                (key, v) -> readStoredUncheckExceptions
                                                        (
                                                                realmId,
                                                                key
                                                        )
                                                        .orElse(null)
                                        )
                );
    }

    private Optional<RealmAuthentication<RIDT, IDT, ATT>> readThrougCache(RIDT realmId, IDT id) {
        return Optional.ofNullable
                (
                        authenticationCache
                                .computeIfAbsent
                                        (
                                                realmId,
                                                key -> makeAuthenticationCache()
                                        )
                                .computeIfAbsent
                                        (
                                                id,
                                                key -> readStoredUncheckExceptions
                                                        (
                                                                realmId,
                                                                key
                                                        )
                                                        .orElse(null)
                                        )
                );
    }

    private Optional<RealmAuthentication<RIDT, IDT, ATT>> readStoredUncheckExceptions(RIDT realmId,
                                                                                      IDT key) {
        try {
            return readStored(realmId, key);
        } catch (SQLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<RealmAuthentication<RIDT, IDT, ATT>> readStored(RIDT realmId,
                                                                     IDT id)
            throws SQLException, UnknownHostException {
        return storage.retrieve(realmId, id);
    }

    private RealmAuthentication<RIDT, IDT, ATT> buildAuthentication(RIDT realmId,
                                                                    IDT id,
                                                                    ATT att,
                                                                    InetAddress clientNetworkAddress) {
        return storage.construct
                (
                        realmId,
                        id,
                        att,
                        clientNetworkAddress
                );
    }

    private RealmAuthentication<RIDT, IDT, ATT> cacheStoreAndNotify(RealmAuthentication<RIDT, IDT, ATT> auth) {
        cache(auth);
        store(auth);
        notify(auth);
        return auth;
    }

    private void cache(RealmAuthentication<RIDT, IDT, ATT> auth) {
        try {
            authenticationCache
                    .computeIfAbsent
                            (
                                    auth.getRealmId(),
                                    k -> makeAuthenticationCache()
                            )
                    .put
                            (
                                    auth.getId(),
                                    auth
                            );
        } catch (Exception ex) {
            logger.error
                    (
                            auth.getRealmId(),
                            auth.getId(),
                            auth.getClientNetworkAddress(),
                            "Unable to cache authentication",
                            ex
                    );
        }
    }

    private RealmAuthentication<RIDT, IDT, ATT> store(RealmAuthentication<RIDT, IDT, ATT> auth) {
        try {
            storage.store(auth);
        } catch (Exception ex) {
            logger.warn
                    (
                            auth.getRealmId(),
                            auth.getId(),
                            auth.getClientNetworkAddress(),
                            "Unable to store authentication",
                            ex
                    );
        }
        return auth;
    }

    private void removeStored(RIDT realmId, IDT id) {
        try {
            storage.remove
                    (
                            realmId,
                            id
                    );
        } catch (Exception ex) {
            logger.warn
                    (
                            realmId,
                            id,
                            "Unable to remove authentication from cache",
                            ex
                    );
        }
    }

    private void removeCached(RIDT realmId, IDT id) {
        try {
            authenticationCache
                    .computeIfAbsent
                            (
                                    realmId,
                                    k -> makeAuthenticationCache()
                            )
                    .remove
                            (
                                    id
                            );
        } catch (Exception ex) {
            logger.warn
                    (
                            realmId,
                            id,
                            "Unable to remove authentication from cache",
                            ex
                    );
        }
    }

    private RealmAuthentication<RIDT, IDT, ATT> notify(RealmAuthentication<RIDT, IDT, ATT> auth) {
        notify
                (
                        auth.getRealmId(),
                        auth.getId(),
                        auth.getClientNetworkAddress()
                );
        return auth;
    }

    private void notify(RIDT realmId, IDT id) {
        notify
                (
                        realmId,
                        id,
                        null
                );
    }

    private void notify(RIDT realmId, IDT id, InetAddress cna) {
        try {
            notifier.notify
                    (
                            realmId,
                            id
                    );
        } catch (Exception ex) {
            if (cna == null)
                logger.warn
                        (
                                realmId,
                                id,
                                "Unable to notify peers for authentication change",
                                ex
                        );
            else
                logger.warn
                        (
                                realmId,
                                id,
                                cna,
                                "Unable to notify peers for authentication change",
                                ex
                        );
        }
    }

    private InMemoryKVExpirableLRUCache<IDT, RealmAuthentication<RIDT, IDT, ATT>> makeAuthenticationCache() {
        return makeAuthenticationCache(DEFAULT_MIN_CACHE_SIZE, maxCacheSize);
    }

    @SuppressWarnings("SameParameterValue")
    private InMemoryKVExpirableLRUCache<IDT, RealmAuthentication<RIDT, IDT, ATT>>
    makeAuthenticationCache(short minCacheSize,
                            short maxCacheSize) {
        return new InMemoryKVExpirableLRUCache<>
                (
                        CACHE_TIMEOUT_CHECK_INTERVAL_SECONDS,
                        minCacheSize,
                        maxCacheSize,
                        computeDrainCacheSize(minCacheSize, maxCacheSize)
                );
    }

    private short computeDrainCacheSize(short minCacheSize,
                                        short maxCacheSize) {
        return (short) Math.max(minCacheSize, (maxCacheSize - minCacheSize));
    }

    private void checkBlacklisted(InetAddress clientNetworkAddress) throws BlacklistedException {
        requestCounts
                .computeIfAbsent
                        (
                                clientNetworkAddress,
                                k -> new RequestCount()
                        )
                .checkBlacklisted(clientNetworkAddress);
    }

    private void checkFailedAuthVisitOk(InetAddress clientNetworkAddress) throws BlacklistedException {
        requestCounts
                .computeIfAbsent
                        (
                                clientNetworkAddress,
                                k -> new RequestCount()
                        )
                .checkFailedAuthVisitOK(clientNetworkAddress);
    }

    @SuppressWarnings("WeakerAccess")
    private static class RequestCount implements Expirable {

        private long firstRequest = System.currentTimeMillis();
        private long count = 0;

        public synchronized void checkFailedAuthVisitOK(InetAddress clientNetworkAddress) throws BlacklistedException {
            count++;
            if (count == Long.MAX_VALUE) {
                count = count >> 1;
                firstRequest = System.currentTimeMillis() - 180_000;
            }
            checkBlacklisted(clientNetworkAddress);
        }

        public synchronized void checkBlacklisted(InetAddress clientNetworkAddress) throws BlacklistedException {
            if (blacklisted(timeSinceFirstRequestInMinutes()))
                throw new BlacklistedException(clientNetworkAddress);
        }

        @Override
        public synchronized boolean isExpired() {
            double timeSinceFirstRequestMinutes = timeSinceFirstRequestInMinutes();
            boolean blacklisted = blacklisted(timeSinceFirstRequestMinutes);
            return !blacklisted
                    && timeSinceFirstRequestMinutes > 3;
        }

        private boolean blacklisted(double timeSinceFirstRequestMinutes) {
            return count > 10
                    && timeSinceFirstRequestMinutes > 0.1
                    && count / timeSinceFirstRequestMinutes > 30;
        }

        private double timeSinceFirstRequestInMinutes() {
            return (System.currentTimeMillis() - firstRequest) / 60_000d;
        }

    }

    @SuppressWarnings("WeakerAccess")
    public static class BlacklistedException extends RealmAuthenticationException {
        private InetAddress clientNetworkAddress;

        public BlacklistedException(InetAddress clientNetworkAddress) {
            this.clientNetworkAddress = clientNetworkAddress;
        }

        public InetAddress getClientNetworkAddress() {
            return clientNetworkAddress;
        }
    }

}
