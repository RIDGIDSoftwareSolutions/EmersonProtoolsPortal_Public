package com.ridgid.oss.common.jdbc;

import java.sql.*;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

@SuppressWarnings("unused")
public class NamedParameterJdbcQuery {

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\W:([A-Za-z][A-Za-z0-9_]+)\\W");

    private final String query;
    private final Map<String, List<Integer>> parameterMap = new HashMap<>();
    private int parameterNumber = 0;

    public NamedParameterJdbcQuery(String query) {
        this.query = parseParametersInQuery(query);
    }

    private String parseParametersInQuery(String query) {
        return PARAMETER_PATTERN
                .splitAsStream(query)
                .map(s -> {
                    Matcher m = PARAMETER_PATTERN.matcher(query);
                    MatchResult mr = m.toMatchResult();
                    String pName = mr.group(1);
                    parameterNumber++;
                    parameterMap
                            .computeIfAbsent(pName, k -> new ArrayList<>())
                            .add(parameterNumber);
                    return m.replaceFirst("?");
                })
                .collect(joining(" "));
    }

    private List<Integer> getParameterIndices(String parameterName) {
        return Collections.unmodifiableList
                (
                        parameterMap
                                .getOrDefault(
                                        parameterName,
                                        Collections.emptyList()
                                )
                );
    }

    public PreparedNamedParameterJdbcQuery prepare(Connection conn) throws SQLException {
        return new PreparedNamedParameterJdbcQuery(this, conn);
    }

    @SuppressWarnings("WeakerAccess")
    public static class PreparedNamedParameterJdbcQuery {

        private final NamedParameterJdbcQuery unpreparedQuery;
        private final PreparedStatement preparedStatement;

        public PreparedNamedParameterJdbcQuery(NamedParameterJdbcQuery unpreparedQuery,
                                               Connection conn
        ) throws SQLException {
            this.unpreparedQuery = unpreparedQuery;
            this.preparedStatement = conn.prepareStatement(unpreparedQuery.query);
        }

        public PreparedNamedParameterJdbcQuery setParameter(String parameterName,
                                                            Object value
        ) throws SQLException {
            ParameterMetaData md = preparedStatement.getParameterMetaData();
            for (int pidx : unpreparedQuery.getParameterIndices(parameterName))
                if (value == null)
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
                                         int pidx
        ) throws SQLException {
            preparedStatement.setObject
                    (
                            pidx,
                            value,
                            md.getParameterType(pidx)
                    );
        }

    }
}
