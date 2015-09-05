package app.controller;

import app.model.Command;
import app.model.Task;
import app.model.TaskList;
import app.view.ViewManager;

public class CommandController {

	private ViewManager viewManager;
	private TaskList taskList;

	public CommandController(ViewManager viewManager) {
		this.viewManager = viewManager;
		taskList = new TaskList();
	}

	public void executeCommand(String commandString) {
		/*
		 * TODO: parse command, build Task object, update taskList, update view
		 * this is placeholder code until parsing is done. now we're just
		 * setting the task name to the user input
		 */
		Command cmd = new Command(commandString);
		cmd.setTaskName(commandString);

		taskList.addTask(new Task(cmd));
		viewManager.updateTaskList(taskList);
	}
}
