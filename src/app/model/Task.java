package app.model;

import java.time.LocalDateTime;
import java.util.UUID;

import app.constants.TaskConstants.Priority;
import app.model.command.Command;

public class Task {
	private UUID id;
	private String name;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Priority priority;

	private boolean isCompleted;
	
	public Task(Command cmd) {
		id = UUID.randomUUID();
		name = cmd.getContent();
		startDate = cmd.getStartDate();
		endDate = cmd.getEndDate();
		priority = cmd.getPriority();
		isCompleted = false;
	}

	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	public LocalDateTime getSortKey() {
		if (getStartDate() == null && getEndDate() != null) {
			return getEndDate();
		} else if (getStartDate() != null && getEndDate() != null) {
			return getStartDate();
		}
		return null;
	}
}
