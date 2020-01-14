package database.records;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Record;
import database.Table;
import database.annotations.Column;

public class Person extends Record {

	public Person(Table<? extends Record> table) {
		super(table);
	}

	@Column("firstName")
	public String firstName;
	@Column("lastName")
	public String lastName;
	@Column("alive")
	public boolean alive;
	@Column("age")
	public int age;
	
	private List<Employee> employment;
	
	public List<Employee> getEmployment(boolean forceDataImport) {
		if(employment == null || forceDataImport) {
			employment = super.getRelatedRecords(Employee.class, "person_id");
		}
		return employment;
	}
	
	public List<Employee> getEmployment(){
		return getEmployment(false);
	}
	
	@Override
	public String toString() {
		Map<String, Object> values = new HashMap<>();
		values.put("FirstName", firstName);
		values.put("LastName", lastName);
		values.put("Alive", alive);
		values.put("Age", age);
		return print(values);
	}
}
