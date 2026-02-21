import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:attendance.db";

    static {
        try {
            Class<?> aClass = Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id TEXT NOT NULL," +
                    "date TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "subject TEXT NOT NULL)");

            stmt.execute("INSERT OR IGNORE INTO users (username, password, role) VALUES " +
                    "('admin', 'admin123', 'admin')");

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}