package app.controller;

import app.constants.ViewConstants.ViewType;
import app.helper.CommandParser;
import app.model.Task;
import app.model.TaskList;
import app.model.command.Command;
import app.view.ViewManager;
import javafx.collections.ListChangeListener;

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
	private ViewType activeView;

	private CommandController() {
		parser = new CommandParser();
		taskList = new TaskList();
		
		// Updates the view whenever taskList is changed.
		taskList.getTaskList().addListener(new ListChangeListener<Task>() {
			public void onChanged(ListChangeListener.Change<? extends Task> c) {
				viewManager.updateTaskList(taskList);
			}
		});
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
			activeView = ViewType.TEXT_VIEW;
			showActiveView();
			return;
		}
		
		commandString = commandString.trim();
		Command cmd = parser.parseCommand(commandString);
		cmd.execute();
		showActiveView();

		// Set new status bar message if feedback exists.
		if (!cmd.getFeedback().isEmpty()) {
			viewManager.setStatus(cmd.getFeedback(), cmd.getStatusType());
		}
	}

	private void showHelp() {
		viewManager.updateTextView("PLACEHOLDER: help string of available commands here");
		viewManager.setStatus("Showing list of commands");
	}
	
	private void showActiveView() {
		if (activeView == ViewType.TASK_LIST) {
			viewManager.showTaskList();
		} else if (activeView == ViewType.TEXT_VIEW) {
			viewManager.showTextView();
		}
	}

	/**
	 * Updates the view with the specified theme.
	 * 
	 * @param themeCss The new theme to set.
	 */
	public void setTheme(String themeCss) {
		viewManager.setTheme(themeCss);
	}

	public void setActiveView(ViewType activeView) {
		this.activeView = activeView;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}

	public TaskList getTaskList() {
		return taskList;
	}
}
