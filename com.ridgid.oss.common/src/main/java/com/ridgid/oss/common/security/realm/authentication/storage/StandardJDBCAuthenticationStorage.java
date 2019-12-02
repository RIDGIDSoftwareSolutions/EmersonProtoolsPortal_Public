package com.ridgid.oss.common.security.realm.authentication.storage;

import com.ridgid.oss.common.helper.JdbcHelpers;
import com.ridgid.oss.common.jdbc.NamedParameterJdbcQuery;
import com.ridgid.oss.common.jdbc.transform.AttributeConverter;
import com.ridgid.oss.common.jdbc.transform.InetAddressConverter;
import com.ridgid.oss.common.security.realm.authentication.RealmAuthentication;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

import static com.ridgid.oss.common.jdbc.NamedParameterJdbcQuery.get;
import static com.ridgid.oss.common.jdbc.NamedParameterJdbcQuery.getLong;

@SuppressWarnings({
                      "unused",
                      "WeakerAccess",
                      "FieldCanBeLocal",
                      "JavaDoc",
                      "InstanceVariableOfConcreteClass",
                      "ClassHasNoToStringMethod",
                      "ClassWithTooManyFields",
                      "ConstructorWithTooManyParameters",
                      "FeatureEnvy",
                      "BoundedWildcard",
                      "AssignmentToNull"
                  })
