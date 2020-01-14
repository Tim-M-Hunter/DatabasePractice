package database.records;

import java.util.HashMap;
import java.util.Map;

import database.Record;
import database.Table;
import database.annotations.Column;

public class JobTask extends Record {
	public JobTask(Table<? extends Record> table) {
		super(table);
	}

	@Column("name")
	public String name;
	@Column("description")
	public String description;
	@Column("durationInHours")
	public double durationInHours;
	@Column("job_assignment_id")
	public long jobAssignmentId;
	
	private JobAssignment jobAssignment;
	
	public JobAssignment getJobAssignment(boolean forceDataImport) {
		if(jobAssignment == null || forceDataImport) {
			jobAssignment = super.getRelatedRecord(JobAssignment.class, jobAssignmentId);
		}
		return jobAssignment;
	}
	
	public JobAssignment getJobAssignment() {
		return getJobAssignment(false);
	}
	
	@Override
	public String toString() {
		Map<String, Object> values = new HashMap<>();
		values.put("Name", name);
		values.put("Description", description);
		values.put("DurationInHours", durationInHours);
		values.put("JobAssignmentId", jobAssignmentId);
		return print(values);
	}
}
