package app.controller;

import app.helper.CommandParser;
import app.model.TaskList;
import app.model.command.Command;
import app.view.ViewManager;

/**
 * This class provides the layer of logic between the ViewManager and the rest
 * of the program. Classes from this app.controller package are the only ones to
 * interact with the ViewManager.
 */
public class CommandController {

	private static CommandController commandController;

	private ViewManager viewManager;
	private TaskList taskList;
	private CommandParser parser;

	private CommandController() {
		taskList = new TaskList();
		parser = new CommandParser();
	}

	/**
	 * This method implements the Singleton design pattern.
	 * 
	 * @return This instance of CommandController.
	 */

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
		// placeholder example of showing the help. Implement as a CommandHelp
		// object instead.
		if (commandString.equalsIgnoreCase("help")) {
			showHelp();
			return;
		}

		Command cmd = parser.parseCommand(commandString);
		cmd.execute();
		viewManager.updateTaskList(taskList);

		// Set new status bar message if feedback exists.
		if (!cmd.getFeedback().isEmpty()) {
			viewManager.setStatus(cmd.getFeedback(), cmd.getStatusType());
		}
	}

	private void showHelp() {
		viewManager.updateTextView("PLACEHOLDER: help string of available commands here");
		viewManager.setStatus("Showing list of commands");
	}

	/**
	 * Updates the view with the specified theme.
	 * 
	 * @param themeCss The new theme to set.
	 */
	public void setTheme(String themeCss) {
		viewManager.setTheme(themeCss);
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}

	public TaskList getTaskList() {
		return taskList;
	}
}
