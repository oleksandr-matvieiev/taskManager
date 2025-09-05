package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:taskmanager.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to DB", e);
        }
    }

    public static void createTables() {
        String[] sqlStatements = {
                "CREATE TABLE IF NOT EXISTS tasks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT NOT NULL," +
                        "description TEXT," +
                        "endDate DATE," +
                        "repeatIntervalDays INTEGER," +
                        "status TEXT," +
                        "tag_id INTEGER," +
                        "FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE SET NULL" +
                        ");",
                "CREATE TABLE IF NOT EXISTS tags (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL UNIQUE" +
                        ");",
                "INSERT INTO tags (id, name) VALUES (1, 'Uncategorized')" +
                        "ON CONFLICT(id) DO NOTHING;"
        };

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            for (String s : sqlStatements) {
                stmt.executeUpdate(s);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }
}
