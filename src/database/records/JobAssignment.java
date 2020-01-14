package database.records;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Record;
import database.Table;
import database.annotations.Column;

/**
 * Link between Employee and Job. Can keep track of duration spent on a particular Job. 
 * @author thunter
 */
public class JobAssignment extends Record {

	public JobAssignment(Table<? extends Record> table) {
		super(table);
	}

	@Column("active")
	public boolean active;
	@Column("startDate")
	public Date startDate;
	@Column("endDate")
	public Date endDate;
	@Column("startingWage")
	public double startingWage;
	@Column("currentWage")
	public double currentWage;
	@Column("job_id")
	public long jobId;
	@Column("employee_id")
	public long employeeId;
	
	private Job job;
	private Employee employee;
	private List<JobTask> jobTasks;
	
	public Job getJob(boolean forceDataImport) {
		if(job == null || forceDataImport) {
			job = super.getRelatedRecord(Job.class, jobId);
		}
		return job;
	}
	
	public Job getJob() {
		return getJob(false);
	}
	
	public Employee getEmployee(boolean forceDataImport) {
		if(employee == null || forceDataImport) {
			employee = super.getRelatedRecord(Employee.class, employeeId);
		}
		return employee;
	}
	
	public Employee getEmployee() {
		return getEmployee(false);
	}
	
	public List<JobTask> getJobTasks(boolean forceDataImport) {
		if(jobTasks == null || forceDataImport) {
			jobTasks = super.getRelatedRecords(JobTask.class, "job_assignment_id");
		}
		return jobTasks;
	}
	
	public List<JobTask> getJobTasks() {
		return getJobTasks(false);
	}
	
	@Override
	public String toString() {
		Map<String, Object> values = new HashMap<>();
		values.put("Active", active);
		values.put("StartDate", startDate);
		values.put("EndDate", endDate);
		values.put("StartingWage", startingWage);
		values.put("CurrentWage", currentWage);
		values.put("JobId", jobId);
		values.put("EmployeeId", employeeId);
		return print(values);
	}
}
