package com.ridgid.oss.common.jdbc;

import com.ridgid.oss.common.helper.ResourceHelper;
import com.ridgid.oss.common.jdbc.NamedParameterJdbcQuery.PreparedNamedParameterJdbcQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("SpellCheckingInspection")
class NamedParameterJdbcQuery_Test
{
    private Connection conn;

    @BeforeEach
    void setup() throws SQLException, IOException {
        conn = DriverManager.getConnection("jdbc:h2:mem:bootapp;"
                                           + "DB_CLOSE_DELAY=-1");
        PreparedStatement initialization = conn.prepareStatement
            (
                ResourceHelper.loadResourceToString("NamedParameterJdbcQuery_Test_Setup.sql",
                                                    this.getClass())
            );
        initialization.execute();
        initialization.close();
    }

    @Test
    void it_constructs_with_a_nonparameterized_query() {
        Assertions.assertDoesNotThrow(() -> new NamedParameterJdbcQuery("select * from Dummy"));
    }

    @SuppressWarnings("unused")
    @Test
    void it_prepares_with_a_nonparameterized_query() throws SQLException {
        NamedParameterJdbcQuery         query         = new NamedParameterJdbcQuery("select * from Dummy");
        PreparedNamedParameterJdbcQuery preparedQuery = query.prepare(conn);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    void it_executes_a_nonparameterized_query() throws SQLException {
        NamedParameterJdbcQuery         query         = new NamedParameterJdbcQuery("select * from Dummy");
        PreparedNamedParameterJdbcQuery preparedQuery = query.prepare(conn);
        try ( ResultSet rs = preparedQuery.executeQuery() ) {
            while ( rs.next() ) ;
        }
    }

    @Test
    void it_constructs_with_a_parameterized_query() {
        Assertions.assertDoesNotThrow(() -> new NamedParameterJdbcQuery("select * from Dummy where Code = :code"));
    }

    @SuppressWarnings("unused")
    @Test
    void it_prepares_with_a_parameterized_query() throws SQLException {
        NamedParameterJdbcQuery         query         = new NamedParameterJdbcQuery(
            "select * from Dummy where Code = :code");
        PreparedNamedParameterJdbcQuery preparedQuery = query.prepare(conn);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    void it_executes_a_parameterized_query() throws SQLException {
        NamedParameterJdbcQuery         query         = new NamedParameterJdbcQuery(
            "select * from Dummy where Code = :code");
        PreparedNamedParameterJdbcQuery preparedQuery = query.prepare(conn);
        preparedQuery.setParameter("code", "XXX");
        try ( ResultSet rs = preparedQuery.executeQuery() ) {
            while ( rs.next() ) ;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    void it_executes_a_parameterized_query_with_2_parameters() throws SQLException {
        NamedParameterJdbcQuery         query         = new NamedParameterJdbcQuery(
            "select * from Dummy where Code = :code");
        PreparedNamedParameterJdbcQuery preparedQuery = query.prepare(conn);
        preparedQuery.setParameter("code", "XXX");
        try ( ResultSet rs = preparedQuery.executeQuery() ) {
            while ( rs.next() ) ;
        }
    }

    @Test
    void it_executes_a_merge_parameterized_query() throws SQLException {
        NamedParameterJdbcQuery query
            = new NamedParameterJdbcQuery
            (
                "merge into Dummy d "
                + "using ( "
                + "    select Id, Code, Name from values ( :id, :code, :name ) as v1 ( Id, Code, Name ) "
                + ") as v  "
                + "on "
                + "    ( v.Id = d.Id ) "
                + "when matched then update "
                + "set "
                + "    d.Id = v.Id, "
                + "    d.Code = v.Code, "
                + "    d.Name = v.Name "
                + "when not matched then insert "
                + "    ( Id, Code, Name ) "
                + "    values "
                + "    ( v.Id, v.Code, v.Name ) "
            );
        PreparedNamedParameterJdbcQuery preparedQuery = query.prepare(conn);
        preparedQuery.setParameter("id", "3");
        preparedQuery.setParameter("code", "XXX");
        preparedQuery.setParameter("name", "Jim");
        int rowsAffected = preparedQuery.executeUpdate();
        assertEquals(1, rowsAffected);
    }


}