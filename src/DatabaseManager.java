import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {

   private static final String JDBC_URL =
         "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
   private static final String JDBC_USER = "myuser";
   private static final String JDBC_PASSWORD = "xxxx";

   private DatabaseManager() {
   }

   public static Connection getConnection() throws SQLException {
      return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
   }
}
