package app.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import app.model.Task.Priority;

public class Command {

	private String commandString;
	private String commandWord;
	private String taskName;
	private Date startDate;
	private Date endDate;
	private Priority priority;
	private String feedback;
	
	public static final List<String> ALIASES_ADD = getUnmodifiableList("add", "a");
	public static final List<String> ALIASES_REMOVE = getUnmodifiableList("remove", "delete", "rm");
	public static final List<String> ALIASES_HELP = getUnmodifiableList("help", "?");
	
	public enum CommandType {
		ADD, REMOVE, UPDATE, SEARCH, EXIT, HELP;
	}
	
	private static List<String> getUnmodifiableList(String... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}

	public Command(String commandString) {
		this.commandString = commandString;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getCommandString() {
		return commandString;
	}

	public void setCommandString(String commandString) {
		this.commandString = commandString;
	}

	public String getCommandWord() {
		return commandWord;
	}

	public void setCommandWord(String commandWord) {
		this.commandWord = commandWord;
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

}
