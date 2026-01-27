package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // Database reale (default)
    private static String URL = "jdbc:mysql://localhost:3306/bookmarker";
    private static String USER = "root";
    private static String PASSWORD = "password"; // Metti la tua
    private static String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Database testing
    public static void setConnectionConfig(String url, String user, String password, String driver) {
        URL = url;
        USER = user;
        PASSWORD = password;
        DRIVER = driver;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver non trovato", e);
        }
    }
}