package com.ridgid.oss.common.security.realm;

import com.ridgid.oss.common.cache.Cache;
import com.ridgid.oss.common.cache.Expirable;
import com.ridgid.oss.common.cache.InMemoryExpirableLRUCache;
import com.ridgid.oss.common.security.realm.authentication.AuthenticationStorage;
import com.ridgid.oss.common.security.realm.authentication.RealmAuthentication;
import com.ridgid.oss.common.security.realm.authentication.RealmAuthenticationException;
import com.ridgid.oss.common.security.realm.authentication.secret.SecretValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Realm Authentication Manager
 *
 * @param <RIDT> Realm ID Type
 * @param <IDT>  ID Type
 * @param <ST>   Secret Type
 * @param <ATT>  Authentication Token Type
 */
@SuppressWarnings({"SpellCheckingInspection", "FieldCanBeLocal", "unused"})
public class RealmManager<RIDT, IDT, ST, ATT>
{

    private static final short DEFAULT_MIN_CACHE_SIZE               = 50;
    private static final short CACHE_TIMEOUT_CHECK_INTERVAL_SECONDS = 300;

    private final RealmListener<RIDT, IDT, ST, ATT>                                    listener;
    private final RealmNotifier<RIDT, IDT, ST, ATT>                                    notifier;
    private final ConcurrentMap<RIDT, Cache<ATT, RealmAuthentication<RIDT, IDT, ATT>>> authenticationCache
        = new ConcurrentHashMap<>();
    private final Cache<InetAddress, RequestCount>                                     requestCounts
        = new InMemoryExpirableLRUCache<>
        (
            (short) 300,
            (short) 100,
            (short) 1000,
            (short) 900
        );
    private final SecretValidator<RIDT, IDT, ST, ATT>                                  secretValidator;
    private final AuthenticationStorage<RIDT, IDT, ATT>                                storage;
    private final RealmLogger<RIDT, IDT>                                               logger;
    private final short                                                                maxCacheSize;

    private final QueueStoreAndNotifyTimer queuedStoreAndNotifyTimer = new QueueStoreAndNotifyTimer(true);

    protected RealmManager(RealmListener<RIDT, IDT, ST, ATT> listener,
                           RealmNotifier<RIDT, IDT, ST, ATT> notifier,
                           SecretValidator<RIDT, IDT, ST, ATT> secretValidator,
                           AuthenticationStorage<RIDT, IDT, ATT> storage,
                           RealmLogger<RIDT, IDT> logger,
                           short maxCacheSize)
    {
        this.listener        = listener;
        this.notifier        = notifier;
        this.storage         = storage;
        this.secretValidator = secretValidator;
        this.logger          = logger;
        this.maxCacheSize    = computeMaxCacheSize(DEFAULT_MIN_CACHE_SIZE, maxCacheSize);
        listener.register(this::handleListenerEvent);
    }

    @Override
    protected void finalize() throws Throwable {
        queuedStoreAndNotifyTimer.cancel();
        super.finalize();
    }

    @SuppressWarnings("SameParameterValue")
    private static short computeMaxCacheSize(short min, short requestedMax) {
        return (short) Math.max(min * 2, Math.max(1, requestedMax));
    }

    public Optional<ATT> authenticate(RIDT realmId,
                                      IDT id,
                                      ST secret,
                                      InetAddress clientNetworkAddress)
        throws BlacklistedException
    {
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
        if ( !auth.isPresent() )
            checkFailedAuthVisitOk(clientNetworkAddress);
        return auth.map(RealmAuthentication::getAuthenticationToken);
    }

