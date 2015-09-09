package app.controller;

import app.model.Command;
import app.model.Task;
import app.model.TaskList;
import app.model.Task.Priority;
import app.view.ViewManager;
import app.view.ViewManager.StatusType;

public class CommandController {
	
	private static CommandController commandController;

	private ViewManager viewManager;
	private TaskList taskList;

	private CommandController() {
		taskList = new TaskList();
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
		 * THIS IS ALL PLACEHOLDER CODE! This whole chuck should be removed once
		 * parsing is done. now we're just setting the task name to the user
		 * input
		 */
		// placeholder example of showing the help.
		if (commandString.equalsIgnoreCase("help")) {
			showHelp();
			return;
		}
		Command cmd = new Command(commandString);
		cmd.setTaskName(commandString);
		
		// placeholder logic for priority levels
		if (commandString.contains("priority high")) {
			cmd.setPriority(Priority.HIGH);
		} else if (commandString.contains("priority medium")) {
			cmd.setPriority(Priority.MEDIUM);
		} else if (commandString.contains("priority low")) {
			cmd.setPriority(Priority.LOW);
		}

		taskList.addTask(new Task(cmd));
		viewManager.updateTaskList(taskList);
		viewManager.setStatus("Added task: " + cmd.getTaskName(), StatusType.SUCCESS);
	}

	private void showHelp() {
		viewManager.updateTextView("PLACEHOLDER: help string of available commands here");
		viewManager.setStatus("Showing list of commands");
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
}
