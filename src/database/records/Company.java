package database.records;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Record;
import database.Table;
import database.annotations.Column;

public class Company extends Record {
	
	public Company(Table<? extends Record> table) {
		super(table);
	}

	@Column("name")
	public String name;
	
	private List<Job> jobs;
	private List<Employee> employees;
	
	public List<Job> getJobs(boolean forceDataImport) {
		if(jobs == null || forceDataImport) {
			jobs = super.getRelatedRecords(Job.class, "company_id");
		}
		return jobs;
	}
	
	public List<Job> getJobs() {
		return getJobs(false);
	}
	
	public List<Employee> getEmployees(boolean forceDataImport) {
		if(employees == null || forceDataImport) {
			employees = super.getRelatedRecords(Employee.class, "company_id");
		}
		return employees;
	}
	
	public List<Employee> getEmployees() {
		return employees;
	}
	
	@Override
	public String toString() {
		Map<String, Object> values = new HashMap<>();
		values.put("Name", name);
		return print(values);
	}
}