    public Optional<ATT> authenticate(Collection<RIDT> realmIds,
                                      IDT id,
                                      ST secret,
                                      InetAddress clientNetworkAddress)
        throws BlacklistedException
    {
        checkBlacklisted(clientNetworkAddress);
        Optional<Stream<RealmAuthentication<RIDT, IDT, ATT>>> auths
            = secretValidator
            .authenticate(realmIds, id, secret)
            .map(att -> buildAuthentications
                     (
                         realmIds,
                         id,
                         att,
                         clientNetworkAddress
                     )
                )
            .map(this::cacheStoreAndNotify);
        if ( !auths.isPresent() )
            checkFailedAuthVisitOk(clientNetworkAddress);

        return auths.orElseGet(Stream::empty)
                    .map(RealmAuthentication::getAuthenticationToken)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                        if (list.isEmpty()) {
                            throw new IllegalStateException("Failed to find a single authentication token!");
                        }

                        return Optional.of(list.get(0));
                    }));
    }

    public boolean extendAuthentication(RIDT realmId,
                                        IDT id,
                                        ATT authenticationToken,
                                        InetAddress clientNetworkAddress)
        throws BlacklistedException
    {
        checkBlacklisted(clientNetworkAddress);
        if ( readThroughCache(realmId, id, authenticationToken)
            .map(queuedStoreAndNotifyTimer::ensureStoredAndNotifiedInTime)
            .map(RealmAuthentication::extendAuthentication)
            .isPresent() )
            return true;
        checkFailedAuthVisitOk(clientNetworkAddress);
        return false;
    }

    public boolean extendAuthentication(Collection<RIDT> realmIds,
                                        IDT id,
                                        ATT authenticationToken,
                                        InetAddress clientNetworkAddress)
        throws BlacklistedException
    {
        checkBlacklisted(clientNetworkAddress);
        if ( realmIds.stream()
                     .allMatch
                         (
                             realmId -> readThroughCache(realmId, id, authenticationToken)
                                 .map(queuedStoreAndNotifyTimer::ensureStoredAndNotifiedInTime)
                                 .map(RealmAuthentication::extendAuthentication)
                                 .isPresent()
                         )
        )
            return true;
        checkFailedAuthVisitOk(clientNetworkAddress);
        return false;
    }

    public void revokeAuthentication(RIDT realmId,
                                     IDT id,
                                     ATT authenticationToken)
    {
        removeStored(realmId, id, authenticationToken);
        removeCached(realmId, id, authenticationToken);
        notify(realmId, id, authenticationToken);
    }

    public void revokeAuthentications(Collection<RIDT> realmIds,
                                      IDT id,
                                      ATT authenticationToken)
    {
        for ( RIDT realmId : realmIds ) {
            removeStored(realmId, id, authenticationToken);
            removeCached(realmId, id, authenticationToken);
            notify(realmId, id, authenticationToken);
        }
    }

    public boolean isAuthenticatedInRealm(RIDT realmId,
                                          IDT id,
                                          ATT authenticationToken,
                                          InetAddress clientNetworkAddress)
        throws BlacklistedException
    {
        checkBlacklisted(clientNetworkAddress);
        if ( readThroughCache(realmId, id, authenticationToken)
            .map(auth -> auth.isAuthenticated(authenticationToken, clientNetworkAddress))
            .isPresent() )
            return true;
        checkFailedAuthVisitOk(clientNetworkAddress);
        return false;
    }

    private Boolean handleListenerEvent(RIDT realmId, IDT id, ATT authenticationToken) {
        try {
            refreshCache(realmId, id, authenticationToken);
            return true;
        } catch ( Exception ex ) {
            return false;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private Optional<RealmAuthentication<RIDT, IDT, ATT>> refreshCache(RIDT realmId, IDT id, ATT authenticationToken) {
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
                            authenticationToken,
                            (key, v) -> readStoredUncheckExceptions
                                (
                                    realmId,
                                    id,
                                    key
                                )
                                .orElse(null)
                        )
            );
    }

    private Optional<RealmAuthentication<RIDT, IDT, ATT>> readThroughCache(RIDT realmId, IDT id, ATT authenticationToken) {
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
                            authenticationToken,
                            key -> readStoredUncheckExceptions
                                (
                                    realmId,
                                    id,
                                    key
                                )
                                .orElse(null)
                        )
            );
    }

    private Optional<RealmAuthentication<RIDT, IDT, ATT>> readStoredUncheckExceptions(RIDT realmId,
                                                                                      IDT key,
                                                                                      ATT authenticationToken)
    {
        try {
            return readStored(realmId, key, authenticationToken);
        } catch ( SQLException | UnknownHostException e ) {
            throw new RuntimeException(e);
        }
    }

    private Optional<RealmAuthentication<RIDT, IDT, ATT>> readStored(RIDT realmId,
                                                                     IDT id,
                                                                     ATT authenticationToken)
        throws SQLException, UnknownHostException
    {
        return storage.retrieve(realmId, id, authenticationToken);
    }

    private RealmAuthentication<RIDT, IDT, ATT> buildAuthentication(RIDT realmId,
                                                                    IDT id,
                                                                    ATT att,
                                                                    InetAddress clientNetworkAddress)
    {
        return storage.construct
            (
                realmId,
                id,
                att,
                clientNetworkAddress
            );
    }

    private Stream<RealmAuthentication<RIDT, IDT, ATT>> buildAuthentications(Collection<RIDT> realmIds,
                                                                             IDT id,
                                                                             ATT att,
                                                                             InetAddress clientNetworkAddress)
    {
        return realmIds
            .stream()
            .map
                (
                    realmId -> storage.construct
                        (
                            realmId,
                            id,
                            att,
                            clientNetworkAddress
                        )
                );
    }

    private Stream<RealmAuthentication<RIDT, IDT, ATT>> cacheStoreAndNotify(Stream<RealmAuthentication<RIDT, IDT, ATT>> auths) {
        return auths.peek(auth -> {
            cache(auth);
            store(auth);
            notify(auth);
        });
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
                        auth.getAuthenticationToken(),
                        auth
                    );
        } catch ( Exception ex ) {
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

    private void store(RealmAuthentication<RIDT, IDT, ATT> auth) {
        try {
            storage.store(auth);
        } catch ( Exception ex ) {
            logger.warn
                (
                    auth.getRealmId(),
                    auth.getId(),
                    auth.getClientNetworkAddress(),
                    "Unable to store authentication",
                    ex
                );
        }
    }

    private void removeStored(RIDT realmId, IDT id, ATT authenticationToken) {
        try {
            storage.remove
                (
                    realmId,
                    id,
                    authenticationToken
                );
        } catch ( Exception ex ) {
            logger.warn
                (
                    realmId,
                    id,
                    "Unable to remove authentication from storage",
                    ex
                );
        }
    }

    private void removeCached(RIDT realmId, IDT id, ATT authenticationToken) {
        try {
            authenticationCache
                .computeIfAbsent
                    (
                        realmId,
                        k -> makeAuthenticationCache()
                    )
                .remove
                    (
                        authenticationToken
                    );
        } catch ( Exception ex ) {
            logger.warn
                (
                    realmId,
                    id,
                    "Unable to remove authentication from cache",
                    ex
                );
        }
    }

    private void notify(RealmAuthentication<RIDT, IDT, ATT> auth) {
        notify
            (
                auth.getRealmId(),
                auth.getId(),
                auth.getAuthenticationToken(),
                auth.getClientNetworkAddress()
            );
    }

    private void notify(RIDT realmId, IDT id, ATT authenticationToken) {
        notify
            (
                realmId,
                id,
                authenticationToken,
                null
            );
    }

    private void notify(RIDT realmId, IDT id, ATT authenticationToken, InetAddress cna) {
        try {
            notifier.notify
                (
                    realmId,
                    id
                );
        } catch ( Exception ex ) {
            if ( cna == null )
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

    private InMemoryExpirableLRUCache<ATT, RealmAuthentication<RIDT, IDT, ATT>> makeAuthenticationCache() {
        return makeAuthenticationCache(DEFAULT_MIN_CACHE_SIZE, maxCacheSize);
    }

    @SuppressWarnings("SameParameterValue")
    private InMemoryExpirableLRUCache<ATT, RealmAuthentication<RIDT, IDT, ATT>>
    makeAuthenticationCache(short minCacheSize,
                            short maxCacheSize)
    {
        return new InMemoryExpirableLRUCache<>
            (
                CACHE_TIMEOUT_CHECK_INTERVAL_SECONDS,
                minCacheSize,
                maxCacheSize,
                computeDrainCacheSize(minCacheSize, maxCacheSize)
            );
    }

    private short computeDrainCacheSize(short minCacheSize,
                                        short maxCacheSize)
    {
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
    private static class RequestCount implements Expirable
    {

        private long firstRequest = System.currentTimeMillis();
        private long count        = 0;

        public synchronized void checkFailedAuthVisitOK(InetAddress clientNetworkAddress) throws BlacklistedException {
            count++;
            if ( count == Long.MAX_VALUE ) {
                count        = count >> 1;
                firstRequest = System.currentTimeMillis() - 180_000;
            }
            checkBlacklisted(clientNetworkAddress);
        }

        public synchronized void checkBlacklisted(InetAddress clientNetworkAddress) throws BlacklistedException {
            if ( blacklisted(timeSinceFirstRequestInMinutes()) )
                throw new BlacklistedException(clientNetworkAddress);
        }

        @Override
        public synchronized boolean isExpired() {
            double  timeSinceFirstRequestMinutes = timeSinceFirstRequestInMinutes();
            boolean blacklisted                  = blacklisted(timeSinceFirstRequestMinutes);
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
    public static class BlacklistedException extends RealmAuthenticationException
    {
        private InetAddress clientNetworkAddress;

        public BlacklistedException(InetAddress clientNetworkAddress) {
            this.clientNetworkAddress = clientNetworkAddress;
        }

        public InetAddress getClientNetworkAddress() {
            return clientNetworkAddress;
        }
    }

    private class QueueStoreAndNotifyTimer extends Timer
    {
        private final TimerTask storeNotifyTask = new TimerTask()
        {
            @Override
            public void run() {
                authenticationCache
                    .values()
                    .stream()
                    .flatMap(Cache::streamValues)
                    .filter(RealmAuthentication::needsPersisted)
                    .peek(RealmManager.this::store)
                    .peek(RealmManager.this::notify)
                    .forEach(RealmAuthentication::persisted);
                synchronized ( this ) {
                    nextTimeToFire = randomNearTimeInFuture();
                }
            }
        };

        private long nextTimeToFire = randomNearTimeInFuture();

        QueueStoreAndNotifyTimer(boolean isDaemon) {
            super(isDaemon);
        }

        RealmAuthentication<RIDT, IDT, ATT>
        ensureStoredAndNotifiedInTime(RealmAuthentication<RIDT, IDT, ATT> auth)
        {
            reschedule(Math.min(auth.getExpiresSystemTimeMillis() - 300_000, nextTimeToFire));
            return auth;
        }

        private void reschedule(long latestTimeToFire) {
            if ( latestTimeToFire < nextTimeToFire )
                synchronized ( storeNotifyTask ) {
                    if ( latestTimeToFire >= nextTimeToFire && nextTimeToFire > System.currentTimeMillis() ) return;
                    storeNotifyTask.cancel();
                    schedule(storeNotifyTask, Math.max(5000, latestTimeToFire - System.currentTimeMillis()));
                }
        }

        private long randomNearTimeInFuture() {
            return System.currentTimeMillis() + (long) Math.floor(Math.random() * 15 + 10) * 60000;
        }
    }
}
