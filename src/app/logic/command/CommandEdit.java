package app.logic.command;

import app.constants.ViewConstants;

import java.util.UUID;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ActionType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.parser.CommandParser;
import app.storage.TaskStorage;
import app.util.LogHelper;
import app.model.Action;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;

public class CommandEdit extends Command {

	public CommandEdit() {
		super();
		this.setCommandType(CommandType.EDIT);
	}
	
	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getLogger().info("Executing CommandEdit object.");
		ViewState viewState = new ViewState();
		Task task = new Task(this);
		
		try {
			int taskId = CommandParser.getTaskDisplayedIdFromContent(this.getContent());
			int taskIndex = taskId - 1;
			task.setName(CommandParser.getTaskDescFromContent(this.getContent()));
			TaskList master = CommandController.getInstance().getMasterTaskList();
			TaskList display = previousViewState.getTaskList();
			if (taskId > display.getTaskList().size() || taskId <= 0) {
				viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_EDIT_INVALID_TASK_ID);
				LogHelper.getLogger().info(ViewConstants.ERROR_EDIT_INVALID_TASK_ID);
				return viewState;
			}

			boolean isEdited = editTask(display, master, task, taskIndex);
			if (isEdited == true ) {
				TaskStorage.getInstance().writeTasks(master);
				setExecuted(true);
				viewState.setTaskList(display);
				viewState.addAction(new Action(ActionType.SCROLL_TASK_LIST_TO, display.getTaskByIndex(taskIndex)));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_EDIT, display.getTaskByIndex(taskIndex).getName()));
				LogHelper.getLogger().info(String.format(ViewConstants.MESSAGE_EDIT, display.getTaskByIndex(taskIndex).getId()));
			} else {
				LogHelper.getLogger().info(String.format(ViewConstants.ERROR_EDIT_NO_CHANGES, taskId));
				viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_EDIT_NO_CHANGES, taskId));
			}
		} catch (NumberFormatException e) {
			LogHelper.getLogger().severe("NumberFormatException:" + e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_EDIT_NO_TASK));
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_EDIT));
		}
		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}

	// Base on displayed index, find task in master tasklist and update it.
	// Returns an integer which is more than 0 if something has been edited
	private boolean editTask(TaskList display, TaskList master, Task task, int taskIndex) {
		UUID uuid = display.getTaskUuidByIndex(taskIndex);
		int masterListIndex = master.getTaskIndexByUuid(uuid);
		return master.updateTask(task, masterListIndex);
	}
}
	

