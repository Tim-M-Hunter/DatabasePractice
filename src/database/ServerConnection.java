package database;

import java.sql.*;

/**
 * Connect to a MySQL database.
 * @author thunter
 *
 */
public class ServerConnection {

	/** URL of the database */
	private String url;
	/** Username of access credentials */
	private String username;
	/** Password of access credentials */
	private String password;
	/** The SQL connection reference */
	private Connection connection;
	
	/**
	 * Set connection details.
	 * @param url URL of the database.
	 * @param username Username of access credentials.
	 * @param password Password of access credentials.
	 */
	public ServerConnection(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Attempt connection to the database.
	 * @return The SQL Connection reference generated.
	 */
	public Connection connect() {
		if(url != null && username != null && password != null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(url, username, password);
				connection.setAutoCommit(false);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Details for connection not set. Please set details before attempting connection.");
		}
		
		return connection;
	}
	
	/**
	 * Getter for SQL Connection reference.
	 * @return The SQL Connection reference.
	 */
	public Connection getConnection() {
		//If no connection made yet, assume it wants to connect. connect() method will make sure the details are provided to do so.
		if(connection == null) {
			connect();
		}
		return connection;
	}
	
	/**
	 * Disconnect the SQL Connection reference.
	 */
	public void disconnect() {
		if(connection != null ) {
			try {
				if(!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
