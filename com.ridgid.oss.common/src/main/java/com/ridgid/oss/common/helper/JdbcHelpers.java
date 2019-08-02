package com.ridgid.oss.common.helper;

import com.ridgid.oss.common.jdbc.NamedParameterJdbcQuery;

@SuppressWarnings("unused")
public final class JdbcHelpers {

    private JdbcHelpers() {
    }

    public static NamedParameterJdbcQuery parseQuery(String jdbcNamedParameterQuery) {
        return new NamedParameterJdbcQuery(jdbcNamedParameterQuery);
    }
}
