package com.ridgid.oss.common.cache.jdbc;

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
                                               Connection conn) throws SQLException {
            this.unpreparedQuery = unpreparedQuery;
            this.preparedStatement = conn.prepareStatement(unpreparedQuery.query);
        }

        public PreparedNamedParameterJdbcQuery setParameter(String parameterName, Object value) throws SQLException {
            ParameterMetaData md = preparedStatement.getParameterMetaData();
            for (int pidx : unpreparedQuery.getParameterIndices(parameterName)) {
                if (value == null)
                    preparedStatement.setNull
                            (
                                    pidx,
                                    md.getParameterType(pidx)
                            );
                else {
                    setNonNullParameter(value, md, pidx);
                }
            }
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
                                         int pidx) throws SQLException {
            if (Boolean.class.isAssignableFrom(value.getClass()))
                preparedStatement.setObject
                        (
                                pidx,
                                value,
                                md.getParameterType(pidx)
                        );
        }

/*
        @Override
        public void setBoolean(int parameterIndex, boolean x) throws SQLException {
            preparedStatement.setBoolean(parameterIndex, x);
        }

        @Override
        public void setByte(int parameterIndex, byte x) throws SQLException {
            preparedStatement.setByte(parameterIndex, x);
        }

        @Override
        public void setShort(int parameterIndex, short x) throws SQLException {
            preparedStatement.setShort(parameterIndex, x);
        }

        @Override
        public void setInt(int parameterIndex, int x) throws SQLException {
            preparedStatement.setInt(parameterIndex, x);
        }

        @Override
        public void setLong(int parameterIndex, long x) throws SQLException {
            preparedStatement.setLong(parameterIndex, x);
        }

        @Override
        public void setFloat(int parameterIndex, float x) throws SQLException {
            preparedStatement.setFloat(parameterIndex, x);
        }

        @Override
        public void setDouble(int parameterIndex, double x) throws SQLException {
            preparedStatement.setDouble(parameterIndex, x);
        }

        @Override
        public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
            preparedStatement.setBigDecimal(parameterIndex, x);
        }

        @Override
        public void setString(int parameterIndex, String x) throws SQLException {
            preparedStatement.setString(parameterIndex, x);
        }

        @Override
        public void setBytes(int parameterIndex, byte[] x) throws SQLException {
            preparedStatement.setBytes(parameterIndex, x);
        }

        @Override
        public void setDate(int parameterIndex, Date x) throws SQLException {
            preparedStatement.setDate(parameterIndex, x);
        }

        @Override
        public void setTime(int parameterIndex, Time x) throws SQLException {
            preparedStatement.setTime(parameterIndex, x);
        }

        @Override
        public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
            preparedStatement.setTimestamp(parameterIndex, x);
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
            preparedStatement.setAsciiStream(parameterIndex, x, length);
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
            preparedStatement.setBinaryStream(parameterIndex, x, length);
        }

        @Override
        public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
            preparedStatement.setObject(parameterIndex, x, targetSqlType);
        }

        @Override
        public void setObject(int parameterIndex, Object x) throws SQLException {
            preparedStatement.setObject(parameterIndex, x);
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
            preparedStatement.setCharacterStream(parameterIndex, reader, length);
        }

        @Override
        public void setRef(int parameterIndex, Ref x) throws SQLException {
            preparedStatement.setRef(parameterIndex, x);
        }

        @Override
        public void setBlob(int parameterIndex, Blob x) throws SQLException {
            preparedStatement.setBlob(parameterIndex, x);
        }

        @Override
        public void setClob(int parameterIndex, Clob x) throws SQLException {
            preparedStatement.setClob(parameterIndex, x);
        }

        @Override
        public void setArray(int parameterIndex, Array x) throws SQLException {
            preparedStatement.setArray(parameterIndex, x);
        }

        @Override
        public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
            preparedStatement.setDate(parameterIndex, x, cal);
        }

        @Override
        public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
            preparedStatement.setTime(parameterIndex, x, cal);
        }

        @Override
        public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
            preparedStatement.setTimestamp(parameterIndex, x, cal);
        }

        @Override
        public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
            preparedStatement.setNull(parameterIndex, sqlType, typeName);
        }

        @Override
        public void setURL(int parameterIndex, URL x) throws SQLException {
            preparedStatement.setURL(parameterIndex, x);
        }

        @Override
        public void setNString(int parameterIndex, String value) throws SQLException {
            preparedStatement.setNString(parameterIndex, value);
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
            preparedStatement.setNCharacterStream(parameterIndex, value, length);
        }

        @Override
        public void setNClob(int parameterIndex, NClob value) throws SQLException {
            preparedStatement.setNClob(parameterIndex, value);
        }

        @Override
        public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
            preparedStatement.setClob(parameterIndex, reader, length);
        }

        @Override
        public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
            preparedStatement.setBlob(parameterIndex, inputStream, length);
        }

        @Override
        public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
            preparedStatement.setNClob(parameterIndex, reader, length);
        }

        @Override
        public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
            preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
            preparedStatement.setAsciiStream(parameterIndex, x, length);
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
            preparedStatement.setBinaryStream(parameterIndex, x, length);
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
            preparedStatement.setCharacterStream(parameterIndex, reader, length);
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
            preparedStatement.setAsciiStream(parameterIndex, x);
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
            preparedStatement.setBinaryStream(parameterIndex, x);
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
            preparedStatement.setCharacterStream(parameterIndex, reader);
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
            preparedStatement.setNCharacterStream(parameterIndex, value);
        }

        @Override
        public void setClob(int parameterIndex, Reader reader) throws SQLException {
            preparedStatement.setClob(parameterIndex, reader);
        }

        @Override
        public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
            preparedStatement.setBlob(parameterIndex, inputStream);
        }

        @Override
        public void setNClob(int parameterIndex, Reader reader) throws SQLException {
            preparedStatement.setNClob(parameterIndex, reader);
        }

        @Override
        public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
            preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
        }

        @Override
        public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
            preparedStatement.setObject(parameterIndex, x, targetSqlType);
        }
*/
    }
}
