package app.model.command;

import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.controller.CommandController;
import app.helper.LogHelper;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;

public class CommandAdd extends Command {
	
	private Task task;

	public CommandAdd() {
		super();
		this.setCommandType(CommandType.ADD);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getLogger().info("Executing CommandAdd object.");
		ViewState viewState = new ViewState();
		if (this.getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_ADD_NO_TASK);
			return viewState;
		}
		
		task = new Task(this);
		try {
			TaskList master = CommandController.getInstance().getMasterTaskList();
			TaskList displayed = previousViewState.getTaskList();
			master.addTask(task);
			displayed.addTask(task);
			viewState.setTaskList(displayed);
			//CommandController.getInstance().scrollTaskListTo(task);
			
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_ADD, task.getName()));
			setExecuted(true);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_ADD, task.getName()));
		}
		
		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}
}
