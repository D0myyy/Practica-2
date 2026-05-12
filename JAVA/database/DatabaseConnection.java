package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:sqlserver://localhost:1433;" +
            "databaseName=Rezervare_bilete_avion;" +
            "integratedSecurity=true;" +
            "encrypt=true;" +
            "trustServerCertificate=true";

    public static Connection getConnection() {
        try {
            String user = System.getenv("SQLSERVER_USER");
            String password = System.getenv("SQLSERVER_PASSWORD");
            if (user != null && !user.isBlank()) {
                String url = "jdbc:sqlserver://localhost:1433;databaseName=Rezervare_bilete_avion;encrypt=true;trustServerCertificate=true";
                return DriverManager.getConnection(url, user, password == null ? "" : password);
            }
            return DriverManager.getConnection(DEFAULT_URL);
        } catch (SQLException exception) {
            throw new IllegalStateException("Nu s-a putut realiza conectarea la baza de date.", exception);
        }
    }
}
