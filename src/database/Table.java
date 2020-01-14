package database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.annotations.Column;
import database.interfaces.Convertable_I;

/**
 * Used to create a object representation of a database table. 
 * <br/>
 * Part of this is based off of code from these blogs: 
 * <br/>
 * <a href="http://baseprogramming.com/blog1/2017/08/24/automating-jdbc-crud-operations-with-reflection/">Automating JDBC CRUD Operations with Reflection</a>
 * <br/>
 * <a href="https://dzone.com/articles/method-to-get-jdbc-result-set-into-given-generic-c">JDBC ResultSet and Generic Class List Using Reflection Annotations</a>
 * 
 * @author Tim Hunter
 *
 * @param <V> The class type of the object used for record creation.
 */
public class Table<V extends Record> {
	/** Class type of a record */
	private Class<V> classType;
	/** The database associated to this table */
	private Database database;
	/** The name of the database table */
	private String tableName;
	/** The name of the primary key for the table */
	private String primaryKeyName;
	
	/**
	 * Constructor.
	 * @param connection The connection to the database.
	 * @param metadata Metadata about the table, such as the table name and column names.
	 */
	public Table(Database database, Class<V> classType, String tableName, String primaryKeyName) {
		this.database = database;
		this.classType = classType;
		this.tableName = tableName;
		this.primaryKeyName = primaryKeyName;
	}
	
