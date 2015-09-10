package app.model.command;

import java.util.Date;

import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.Priority;
import app.constants.ViewConstants.StatusType;


public abstract class Command {

	private String commandString;
	private CommandType commandType;
	private String content;
	private Date startDate;
	private Date endDate;
	private Priority priority;
	private String feedback;
	private StatusType statusType;

	public Command() {
		commandString = "";
		feedback = "";
		statusType = StatusType.INFO;
		priority = Priority.NONE;
	}
	
	public Command(CommandType type) {
		this();
		this.commandType = type;
	}
	
	public abstract	void execute();

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

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	public StatusType getStatusType() {
		return statusType;
	}

	public void setStatusType(StatusType statusType) {
		this.statusType = statusType;
	}

}
