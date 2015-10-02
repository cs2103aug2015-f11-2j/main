package app.model;

import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;

public class ViewState {
	private String header;
	private String statusMessage;
	private StatusType statusType;
	private ViewType activeView;
	private TaskList taskList;
	private String textArea;
	private String theme;
	
	public void mergeWith(ViewState newState) {
		if (newState.getHeader() != null) {
			header = newState.getHeader();
		}
		if (newState.getStatusMessage() != null) {
			statusMessage = newState.getStatusMessage();
		}
		if (newState.getStatusType() != null) {
			statusType = newState.getStatusType();
		}
		if (newState.getActiveView() != null) {
			activeView = newState.getActiveView();
		}
		if (newState.getTaskList() != null) {
			taskList = newState.getTaskList();
		}
		if (newState.getTextArea() != null) {
			textArea = newState.getTextArea();
		}
		if (newState.getTheme() != null) {
			theme = newState.getTheme();
		}
	}

	public void setStatus(String message) {
		statusMessage = message;
		statusType = StatusType.INFO;
	}

	public void setStatus(StatusType type, String message) {
		statusMessage = message;
		statusType = type;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public ViewType getActiveView() {
		return activeView;
	}

	public void setActiveView(ViewType activeView) {
		this.activeView = activeView;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskList taskList) {
		// Make sure we don't reference an object that exists somewhere else and
		// may be modified
		this.taskList = new TaskList(taskList);
	}

	public String getTextArea() {
		return textArea;
	}

	public void setTextArea(String textArea) {
		this.textArea = textArea;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

}
