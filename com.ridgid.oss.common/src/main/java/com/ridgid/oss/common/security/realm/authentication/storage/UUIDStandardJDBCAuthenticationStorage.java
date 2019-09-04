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
    private static final String namedParameterUpsertStatement
        = "merge into Security.UserSessionAuthentication usa "
          + "using ( "
          + "    values ( :userId, :realmId, :clientNetworkAddress, :authenticationToken, :expires ) "
          + ") "
          + "    v ( UserId, RealmId, ClientNetworkAddress, AuthenticationToken, Expires ) "
          + "on "
          + "    v.UserId = usa.UserId "
          + "    and "
          + "    v.RealmId = usa.RealmId "
          + "when matched then update "
          + "set "
          + "    ClientNetworkAddress = v.ClientNetworkAddress, "
          + "    AuthenticationToken = v.AuthenticationToken, "
          + "    Expires = v.Expires "
          + "when not matched then insert "
          + "    ( UserId, RealmId, ClientNetworkAddress, AuthenticationToken, Expires ) "
          + "    values "
          + "    ( v.UserId, v.RealmId, v.ClientNetworkAddress, v.AuthenticationToken, v.Expires ); ";

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
                namedParameterUpsertStatement,
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
