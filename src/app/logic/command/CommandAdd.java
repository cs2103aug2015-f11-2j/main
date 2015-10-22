package app.logic.command;

import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ActionType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.model.Action;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;
import app.storage.TaskStorage;
import app.util.LogHelper;

public class CommandAdd extends Command {
	
	private Task task;

	public CommandAdd() {
		super();
		this.setCommandType(CommandType.ADD);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info("Executing CommandAdd object.");
		ViewState viewState = new ViewState();
		if (this.getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_ADD_NO_TASK);
			LogHelper.getInstance().getLogger().info(ViewConstants.ERROR_ADD_NO_TASK);
			return viewState;
		}
		
		task = new Task(this);
		try {
			TaskList master = CommandController.getInstance().getMasterTaskList();
			TaskList displayed = previousViewState.getTaskList();
			master.addTask(task);
			TaskStorage.getInstance().writeTasks(master);
			displayed.addTask(task);
			viewState.setTaskList(displayed);
			viewState.addAction(new Action(ActionType.SCROLL_TASK_LIST_TO, task));

			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_ADD, task.getName() + "; UUID:" + task.getId()));

			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_ADD, task.getName()));
			setExecuted(true);
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage() + String.format(ViewConstants.ERROR_ADD, task.getName()));
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_ADD, task.getName()));
		}
		
		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}
	
	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
		
		// TODO: undo code here
		return new ViewState();
	}
}
