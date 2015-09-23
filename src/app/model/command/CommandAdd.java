package app.model.command;

import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.controller.CommandController;
import app.helper.LogHelper;
import app.model.Task;
import app.model.TaskList;

public class CommandAdd extends Command {
	
	private Task task;

	public CommandAdd() {
		super();
		this.setCommandType(CommandType.ADD);
	}

	@Override
	public void execute() {
		LogHelper.getLogger().info("Executing CommandAdd object.");
		if (this.getContent().isEmpty()) {
			setFeedback(ViewConstants.ERROR_ADD_NO_TASK);
			setStatusType(StatusType.ERROR);
			return;
		}
		
		task = new Task(this);
		try {
			TaskList master = CommandController.getInstance().getMasterTaskList();
			master.addTask(task);
			CommandController.getInstance().setDisplayedTaskList(master);
			CommandController.getInstance().scrollTaskListTo(task);
			
			setFeedback(String.format(ViewConstants.MESSAGE_ADD, task.getName()));
			setStatusType(StatusType.SUCCESS);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			setFeedback(String.format(ViewConstants.ERROR_ADD, task.getName()));
			setStatusType(StatusType.ERROR);
		}
		
		CommandController.getInstance().setActiveView(ViewType.TASK_LIST);
	}
}
