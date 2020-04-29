package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
	private static final String DATABASE_NAME = "SpaceWar";
	private static final String CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String URL = "jdbc:sqlserver://localhost:1433; databaseName=" + DATABASE_NAME;
	private static final String USER = "sa";
	private static final String PASSWORD = "spacewar";
	
	private static DatabaseHandler dh = null;
	private static Connection conn = null;
	
	public DatabaseHandler() {
		createConnection();
	}
	
	private void createConnection() {
		try {
			Class.forName(CLASS_NAME);
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("success");
		} catch (ClassNotFoundException e) {
			System.out.println("Class name error");
		} catch (SQLException e) {
			System.out.println("Get connection failed");
		}
	}
	
	public Connection getConnection() {
		if (conn == null) createConnection();
		
		return conn;
	}
	
	public static DatabaseHandler getInstance() {
		if (dh == null) dh = new DatabaseHandler();
		
		return dh;
	}
}
