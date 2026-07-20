package com.ndumiso.bursarymatch.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBase {

    private Connection conn;

    public DataBase() throws SQLException {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                "Missing DB_URL / DB_USER / DB_PASSWORD environment variables. "
                + "See README for setup instructions.");
        }

        conn = DriverManager.getConnection(url, user, password);
    }

    /**
     * Runs an INSERT/UPDATE/DELETE statement.
     */
    public int update(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeUpdate();
        }
    }

    /**
     * Runs a SELECT statement and returns the raw ResultSet.
     * Caller is responsible for closing it.
     */
    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    /**
     * Converts an entire ResultSet into a simple readable String,
     * matching the Phase 2 processResultSet() method.
     */
    public String processResultSet(ResultSet resultSet) throws SQLException {
        StringBuilder sb = new StringBuilder();
        int columnCount = resultSet.getMetaData().getColumnCount();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                sb.append(resultSet.getString(i));
                if (i < columnCount) {
                    sb.append(" | ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
