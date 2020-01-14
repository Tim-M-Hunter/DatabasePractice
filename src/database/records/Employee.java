package database.records;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Record;
import database.Table;
import database.annotations.Column;

public class Employee extends Record {

	public Employee(Table<? extends Record> table) {
		super(table);
	}

	@Column("active")
	public boolean active;
	@Column("startDate")
	public Date startDate;
	@Column("endDate")
	public Date endDate;
	@Column("person_id")
	public long personId;
	@Column("company_id")
	public long companyId;
	
	private Person person;
	private Company company;
	private List<JobAssignment> jobAssignments;
	
	public Person getPerson(boolean forceDataImport) {
		if(person == null || forceDataImport) {
			person = super.getRelatedRecord(Person.class, personId);
		}
		return person;
	}
	
	public Person getPerson() {
		return getPerson(false);
	}
	
	public Company getCompany(boolean forceDataImport) {
		if(company == null || forceDataImport) {
			company = super.getRelatedRecord(Company.class, companyId);
		}
		return company;
	}
	
	public Company getCompany() {
		return getCompany(false);
	}
	
	public List<JobAssignment> getJobAssignments(boolean forceDataImport) {
		if(jobAssignments == null || forceDataImport) {
			jobAssignments = super.getRelatedRecords(JobAssignment.class, "employee_id");
		}
		return jobAssignments;
	}
	
	public List<JobAssignment> getJobAssignments() {
		return getJobAssignments(false);
	}
	
	@Override
	public String toString() {
		Map<String, Object> values = new HashMap<>();
		values.put("Active", active);
		values.put("StartDate", startDate);
		values.put("EndDate", endDate);
		values.put("PersonId", personId);
		values.put("CompanyId", companyId);
		return print(values);
	}
}
