package com.ridgid.oss.common.security.realm.authentication.storage;

import com.ridgid.oss.common.helper.JdbcHelpers;
import com.ridgid.oss.common.jdbc.NamedParameterJdbcQuery;
import com.ridgid.oss.common.security.realm.authentication.RealmAuthentication;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings({"unused", "SpellCheckingInspection", "WeakerAccess"})
public class StandardJDBCAuthenticationStorage<RIDT, IDT, ATT>
    extends StandardBaseAuthenticationStorage<RIDT, IDT, ATT>
{
    private final DataSource              dataSource;
    private final Storage                 storage;
    private final NamedParameterJdbcQuery selectQuery;
    private final NamedParameterJdbcQuery upsertStatement;
    private final NamedParameterJdbcQuery insertStatement;
    private final NamedParameterJdbcQuery updateStatement;
    private final NamedParameterJdbcQuery deleteStatement;
    private final String                  expiresColumnName;
    private final Class<RIDT>             realmIdClass;
    private final String                  realmIdColumnName;
    private final String                  realmIdParameterName;
    private final Class<IDT>              idClass;
    private final String                  idColumnName;
    private final String                  idParameterName;
    private final Class<ATT>              authenticationTokenClass;
    private final String                  authenticationTokenColumnName;
    private final String                  authenticationTokenParameterName;
    private final String                  clientNetworkAddressColumnName;
    private final String                  clientNetworkAddressParameterName;

    public StandardJDBCAuthenticationStorage(DataSource dataSource,
                                             String namedParameterUpsertStatement,
                                             String namedParameterSelectStatement,
                                             String namedParameterDeleteStatement,
                                             String expiresColumnName,
                                             Class<RIDT> realmIdClass,
                                             String realmIdColumnName,
                                             String realmIdParameterName,
                                             Class<IDT> idClass,
                                             String idColumnName,
                                             String idParameterName,
                                             Class<ATT> authenticationTokenClass,
                                             String authenticationTokenColumnName,
                                             String authenticationTokenParameterName,
                                             String clientNetworkAddressColumnName,
                                             String clientNetworkAddressParameterName,
                                             Function<RIDT, Long> expirationPolicy,
                                             Function<RIDT, Long> extensionPolicy)
    {
        super(expirationPolicy, extensionPolicy);
        this.dataSource                        = dataSource;
        this.storage                           = new UpsertableStorage();
        this.upsertStatement                   = JdbcHelpers.parseQuery(namedParameterUpsertStatement);
        this.insertStatement                   = null;
        this.updateStatement                   = null;
        this.selectQuery                       = JdbcHelpers.parseQuery(namedParameterSelectStatement);
        this.deleteStatement                   = JdbcHelpers.parseQuery(namedParameterDeleteStatement);
        this.expiresColumnName                 = expiresColumnName;
        this.realmIdClass                      = realmIdClass;
        this.realmIdColumnName                 = realmIdColumnName;
        this.realmIdParameterName              = realmIdParameterName;
        this.idClass                           = idClass;
        this.idColumnName                      = idColumnName;
        this.idParameterName                   = idParameterName;
        this.authenticationTokenClass          = authenticationTokenClass;
        this.authenticationTokenColumnName     = authenticationTokenColumnName;
        this.authenticationTokenParameterName  = authenticationTokenParameterName;
        this.clientNetworkAddressColumnName    = clientNetworkAddressColumnName;
        this.clientNetworkAddressParameterName = clientNetworkAddressParameterName;
    }

    public StandardJDBCAuthenticationStorage(DataSource dataSource,
                                             String namedParameterInsertStatement,
                                             String namedParameterUpdateStatement,
                                             String namedParameterSelectStatement,
                                             String namedParameterDeleteStatement,
                                             String expiresColumnName,
                                             Class<RIDT> realmIdClass,
                                             String realmIdColumnName,
                                             String realmIdParameterName,
                                             Class<IDT> idClass,
                                             String idColumnName,
                                             String idParameterName,
                                             Class<ATT> authenticationTokenClass,
                                             String authenticationTokenColumnName,
                                             String authenticationTokenParameterName,
                                             String clientNetworkAddressColumnName,
                                             String clientNetworkAddressParameterName,
                                             Function<RIDT, Long> expirationPolicy,
                                             Function<RIDT, Long> extensionPolicy)
    {
        super(expirationPolicy, extensionPolicy);
        this.dataSource                        = dataSource;
        this.storage                           = new InsertableUpdateableStorage();
        this.upsertStatement                   = null;
        this.insertStatement                   = JdbcHelpers.parseQuery(namedParameterInsertStatement);
        this.updateStatement                   = JdbcHelpers.parseQuery(namedParameterUpdateStatement);
        this.selectQuery                       = JdbcHelpers.parseQuery(namedParameterSelectStatement);
        this.deleteStatement                   = JdbcHelpers.parseQuery(namedParameterDeleteStatement);
        this.expiresColumnName                 = expiresColumnName;
        this.realmIdClass                      = realmIdClass;
        this.realmIdColumnName                 = realmIdColumnName;
        this.realmIdParameterName              = realmIdParameterName;
        this.idClass                           = idClass;
        this.idColumnName                      = idColumnName;
        this.idParameterName                   = idParameterName;
        this.authenticationTokenClass          = authenticationTokenClass;
        this.authenticationTokenColumnName     = authenticationTokenColumnName;
        this.authenticationTokenParameterName  = authenticationTokenParameterName;
        this.clientNetworkAddressColumnName    = clientNetworkAddressColumnName;
        this.clientNetworkAddressParameterName = clientNetworkAddressParameterName;
    }

    @Override
    public void
    store(RealmAuthentication<RIDT, IDT, ATT> auth) throws SQLException
    {
        storage.store(dataSource, auth);
    }

    @Override
    public Optional<RealmAuthentication<RIDT, IDT, ATT>>
    retrieve(RIDT realmId, IDT id) throws SQLException, UnknownHostException
    {
        return storage.retrieve(dataSource, realmId, id);
    }

    @Override
    public void remove(RIDT realmId, IDT id) throws SQLException {
        storage.remove(dataSource, realmId, id);
    }

    private abstract class Storage
    {
        public abstract void store(DataSource dataSource,
                                   RealmAuthentication<RIDT, IDT, ATT> auth) throws SQLException;

        Optional<RealmAuthentication<RIDT, IDT, ATT>> retrieve(DataSource dataSource,
                                                               RIDT realmId,
                                                               IDT id) throws SQLException, UnknownHostException
        {
            try ( Connection conn = dataSource.getConnection() ) {
                try ( ResultSet rs =
                          (
                              selectQuery
                                  .prepare(conn)
                                  .setParameter(realmIdParameterName, realmId)
                                  .setParameter(idParameterName, id)
                                  .executeQuery()
                          )
                ) {
                    if ( rs.first() ) {
                        return Optional.ofNullable
                            (
                                construct
                                    (
                                        getExtensionPolicy().apply(realmIdClass.cast(rs.getObject(realmIdColumnName))),
                                        rs.getLong(expiresColumnName),
                                        realmIdClass.cast(rs.getObject(realmIdColumnName)),
                                        idClass.cast(rs.getObject(idColumnName)),
                                        authenticationTokenClass.cast(rs.getObject(authenticationTokenColumnName)),
                                        InetAddress.getByName(rs.getString(clientNetworkAddressColumnName))
                                    )
                            );
                    } else
                        return Optional.empty();
                }
            }
        }

        public void remove(DataSource dataSource,
                           RIDT realmId,
                           IDT id) throws SQLException
        {
            try ( Connection conn = dataSource.getConnection() ) {
                int numRowsDeleted =
                    deleteStatement
                        .prepare(conn)
                        .setParameter(realmIdParameterName, realmId)
                        .setParameter(idParameterName, id)
                        .executeUpdate();
            }
        }
    }

    private class UpsertableStorage extends Storage
    {
        @Override
        public void store(DataSource dataSource,
                          RealmAuthentication<RIDT, IDT, ATT> auth) throws SQLException
        {
            try ( Connection conn = dataSource.getConnection() ) {
                assert upsertStatement != null;
                int numRowsUpdated =
                    upsertStatement
                        .prepare(conn)
                        .setParameter(expiresColumnName, auth.getExpiresSystemTimeMillis())
                        .setParameter(realmIdParameterName, auth.getRealmId())
                        .setParameter(idParameterName, auth.getId())
                        .setParameter(authenticationTokenParameterName, auth.getAuthenticationToken())
                        .setParameter(clientNetworkAddressParameterName,
                                      auth.getClientNetworkAddress().getHostAddress())
                        .executeUpdate();
            }
        }
    }

    private class InsertableUpdateableStorage extends Storage
    {
        @Override
        public void store(DataSource dataSource,
                          RealmAuthentication<RIDT, IDT, ATT> auth) throws SQLException
        {
            SQLException insertEx = null;
            try ( Connection conn = dataSource.getConnection() ) {
                assert insertStatement != null;
                int numRowsInserted =
                    insertStatement
                        .prepare(conn)
                        .setParameter(expiresColumnName, auth.getExpiresSystemTimeMillis())
                        .setParameter(realmIdParameterName, auth.getRealmId())
                        .setParameter(idParameterName, auth.getId())
                        .setParameter(authenticationTokenParameterName, auth.getAuthenticationToken())
                        .setParameter(clientNetworkAddressParameterName,
                                      auth.getClientNetworkAddress().getHostAddress())
                        .executeUpdate();
                if ( numRowsInserted >= 1 )
                    return;
            } catch ( SQLException ex ) {
                insertEx = ex;
            }
            try ( Connection conn = dataSource.getConnection() ) {
                assert updateStatement != null;
                int numRowsUpdated =
                    updateStatement
                        .prepare(conn)
                        .setParameter(expiresColumnName, auth.getExpiresSystemTimeMillis())
                        .setParameter(realmIdParameterName, auth.getRealmId())
                        .setParameter(idParameterName, auth.getId())
                        .setParameter(authenticationTokenParameterName, auth.getAuthenticationToken())
                        .setParameter(clientNetworkAddressParameterName,
                                      auth.getClientNetworkAddress().getHostAddress())
                        .executeUpdate();
                if ( numRowsUpdated < 1 )
                    if ( insertEx != null )
                        throw new SQLException(
                            "Unable to Insert/Update: " + auth + "\nInsert Exception was: " + insertEx.getMessage(),
                            insertEx);
                    else
                        throw new SQLException("Unable to Insert/Update: " + auth);
            }
        }
    }
}
