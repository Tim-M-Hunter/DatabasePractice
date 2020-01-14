package database;

import java.util.List;
import java.util.Map;

/**
 * Extensible abstract that all Java objects which will hold the data from record rows must extend.
 * @author Tim Hunter
 */
public abstract class Record {

	/** Holds a reference to an associated table */
	private Table<? extends Record> table;
	
	/** Holds the primary key of the record */
	private Object primaryKey;
	
	/**
	 * Setter for a table associated to the record.
	 * @param table The table to associate to the record.
	 */
	public Record(Table<? extends Record> table) {
		this.table = table;
	}
	
	/**
	 * Getter for the table associated to the record.
	 * @return The table associated to the record.
	 */
	public Table<? extends Record> getTable() {
		return table;
	}
	
	/**
	 * Setter for primary key.
	 * @param primaryKey The primary key of the record.
	 */
	public void setPrimaryKey(Object primaryKey) {
		this.primaryKey = primaryKey;
	}
	 /**
	  * Getter for primary key.
	  * @return The primary key of the record.
	  */
	public Object getPrimaryKey() {
		return primaryKey;
	}
	
	/**
	 * Insert this record into the database.
	 */
	public void insert() {
		table.insert(this);
	}
	
	/**
	 * Update this record in the database.
	 */
	public void update() {
		table.update(this);
	}
	
	/**
	 * Retrieve a related record of given type that has a primary key matching the given foreign key value.
	 * @param <V> Generic class that extends a record.
	 * @param databaseName The name of the database to retrieve the record from.
	 * @param classType Reference to the class for table search.
	 * @param foreignKeyValue The value of the primary key for the record. What is the value of the identifier for the record you want?
	 * @return The found record.
	 */
	public <V extends Record> V getRelatedRecord(Class<V> classType, Object foreignKeyValue) {
		Table<V> otherTable = table.getDatabase().getTable(classType);
		return otherTable.getFirstByCondition(otherTable.getPrimaryKeyName() + " = " + foreignKeyValue);
	}
	
	/**
	 * Retrieve all related records of given type that have a foreign key matching this records primary key.
	 * @param <V> Generic class that extends a record.
	 * @param databaseName The name of the database to retrieve the records from.
	 * @param classType Reference to the class for table search.
	 * @param foreignKeyName The name of the foreign key column in the records. What name does the other record have for this record's identifier?
	 * @return The found records.
	 */
	public <V extends Record> List<V> getRelatedRecords(Class<V> classType, String foreignKeyName) {
		Table<V> otherTable = table.getDatabase().getTable(classType);
		return otherTable.getByCondition(foreignKeyName + " = " + getPrimaryKey());
	}
	
	protected String print(Map<String, Object> values) {
		StringBuilder sb = new StringBuilder();
		sb.append(" | ").append(table.getPrimaryKeyName()).append(": ").append(getPrimaryKey()).append(" | ");
		for(String key : values.keySet()) {
			sb.append(key).append(": ").append(values.get(key)).append(" | ");
		}
		return sb.toString();
	}
}
