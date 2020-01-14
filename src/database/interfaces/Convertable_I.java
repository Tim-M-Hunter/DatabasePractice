package database.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Generic functional interface for use in lambda expression to map data.
 * <br/>
 * Based off of code source: <a href="https://stackoverflow.com/questions/8993188/mapping-a-row-from-a-sql-data-to-a-java-object">Mapping A Row From A SQL DATA To A Java Object</a>
 * @author thunter
 *
 * @param <T>
 */
public interface Convertable_I<T> {

	/**
	 * Generically create a Java Object using the given ResultSet data.
	 * @param rs The ResultSet data from a query.
	 * @return A Java Object built using the given data.
	 * @throws SQLException
	 */
	public T createObject(ResultSet rs) throws SQLException;
	
}
