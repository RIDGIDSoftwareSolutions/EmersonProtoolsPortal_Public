package com.ridgid.oss.common.security.realm.authentication.storage;

import com.ridgid.oss.common.jdbc.transform.SystemTimeMillisAsLocalDateTimeConverter;
import com.ridgid.oss.common.jdbc.transform.UUIDConverter;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class UUIDStandardJDBCAuthenticationStorage
    extends StandardJDBCAuthenticationStorage<Integer, Integer, UUID, LocalDateTime, String>
{
    private static final String namedParameterInsertStatement
        = "insert into Security.UserSessionAuthentication (UserId, Realmid, ClientNetworkAddress, AuthenticationToken, Expires) " +
            "values ( :userId, :realmId, :clientNetworkAddress, :authenticationToken, :expires )";

    private static final String namedParameterSelectStatement
        = "select UserId, RealmId, ClientNetworkAddress, AuthenticationToken, Expires "
          + "from Security.UserSessionAuthentication "
          + "where UserId = :userId and RealmId = :realmId and AuthenticationToken = :authenticationToken; ";

    private static final String namedParameterDeleteStatement
        = "delete Security.UserSessionAuthentication "
          + "where UserId = :userId and RealmId = :realmId and AuthenticationToken = :authenticationToken; ";

    private static final String         expiresColumnName                 = "Expires";
    private static final String         expiresParameterName              = "expires";
    private static final Class<Integer> realmIdClass                      = Integer.class;
    private static final String         realmIdColumnName                 = "RealmId";
    private static final String         realmIdParameterName              = "realmId";
    private static final Class<Integer> idClass                           = Integer.class;
    private static final String         idColumnName                      = "UserId";
    private static final String         idParameterName                   = "userId";
    private static final Class<UUID>    authenticationTokenClass          = UUID.class;
    private static final String         authenticationTokenColumnName     = "AuthenticationToken";
    private static final String         authenticationTokenParameterName  = "authenticationToken";
    private static final String         clientNetworkAddressColumnName    = "ClientNetworkAddress";
    private static final String         clientNetworkAddressParameterName = "clientNetworkAddress";

    public UUIDStandardJDBCAuthenticationStorage
        (
            DataSource dataSource,
            Function<Integer, Long> expirationPolicy,
            Function<Integer, Long> extensionPolicy
        )
    {
        super
            (
                dataSource,
                namedParameterInsertStatement,
                namedParameterSelectStatement,
                namedParameterDeleteStatement,
                expiresColumnName,
                expiresParameterName,
                new SystemTimeMillisAsLocalDateTimeConverter(),
                realmIdClass,
                realmIdColumnName,
                realmIdParameterName,
                idClass,
                idColumnName,
                idParameterName,
                authenticationTokenClass,
                authenticationTokenColumnName,
                authenticationTokenParameterName,
                new UUIDConverter(),
                clientNetworkAddressColumnName,
                clientNetworkAddressParameterName,
                expirationPolicy,
                extensionPolicy
            );
    }
}
