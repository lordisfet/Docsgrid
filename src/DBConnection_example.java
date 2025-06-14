import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection_example {
    private static final String URL = "jdbc:postgresql://localhost:your_port/your_db";
    private static final String USER = "your_user";
    private static final String PASSWORD = "user_password";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
}
