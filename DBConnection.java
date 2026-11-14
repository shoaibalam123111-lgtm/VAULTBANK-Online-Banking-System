
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/vaultbank?serverTimezone=UTC";

    private static final String USER =
        System.getenv().getOrDefault("DB_USER", "root");

    private static final String PASSWORD =
        System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                "Database password is not configured."
            );
        }

        try {
            return DriverManager.getConnection(
                URL, USER, PASSWORD
            );

        } catch (SQLException e) {
            System.err.println("MySQL connection failed: " + e.getMessage());
            throw new SQLException(
                "Unable to connect to the database."
            );
        }
    }
}
