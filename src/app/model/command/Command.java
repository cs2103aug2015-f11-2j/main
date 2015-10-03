package app.model.command;

import java.time.LocalDateTime;

import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.Priority;
import app.model.ViewState;


public abstract class Command {

	private String commandString;
	private CommandType commandType;
	private String content;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Priority priority;
	private boolean isExecuted;

	public Command() {
		commandString = "";
		priority = Priority.NONE;
	}
	
	public abstract	ViewState execute(ViewState previousViewState);

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCommandString() {
		return commandString;
	}

	public void setCommandString(String commandString) {
		this.commandString = commandString;
	}

	public CommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(CommandType commandType) {
		this.commandType = commandType;
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
	
	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}
}
