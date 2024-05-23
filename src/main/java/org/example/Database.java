package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:taskmanager.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase() {

        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                System.out.println("DB created!!!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks( \n" +
                "id integer PRIMARY KEY,\n" +
                "title text not null,\n" +
                "description text,\n " +
                "endDate text,\n" +
                "status text\n" +
                ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createNewDatabase();
        createNewTable();
    }


}
