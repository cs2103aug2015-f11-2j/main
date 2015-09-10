package app.controller;

import app.helper.CommandParser;
import app.model.TaskList;
import app.model.command.Command;
import app.view.ViewManager;

public class CommandController {
	
	private static CommandController commandController;

	private ViewManager viewManager;
	private TaskList taskList;
	private CommandParser parser;

	private CommandController() {
		taskList = new TaskList();
		parser = new CommandParser();
	}
	
	public static CommandController getInstance() {
		if (commandController == null) {
			commandController = new CommandController();
		}
		return commandController;
	}

	/**
	 * Entry method for executing a command string. The command string is parsed
	 * and the relevant logic is executed.
	 * 
	 * @param commandString The full command string.
	 */
	public void executeCommand(String commandString) {
		/*
		 * TODO: parse command, build Task object, update taskList, update view
		 */
		// placeholder example of showing the help.
		if (commandString.equalsIgnoreCase("help")) {
			showHelp();
			return;
		}
		
		Command cmd = parser.parseCommand(commandString);
		cmd.execute();
		viewManager.updateTaskList(taskList);
		
		if (!cmd.getFeedback().isEmpty()) {
			viewManager.setStatus(cmd.getFeedback(), cmd.getStatusType());
		}
	}

	private void showHelp() {
		viewManager.updateTextView("PLACEHOLDER: help string of available commands here");
		viewManager.setStatus("Showing list of commands");
	}

	public ViewManager getViewManager() {
		return viewManager;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
	
	public TaskList getTaskList() {
		return taskList;
	}
}
