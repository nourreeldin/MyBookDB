import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private DBConnection() {}

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("\u001B[32m[+]\u001B[0m Database connection established");
            }
        } catch (SQLException e) {
            System.err.println("\u001B[31m[-]\u001B[0m Failed to connect to the database.");
            System.err.println("\u001B[31m[-]\u001B[0m URL: " + URL);
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("\u001B[33m[!]\u001B[0m Database connection closed.");
            } catch (SQLException e) {
                System.err.println("\u001B[31m[-]\u001B[0m Error closing the connection.");
                e.printStackTrace();
            }
        }
    }
}