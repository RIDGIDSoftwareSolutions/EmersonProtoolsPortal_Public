package com.ridgid.oss.common.jdbc;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class NamedParameterJdbcQuery
{
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(":([A-Za-z][A-Za-z0-9_]*)");

    private final String                     query;
    private final Map<String, List<Integer>> parameterMap    = new HashMap<>();
    private       int                        parameterNumber = 0;

    public NamedParameterJdbcQuery(String query) {
        this.query = parseParametersInQuery(query);
    }

    private String parseParametersInQuery(String query) {
        Matcher      m  = PARAMETER_PATTERN.matcher(query);
        StringBuffer sb = new StringBuffer();
        while ( m.find() ) {
            MatchResult mr    = m.toMatchResult();
            String      pName = mr.group(1);
            parameterNumber++;
            parameterMap
                .computeIfAbsent(pName, k -> new ArrayList<>())
                .add(parameterNumber);
            m.appendReplacement(sb, "?");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private List<Integer> getParameterIndices(String parameterName) {
        return Collections.unmodifiableList
            (
                parameterMap
                    .getOrDefault(parameterName,
                                  Collections.emptyList())
            );
    }

    public PreparedNamedParameterJdbcQuery prepare(Connection conn) throws SQLException {
        return new PreparedNamedParameterJdbcQuery(this, conn);
    }

    @SuppressWarnings({"WeakerAccess", "SpellCheckingInspection"})
    public static class PreparedNamedParameterJdbcQuery
    {
        private final NamedParameterJdbcQuery unpreparedQuery;
        private final PreparedStatement       preparedStatement;

        public PreparedNamedParameterJdbcQuery(NamedParameterJdbcQuery unpreparedQuery,
                                               Connection conn)
            throws SQLException
        {
            this.unpreparedQuery   = unpreparedQuery;
            this.preparedStatement = conn.prepareStatement(unpreparedQuery.query);
        }

        public PreparedNamedParameterJdbcQuery setParameter(String parameterName,
                                                            Object value)
            throws SQLException
        {
            ParameterMetaData md = preparedStatement.getParameterMetaData();
            for ( int pidx : unpreparedQuery.getParameterIndices(parameterName) )
                if ( value == null )
                    preparedStatement.setNull
                        (
                            pidx,
                            md.getParameterType(pidx)
                        );
                else
                    setNonNullParameter(value, md, pidx);
            return this;
        }

        public ResultSet executeQuery() throws SQLException {
            preparedStatement.closeOnCompletion();
            return preparedStatement.executeQuery();
        }

        public int executeUpdate() throws SQLException {
            preparedStatement.closeOnCompletion();
            return preparedStatement.executeUpdate();
        }

        private void setNonNullParameter(Object value,
                                         ParameterMetaData md,
                                         int pidx)
            throws SQLException
        {
            preparedStatement.setObject
                (
                    pidx,
                    value,
                    md.getParameterType(pidx)
                );
        }
    }
}
