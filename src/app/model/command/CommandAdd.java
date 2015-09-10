package app.model.command;

import app.controller.CommandController;
import app.helper.LogHelper;
import app.model.Task;
import app.view.ViewManager.StatusType;

public class CommandAdd extends Command {
	
	private Task task;

	public CommandAdd() {
		super();
	}

	public CommandAdd(CommandType commandType) {
		super(commandType);
	}

	@Override
	public void execute() {
		LogHelper.getLogger().info("Executing CommandAdd object.");
		task = new Task(this);
		
		try {
			CommandController.getInstance().getTaskList().addTask(task);
			setFeedback("Added task: " + getTaskName());
			setStatusType(StatusType.SUCCESS);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			setFeedback("Error adding task: " + getTaskName());
			setStatusType(StatusType.ERROR);
		}
	}
}
