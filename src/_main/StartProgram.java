package _main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import database.Database;
import database.ServerConnection;
import database.Table;
import database.records.*;

public class StartProgram {

	private static final String url = "jdbc:mysql://localhost:3306/practice1";
	private static final String username = "PracticeUser";
	private static final String password = "password";
	
	private static List<ServerConnection> serverConnections = new ArrayList<>();;
	
	public static void main(String[] args) {
		setupProgram();
		
		//testConnection();
		testInsert();
		//testUpdate(1);
		
		finishProgram();
	}
	
	
	/**
	 * Perform initial actions before running the program.
	 */
	private static void setupProgram() {		
		ServerConnection servConn = new ServerConnection(url, username, password);
		serverConnections.add(servConn);
		
		Database practice = new Database("practice1", servConn.getConnection());
		practice.setDefaultPrimaryKeyName("id");
		practice.createTable(Person.class, "person");
		practice.createTable(Employee.class, "employee");
		practice.createTable(Company.class, "company");
		practice.createTable(Job.class, "job");
		practice.createTable(JobAssignment.class, "job_assignment");
		practice.createTable(JobTask.class, "job_task");
	}
	
	/**
	 * Perform final actions before finishing the program.
	 */
	private static void finishProgram() {
		for(ServerConnection servConn : serverConnections) {
			servConn.disconnect();
		}
	}
	
	private static void testConnection() {
		for(Person p : Database.getDatabase("practice1").getTable(Person.class).getAll()) {
			System.out.println(p);
		}
	}
	
	
	private static void testInsert() {
		Table<Person> personTable = Database.getDatabase("practice1").getTable(Person.class);
		Person p = new Person(personTable);
		
		p.firstName = "Bill";
		p.lastName = "Doe";
		p.age = 45;
		p.alive = true;
		
		p.insert();
		
		System.out.println("Inserted Record: ");
		System.out.println(p);
	}
	
	private static void testInsert2() {
		Database db = Database.getDatabase("practice1");
		Table<Company> companyTable = db.getTable(Company.class);
		Table<Employee> employeeTable = db.getTable(Employee.class);
		Table<Job> jobTable = db.getTable(Job.class);
		Table<JobAssignment> jobAssignmentTable = db.getTable(JobAssignment.class);
		Table<JobTask> jobTaskTable = db.getTable(JobTask.class);
		Table<Person> personTable = db.getTable(Person.class);
		
		Calendar cal = Calendar.getInstance();
		
		Person p1 = new Person(personTable);
		p1.firstName = "John";
		p1.lastName = "Doe";
		p1.age = 20;
		p1.alive = true;
		p1.insert();
		
		Person p2 = new Person(personTable);
		p2.firstName = "Jane";
		p2.lastName = "Doe";
		p2.age = 32;
		p2.alive = true;
		p2.insert();
		
		Company c = new Company(companyTable);
		c.name = "Green Hills Foodery";
		c.insert();
		
		Employee e1 = new Employee(employeeTable);
		e1.active = true;
		cal.add(Calendar.MONTH, -3);
		e1.startDate = cal.getTime();
		e1.personId = (long) p1.getPrimaryKey();
		e1.companyId = (long) c.getPrimaryKey();
		e1.insert();
		
		Employee e2 = new Employee(employeeTable);
		e2.active = false;
		cal.add(Calendar.MONTH, -9);
		e2.startDate = cal.getTime();
		cal.add(Calendar.MONTH, 5);
		e2.endDate = cal.getTime();
		e2.personId = (long) p2.getPrimaryKey();
		e2.companyId = (long) c.getPrimaryKey();
		e2.insert();
		
		Job j1 = new Job(jobTable);
		j1.title = "Manager";
		j1.description = "One of the boss people.";
		j1.companyId = (long) c.getPrimaryKey();
		j1.insert();
		
		Job j2 = new Job(jobTable);
		j2.title = "Peon";
		j2.description = "A worker drone. Overtime is mandatory.";
		j2.companyId = (long) c.getPrimaryKey();
		j2.insert();
		
		JobAssignment ja1 = new JobAssignment(jobAssignmentTable);
	}
	
	private static void testUpdate(int id) {
		Table<Person> personTable = Database.getDatabase("practice1").getTable(Person.class);
		Person p = personTable.getFirstByCondition("id = " + id);
		
		p.firstName = "Lilly";
		p.lastName = "Roth";
		p.age = 20;
		p.alive = true;
		
		p.update();
		
		System.out.println("Updated Record:");
		System.out.println(p);
	}
	
}
