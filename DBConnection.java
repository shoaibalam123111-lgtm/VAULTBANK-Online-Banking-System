import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.cj.jdbc.Driver;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/vaultbank";

    private static final String USER = "root";

    private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";

    public static Connection getConnection() throws SQLException {

        try {
            Driver driver = new Driver();

            Properties properties = new Properties();
            properties.setProperty("user", USER);
            properties.setProperty("password", PASSWORD);

            return driver.connect(URL, properties);

        } catch (Exception e) {
            throw new SQLException("MySQL Connection Error: " + e.getMessage(), e);
        }
    }
}