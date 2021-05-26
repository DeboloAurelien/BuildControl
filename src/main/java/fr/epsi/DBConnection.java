package fr.epsi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final DBConnection INSTANCE = new DBConnection();
    private static final String url = "jdbc:sqlserver://localhost:1433;database=build_control;integratedSecurity=true";

    public DBConnection() {}

    public Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }

    public Connection getConnection() throws SQLException {
        return this.getConnection(url);
    }

    public static DBConnection getInstance() {
        return INSTANCE;
    }
}
