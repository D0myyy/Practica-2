package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("SELECT 1");
        } catch (SQLException exception) {
            throw new IllegalStateException("Eroare la initializarea conexiunii cu baza de date.", exception);
        }
    }
}