public class StandardJDBCAuthenticationStorage<RIDT, IDT, ATT, ECT, ACT>
    extends StandardBaseAuthenticationStorage<RIDT, IDT, ATT>
{
    private final DataSource dataSource;
    private final Storage    storage;

    private final NamedParameterJdbcQuery selectQuery;
    private final NamedParameterJdbcQuery upsertStatement;
    private final NamedParameterJdbcQuery insertStatement;
    private final NamedParameterJdbcQuery updateStatement;
    private final NamedParameterJdbcQuery deleteStatement;

    private final String                        expiresColumnName;
    private final AttributeConverter<Long, ECT> expiresColumnConverter;
    private final String                        expiresParameterName;

    private final Class<RIDT> realmIdClass;
    private final String      realmIdColumnName;
    private final String      realmIdParameterName;

    private final Class<IDT> idClass;
    private final String     idColumnName;
    private final String     idParameterName;

    private final Class<ATT>                   authenticationTokenClass;
    private final String                       authenticationTokenColumnName;
    private final String                       authenticationTokenParameterName;
    private final AttributeConverter<ATT, ACT> authenticationTokenColumnConverter;

    private final String                                  clientNetworkAddressColumnName;
    private final String                                  clientNetworkAddressParameterName;
    private final AttributeConverter<InetAddress, byte[]> clientNetworkAddressColumnConverter;

    public StandardJDBCAuthenticationStorage(DataSource dataSource,
                                             String namedParameterUpsertStatement,
                                             String namedParameterSelectStatement,
                                             String namedParameterDeleteStatement,
                                             String expiresColumnName,
                                             String expiresParameterName,
                                             AttributeConverter<Long, ECT> expiresColumnConverter,
                                             Class<RIDT> realmIdClass,
                                             String realmIdColumnName,
                                             String realmIdParameterName,
                                             Class<IDT> idClass,
                                             String idColumnName,
                                             String idParameterName,
                                             Class<ATT> authenticationTokenClass,
                                             String authenticationTokenColumnName,
                                             String authenticationTokenParameterName,
                                             AttributeConverter<ATT, ACT> authenticationTokenColumnConverter,
                                             String clientNetworkAddressColumnName,
                                             String clientNetworkAddressParameterName,
                                             Function<RIDT, Long> expirationPolicy,
                                             Function<RIDT, Long> extensionPolicy)
    {
        super(expirationPolicy, extensionPolicy);
        this.dataSource                         = dataSource;
        this.expiresColumnConverter             = expiresColumnConverter;
        this.expiresParameterName               = expiresParameterName;
        storage                                 = new UpsertableStorage();
        upsertStatement                         = JdbcHelpers.parseQuery(namedParameterUpsertStatement);
        insertStatement                         = null;
        updateStatement                         = null;
        selectQuery                             = JdbcHelpers.parseQuery(namedParameterSelectStatement);
        deleteStatement                         = JdbcHelpers.parseQuery(namedParameterDeleteStatement);
        this.expiresColumnName                  = expiresColumnName;
        this.realmIdClass                       = realmIdClass;
        this.realmIdColumnName                  = realmIdColumnName;
        this.realmIdParameterName               = realmIdParameterName;
        this.idClass                            = idClass;
        this.idColumnName                       = idColumnName;
        this.idParameterName                    = idParameterName;
        this.authenticationTokenClass           = authenticationTokenClass;
        this.authenticationTokenColumnName      = authenticationTokenColumnName;
        this.authenticationTokenParameterName   = authenticationTokenParameterName;
        this.authenticationTokenColumnConverter = authenticationTokenColumnConverter;
        this.clientNetworkAddressColumnName     = clientNetworkAddressColumnName;
        this.clientNetworkAddressParameterName  = clientNetworkAddressParameterName;
        clientNetworkAddressColumnConverter     = new InetAddressConverter();
    }

    public StandardJDBCAuthenticationStorage(DataSource dataSource,
                                             String namedParameterInsertStatement,
                                             String namedParameterUpdateStatement,
                                             String namedParameterSelectStatement,
                                             String namedParameterDeleteStatement,
                                             String expiresColumnName,
                                             String expiresParameterName,
                                             AttributeConverter<Long, ECT> expiresColumnConverter,
                                             Class<RIDT> realmIdClass,
                                             String realmIdColumnName,
                                             String realmIdParameterName,
                                             Class<IDT> idClass,
                                             String idColumnName,
                                             String idParameterName,
                                             Class<ATT> authenticationTokenClass,
                                             String authenticationTokenColumnName,
                                             String authenticationTokenParameterName,
                                             AttributeConverter<ATT, ACT> authenticationTokenColumnConverter,
                                             String clientNetworkAddressColumnName,
                                             String clientNetworkAddressParameterName,
                                             Function<RIDT, Long> expirationPolicy,
                                             Function<RIDT, Long> extensionPolicy)
    {
        super(expirationPolicy, extensionPolicy);
        this.dataSource                         = dataSource;
        this.expiresColumnConverter             = expiresColumnConverter;
        storage                                 = new InsertableUpdateableStorage();
        upsertStatement                         = null;
        insertStatement                         = JdbcHelpers.parseQuery(namedParameterInsertStatement);
        updateStatement                         = JdbcHelpers.parseQuery(namedParameterUpdateStatement);
        selectQuery                             = JdbcHelpers.parseQuery(namedParameterSelectStatement);
        deleteStatement                         = JdbcHelpers.parseQuery(namedParameterDeleteStatement);
        this.expiresColumnName                  = expiresColumnName;
        this.expiresParameterName               = expiresParameterName;
        this.realmIdClass                       = realmIdClass;
        this.realmIdColumnName                  = realmIdColumnName;
        this.realmIdParameterName               = realmIdParameterName;
        this.idClass                            = idClass;
        this.idColumnName                       = idColumnName;
        this.idParameterName                    = idParameterName;
        this.authenticationTokenClass           = authenticationTokenClass;
        this.authenticationTokenColumnName      = authenticationTokenColumnName;
        this.authenticationTokenParameterName   = authenticationTokenParameterName;
        this.authenticationTokenColumnConverter = authenticationTokenColumnConverter;
        this.clientNetworkAddressColumnName     = clientNetworkAddressColumnName;
        this.clientNetworkAddressParameterName  = clientNetworkAddressParameterName;
        clientNetworkAddressColumnConverter     = new InetAddressConverter();
    }

    @Override
    public void
    store(RealmAuthentication<RIDT, IDT, ATT> auth) throws SQLException
    {
        storage.store(dataSource, auth);
    }

    @Override
    public Optional<RealmAuthentication<RIDT, IDT, ATT>>
    retrieve(RIDT realmId, IDT id, ATT authenticationToken) throws SQLException
    {
        return storage.retrieve(dataSource, realmId, id, authenticationToken);
    }

    @Override
    public void remove(RIDT realmId, IDT id, ATT authenticationToken) throws SQLException {
        storage.remove(dataSource, realmId, id, authenticationToken);
    }

    @SuppressWarnings({"ClassNamePrefixedWithPackageName", "NonStaticInnerClassInSecureContext"})
    private abstract class Storage
    {
        public abstract void store(DataSource dataSource,
                                   RealmAuthentication<RIDT, IDT, ATT> auth) throws SQLException;

        Optional<RealmAuthentication<RIDT, IDT, ATT>> retrieve(DataSource dataSource,
                                                               RIDT realmId,
                                                               IDT id,
                                                               ATT authenticationToken) throws SQLException
        {
            try ( ResultSet rs = selectQuery.prepare(dataSource.getConnection())
                                            .setParameter(realmIdParameterName,
                                                          realmId)
                                            .setParameter(idParameterName,
                                                          id)
                                            .setParameter(authenticationTokenParameterName,
                                                          authenticationToken)
                                            .executeQuery()
            ) {
                return
                    rs.next()
                    ? Optional.ofNullable
                        (
                            construct
                                (
                                    getExtensionPolicy()
                                        .apply(realmIdClass.cast(rs.getObject(realmIdColumnName))),
                                    getLong(rs,
                                            expiresColumnConverter,
                                            expiresColumnName),
                                    realmIdClass
                                        .cast(rs.getObject(realmIdColumnName)),
                                    idClass
                                        .cast(rs.getObject(idColumnName)),
                                    get(rs,
                                        authenticationTokenColumnConverter,
                                        authenticationTokenColumnName),
                                    get(rs,
                                        clientNetworkAddressColumnConverter,
                                        clientNetworkAddressColumnName)
                                )
                        )
                    : Optional.empty();
            }
        }

        public void remove(DataSource dataSource,
                           RIDT realmId,
                           IDT id,
                           ATT authenticationToken)
            throws SQLException
        {
            try ( Connection conn = dataSource.getConnection() ) {
                int numRowsDeleted = deleteStatement.prepare(conn)
                                                    .setParameter(realmIdParameterName,
                                                                  realmId)
                                                    .setParameter(idParameterName,
                                                                  id)
                                                    .setParameter(authenticationTokenParameterName,
                                                                  authenticationToken)
                                                    .executeUpdate();
            }
        }
    }

    @SuppressWarnings("NonStaticInnerClassInSecureContext")
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
                        .setParameter(expiresParameterName,
                                      expiresColumnConverter.convertToDatabaseColumn
                                          (
                                              auth.getExpiresSystemTimeMillis()
                                          ))
                        .setParameter(realmIdParameterName, auth.getRealmId())
                        .setParameter(idParameterName, auth.getId())
                        .setParameter(authenticationTokenParameterName,
                                      authenticationTokenColumnConverter.convertToDatabaseColumn
                                          (
                                              auth.getAuthenticationToken()
                                          ))
                        .setParameter(clientNetworkAddressParameterName,
                                      clientNetworkAddressColumnConverter.convertToDatabaseColumn
                                          (
                                              auth.getClientNetworkAddress()
                                          ))
                        .executeUpdate();
            }
        }
    }

    @SuppressWarnings({"NonStaticInnerClassInSecureContext", "OverlyNestedMethod", "HardcodedFileSeparator"})
    private class InsertableUpdateableStorage extends Storage
    {
        public static final String UNABLE_TO_INSERT_UPDATE = "Unable to Insert/Update: ";

        @SuppressWarnings("HardcodedLineSeparator")
        @Override
        public void store(DataSource dataSource,
                          RealmAuthentication<RIDT, IDT, ATT> auth)
            throws SQLException
        {
            SQLException insertEx = null;
            try ( Connection conn = dataSource.getConnection() ) {
                assert insertStatement != null;
                int numRowsInserted = insertStatement.prepare(conn)
                                                     .setParameter(expiresParameterName,
                                                                   expiresColumnConverter.convertToDatabaseColumn
                                                                       (
                                                                           auth.getExpiresSystemTimeMillis()
                                                                       ))
                                                     .setParameter(realmIdParameterName, auth.getRealmId())
                                                     .setParameter(idParameterName, auth.getId())
                                                     .setParameter(authenticationTokenParameterName,
                                                                   authenticationTokenColumnConverter.convertToDatabaseColumn
                                                                       (
                                                                           auth.getAuthenticationToken()
                                                                       ))
                                                     .setParameter(clientNetworkAddressParameterName,
                                                                   clientNetworkAddressColumnConverter.convertToDatabaseColumn
                                                                       (
                                                                           auth.getClientNetworkAddress()
                                                                       ))
                                                     .executeUpdate();
                if ( numRowsInserted >= 1 )
                    return;
            } catch ( SQLException ex ) {
                insertEx = ex;
            }
            try ( Connection conn = dataSource.getConnection() ) {
                assert updateStatement != null;
                int numRowsUpdated = updateStatement.prepare(conn)
                                                    .setParameter(expiresParameterName,
                                                                  expiresColumnConverter.convertToDatabaseColumn
                                                                      (
                                                                          auth.getExpiresSystemTimeMillis()
                                                                      ))
                                                    .setParameter(realmIdParameterName, auth.getRealmId())
                                                    .setParameter(idParameterName, auth.getId())
                                                    .setParameter(authenticationTokenParameterName,
                                                                  authenticationTokenColumnConverter.convertToDatabaseColumn
                                                                      (
                                                                          auth.getAuthenticationToken()
                                                                      ))
                                                    .setParameter(clientNetworkAddressParameterName,
                                                                  clientNetworkAddressColumnConverter.convertToDatabaseColumn
                                                                      (
                                                                          auth.getClientNetworkAddress()
                                                                      ))
                                                    .executeUpdate();
                if ( numRowsUpdated < 1 )
                    if ( insertEx != null )
                        throw new SQLException(UNABLE_TO_INSERT_UPDATE
                                               + auth
                                               + "\nInsert Exception was: " + insertEx.getMessage(),
                                               insertEx);
                    else
                        throw new SQLException(UNABLE_TO_INSERT_UPDATE + auth);
            }
        }
    }
}