	/**
	 * Generically retrieve list of objects using database info.
	 * Based off of code source: https://stackoverflow.com/questions/8993188/mapping-a-row-from-a-sql-data-to-a-java-object
	 * @param <T> Generic reference of object being retrieved. No need to specify, will be determined by data mapping.
	 * @param rs The ResultSet of a database Connection query.
	 * @param converter Generic functional interface for a converter function via a lambda expression.
	 * @return A list of object specified in the converter function.
	 */
	private <T> List<T> getObjects(ResultSet rs, Convertable_I<T> converter) {
		List<T> list = new ArrayList<>();
		try {
			while(rs.next()) {
				list.add((T) converter.createObject(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Get a list of all records using a custom SQL string. Use carefully.
	 * @param sql The SQL commands to perform.
	 * @return List of matched records.
	 */
	private List<V> customQuery(String sql) {
		try {
			ResultSet resultSet = database.getConnection().createStatement().executeQuery(sql);
			
			return getObjects(resultSet, data -> mapToObject(data));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Generate an insert SQL call. <br/>
	 * Example Result: INSERT INTO person (firstName, lastName, age, active) VALUES (?, ?, ?, ?)
	 * @return The SQL statement.
	 */
	private String generateInsertSQL() {
		StringBuilder columnNames = new StringBuilder();
		StringBuilder vars = new StringBuilder();

		for(Field field : classType.getDeclaredFields()) {
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			if(columnNames.length() > 1) {
				columnNames.append(", ");
				vars.append(", ");
			}
			columnNames.append(column.value());
			vars.append("?");
		}
		
		String sql = "INSERT INTO " + tableName + " (" + columnNames.toString() + ") VALUES (" + vars.toString() + ")";
		
		System.out.println("Generated Insert Query:");
		System.out.println(sql);
		return sql;
	}
	
	/**
	 * Generate an update SQL call. <br/>
	 * Example Result: UPDATE person SET firstName = ?, lastName = ?, age = ?, alive = ? WHERE id = ?
	 * @return The SQL statement.
	 */
	private String generateUpdateSQL() {
		StringBuilder columnNames = new StringBuilder();

		for(Field field : classType.getDeclaredFields()) {
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			if(columnNames.length() > 1)
				columnNames.append(", ");
			columnNames.append(column.value()).append(" = ?");
		}
		
		String sql = "UPDATE " + tableName + " SET " + columnNames.toString() + " WHERE " + primaryKeyName + " = ?";
		
		System.out.println("Generated Update Query:");
		System.out.println(sql);
		return sql;
	}
	
	/**
	 * Defines how the given record should be mapped to the given PreparedStatement.
	 * @param record The Java object containing data.
	 * @param ps The PreparedStatement to map data into.
	 * @throws SQLException
	 */
	private void mapToQuery(Record record, PreparedStatement ps) throws SQLException {
		try {
			int count = 1;
			for(Field field : classType.getDeclaredFields()) {
				field.setAccessible(true);
				
				Column column = field.getAnnotation(Column.class);
				if(column == null) {
					continue;
				}
				
				ps.setObject(count, field.get(record));
				count++;
			}
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
	};
	
	/**
	 * Defines how the given data should be mapped to a Java object.
	 * @param data The data from a query.
	 * @return The Java object.
	 * @throws SQLException 
	 */
	private V mapToObject(ResultSet data) throws SQLException {
		try {
			V instance = classType.getConstructor(Table.class).newInstance(this);
			instance.setPrimaryKey(data.getObject(primaryKeyName));
			for(Field field : classType.getDeclaredFields()) {
				field.setAccessible(true);
				
				Column column = field.getAnnotation(Column.class);
				if(column == null) {
					continue;
				}
				
				Object value = data.getObject(column.value());
				Class<?> type = field.getType();
				
				if(isPrimitive(type)) {
					Class<?> boxed = boxPrimitiveClass(type);
					value = boxed.cast(value);
				}
				
				field.set(instance, value);
			}

			return instance;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException	| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	};
	
	/**
	 * Used to determine if a class is a primitive type.
	 * @param type The class to test.
	 * @return True if a primitive, false otherwise.
	 */
	private boolean isPrimitive(Class<?> type)
	{
		 return (type==int.class || type==long.class ||
		         type==double.class  || type==float.class
		        || type==boolean.class || type==byte.class
		        || type==char.class || type==short.class);
	}
	
	
	/**
	 * Used to convert a primitive to its object equivalent.
	 * @param type The class to convert.
	 * @return The converted class.
	 */
	private Class<?> boxPrimitiveClass(Class<?> type)
	{
		 if(type==int.class){return Integer.class;}
		 else if(type==long.class){return Long.class;}
		 else if (type==double.class){return Double.class;}
		 else if(type==float.class){return Float.class;}
		 else if(type==boolean.class){return Boolean.class;}
		 else if(type==byte.class){return Byte.class;}
		 else if(type==char.class){return Character.class;}
		 else if(type==short.class){return Short.class;}
		 else
		 {
		     String string="class '" + type.getName() + "' is not a primitive";
		     throw new IllegalArgumentException(string);
		 }
	 }
	
	
	/**
	 * Getter for the database object associated to this table.
	 * @return The database object.
	 */
	public Database getDatabase() {
		return database;
	}
	
	/**
	 * Getter for the table name.
	 * @return The table name in the database.
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Getter for the primary key name.
	 * @return The primary key name in the database.
	 */
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}
	
	/**
	 * Get a list of all records contained in the table.
	 * @return A list of all records in the table.
	 */
	public List<V> getAll() {
		String query = "Select * From " + tableName;
		return customQuery(query);
	}
	
	/**
	 * Get a list of all records with a given condition applied after WHERE keyword.
	 * @param condition The condition to append after a WHERE keyword.
	 * @return A list of all records that match parameters.
	 */
	public List<V> getByCondition(String condition) {		
		String query = "Select * From " + tableName + " Where " + condition;
		return customQuery(query);
	}
	
	/**
	 * Get the first result of a given condition applied after WHERE keyword.
	 * @param condition The condition to append after a WHERE keyword.
	 * @return The first found match if located, null otherwise.
	 */
	public V getFirstByCondition(String condition) {		
		String query = "Select * From " + tableName + " Where " + condition;
		List<V> result = customQuery(query);
		if(!result.isEmpty()) {
			return result.get(0);
		} else {
			System.out.println("Search for " + tableName + " using condition '" + condition + "' could not be found.");
			return null;
		}
	}
	
	/**
	 * Perform insert on record.
	 * @param record The record to insert.
	 */
	public void insert(Record record) {
		try {
			String sql = generateInsertSQL();
			
			PreparedStatement ps = database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			mapToQuery(record, ps);
			
			int result = ps.executeUpdate();
			if(result == 1) {
				database.getConnection().commit();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				record.setPrimaryKey(rs.getObject(1));
			} else {
				database.getConnection().rollback();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Perform a batch insert of the given records.
	 * @param records The records to insert.
	 * @return List of successfully added records.
	 */
	public void insert(List<Record> records) {
		try {
			String sql = generateInsertSQL();
			
			PreparedStatement ps = database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for(Record record : records) {
				mapToQuery(record, ps);
				ps.addBatch();
				ps.clearParameters();
			}
			
			ps.executeBatch();

			ResultSet rs = ps.getGeneratedKeys();
			int count = 0;
			while(rs.next()) {
				Object key = rs.getObject(1);
				records.get(count).setPrimaryKey(key);
				count++;
			}
			database.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Perform update on record.
	 * @param record The record to update.
	 */
 	public void update(Record record) {
 		if(record.getPrimaryKey() == null) {
 			System.out.println("Could not update a given record since it doesn't have a primary key to reference:");
			System.out.println(this.toString());
			return;
		}
 		
		try {
			String sql = generateUpdateSQL();
			
			PreparedStatement ps = database.getConnection().prepareStatement(sql);
			mapToQuery(record, ps);
			//Set Primary Key
			ps.setObject(ps.getParameterMetaData().getParameterCount(), record.getPrimaryKey());
			
			int result = ps.executeUpdate();
			if(result == 1) {
				database.getConnection().commit();
			} else {
				database.getConnection().rollback();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Perform deletion of record.
	 * @param primaryKeyValue The value of the primary key to identify the record.
	 * @return True if only one record is returned, false otherwise. Rolls back change if false.
	 */
	public boolean delete(Object primaryKeyValue) {
		try {
			int result = database.getConnection().createStatement().executeUpdate("DELETE FROM " + tableName + " Where " + primaryKeyName + " = " + primaryKeyValue);
			
			if (result == 1) {
				database.getConnection().commit();
				System.out.println("Deleted record from " + tableName + " with Primary Key value " + primaryKeyValue);
				return true;
			} else {
				database.getConnection().rollback();
				System.out.println("Failed to delete record from " + tableName + " with Primary Key value " + primaryKeyValue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	};
	
}
