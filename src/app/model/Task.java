package app.model;

import java.util.Date;

public class Task {
	private String id;
	private String name;
	private Date startDate;
	private Date endDate;
	private boolean isCompleted;
	
	public Task(Command cmd) {
		name = cmd.getTaskName();
		startDate = cmd.getStartDate();
		endDate = cmd.getEndDate();
		isCompleted = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
}
