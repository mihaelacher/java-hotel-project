package Core;
import java.sql.Connection;
import java.sql.DriverAction;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	static Connection conn=null;
	
	public static Connection getConnection() {
		
		try {
			Class.forName("org.h2.Driver");
			conn=DriverManager.getConnection("jdbc:h2:tcp://localhost/C:\\Users\\mihae\\OneDrive\\Desktop\\HotelDB\\HotelDB", "sa", "12345");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
	}

}