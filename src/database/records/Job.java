package database.records;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Record;
import database.Table;
import database.annotations.Column;

public class Job extends Record {
	
	public Job(Table<? extends Record> table) {
		super(table);
	}

	@Column("title")
	public String title;
	@Column("description")
	public String description;
	@Column("company_id")
	public long companyId;
	
	private Company company;
	private List<JobAssignment> jobAssignments;
	
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
			jobAssignments = super.getRelatedRecords(JobAssignment.class, "job_id");
		}
		return jobAssignments;
	}
	
	public List<JobAssignment> getJobAssignments() {
		return getJobAssignments(false);
	}
	
	@Override
	public String toString() {
		Map<String, Object> values = new HashMap<>();
		values.put("Title", title);
		values.put("Description", description);
		values.put("CompanyId", companyId);
		return print(values);
	}
}
