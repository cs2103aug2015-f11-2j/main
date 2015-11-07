package app.logic.command;

// @@author A0125990Y
import app.constants.ViewConstants;

import java.util.UUID;

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
	private UUID storeId;
	private ViewState previousViewState;
	
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
			
			storeId = task.getId(); // store id for undo
			this.previousViewState = new ViewState(previousViewState);
			
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_ADD, task.getName()));
			setExecuted(true);
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage() + String.format(ViewConstants.ERROR_ADD, task.getName()));
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_ADD, task.getName()));
		}		
		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}
	
	// @@author A0125990Y
	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
	
		try {
			
			TaskList master = CommandController.getInstance().getMasterTaskList();
			TaskList display = previousViewState.getTaskList();

			int id = master.getTaskIndexByUuid(storeId);
			int displayId = display.getTaskIndexByUuid(storeId);
			
			LogHelper.getInstance().getLogger().info(String.format("UNDO_ADD:" + master.getTaskList().get(id)));
			
			master.getTaskList().remove(id);
			display.getTaskList().remove(displayId);
			
			TaskStorage.getInstance().writeTasks(master);
			previousViewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_UNDO));
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_UNDO ));
			setExecuted(true);
	
		} 	catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage() + String.format(ViewConstants.ERROR_UNDO));
			previousViewState.setStatus(StatusType.ERROR, String.format(ViewConstants.MESSAGE_UNDO));
		}	
	
		return previousViewState;

	}
}
