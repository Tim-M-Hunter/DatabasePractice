package database;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Database accessor for easy referencing.
 * @author thunter
 *
 */
public class Database {
	
	/** Storage for the created database for easy retrieval */
	private static Map<Object, Database> databaseMap;
	/** The database connection, most likely from the ServerConnection class */
	private Connection connection;
	/** The name of the database */
	private Object databaseIdentifier;
	/** Storage for the created tables */
	private Map<Class<?>, Table<?>> tableMap;
	/** A default name for table PKs to reduce parameter inputs */
	private String defaultPrimaryKeyName = "id";
	
	/**
	 * Perform initial setup of the Database object. Used instead of a constructor.
	 * @param databaseIdentifier A unique identifier to locate the database in a map.
	 * @param connection A database connection.
	 */
	public Database(Object databaseIdentifier, Connection connection) {
		if(databaseMap == null) {
			databaseMap = new HashMap<>();
		}
		databaseMap.put(databaseIdentifier, this);
		this.databaseIdentifier = databaseIdentifier;
		this.tableMap = new HashMap<>();
		this.connection = connection;
	}
	
	/**
	 * Get the database associated to the given name.
	 * @param databaseIdentifier The identifier of the database to retrieve.
	 * @return The database if found, null otherwise.
	 */
	public static Database getDatabase(Object databaseIdentifier) {
		if(databaseMap != null) {
			return databaseMap.get(databaseIdentifier);
		} else {
			System.out.println("No databases have been created yet.");
			return null;
		}
		
	}
	
	/**
	 * Getter for the database identifier associated to this object.
	 * @return The database identifier.
	 */
	public Object getDatabaseIdentifier() {
		return databaseIdentifier;
	}
	
	/**
	 * Getter for the database connection associated to this object.
	 * @return The database connection.
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Set a default primary key name which will be used to find a table's primary key if one is not provided when adding a table.
	 * @param primaryKeyName The default name for a primary key.
	 */
	public void setDefaultPrimaryKeyName(String primaryKeyName) {
		defaultPrimaryKeyName = primaryKeyName;
	}
	
	/**
	 * Create and add a Table based on the given class type to an internal map. Given class type must extend the Record abstract class.
	 * @param <V> A class that extends the Record abstract class.
	 * @param classType A class that extends the Record abstract class.
	 * @param tableName The name of the table in the database.
	 * @param primaryKeyName The name of the primary key for the table in the database.
	 * @return The created Table object.
	 */
	public <V extends Record> Table<V> createTable(Class<V> classType, String tableName, String primaryKeyName) {
		Table<V> table = new Table<>(this, classType, tableName, primaryKeyName);
		tableMap.put(classType, table);
		return table;
	}
	
	/**
	 * Create and add a Table based on the given class type to an internal map. Given class type must extend the Record abstract class.
	 * <br/> Uses the set default primary key name. The starting name is 'id', but can be changed with the setDefaultPrimaryKeyName() function.
	 * @param <V> A class that extends the Record abstract class.
	 * @param classType A class that extends the Record abstract class.
	 * @param tableName The name of the table in the database.
	 * @return The created Table object.
	 */
	public <V extends Record> Table<V> createTable(Class<V> classType, String tableName) {
		return createTable(classType, tableName, defaultPrimaryKeyName);
	}
	
	/**
	 * Add a Table based on the given class type to an internal map. Given class type must extend the Record abstract class.
	 * @param <V> A class that extends the Record abstract class.
	 * @param classType A class that extends the Record abstract class.
	 * @param table A Table object with the same class type as the given class type.
	 */
	public <V extends Record> void addTable(Class<V> classType, Table<V> table) {
		tableMap.put(classType, table);
	}
	
	/**
	 * Get a added Table which uses the given class type from the internal map.
	 * @param <V> A class that extends the Record abstract class.
	 * @param classType A class that extends the Record abstract class.
	 * @return The requested Table object.
	 */
	@SuppressWarnings("unchecked")
	public <V extends Record> Table<V> getTable(Class<V> classType) {
		return (Table<V>) tableMap.get(classType);
	}
	
	/**
	 * Remove a Table with the given class type from the internal map.
	 * @param <V> A class that extends the Record abstract class.
	 * @param classType A class that extends the Record abstract class.
	 */
	public <V extends Record> void removeTable(Class<V> classType) {
		tableMap.remove(classType);
	}
}
