package app.logic.command;

import app.constants.StorageConstants;
import app.constants.ViewConstants;

import java.time.LocalDateTime;
import java.util.UUID;

import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.Priority;
import app.constants.ViewConstants.ActionType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.storage.TaskStorage;
import app.util.LogHelper;
import app.model.Action;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;

public class CommandEdit extends Command {

	private Integer displayId;
	private ViewState previousViewState;
	private UUID uuid;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String content;
	private Priority priority;	
	
	// @@author A0125360R
	public CommandEdit() {
		super();
		this.setCommandType(CommandType.EDIT);
		displayId = null;
	}
	
	// @@author A0125360R
	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_EXECUTE_COMMAND, "CommandEdit"));
		this.previousViewState = new ViewState(previousViewState);
		
		ViewState viewState = new ViewState();
		Task task = new Task(this);
		
		try {
			if (this.displayId == null) {
				LogHelper.getInstance().getLogger().info(ViewConstants.ERROR_EDIT_NO_TASK);
				viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_EDIT_NO_TASK);
				return viewState;
			}
			
			int taskIndex = displayId - 1;
			TaskList master = CommandController.getInstance().getMasterTaskList();
			TaskList display = previousViewState.getTaskList();
			if (displayId > display.getTaskList().size() || displayId <= 0) {
				viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_EDIT_INVALID_TASK_ID);
				LogHelper.getInstance().getLogger().info(ViewConstants.ERROR_EDIT_INVALID_TASK_ID);
				return viewState;
			}
			
			boolean isEdited = editTask(display, master, task, taskIndex);
			if (isEdited == true) {
				TaskStorage.getInstance().writeTasks(master);
				setExecuted(true);
				viewState.setTaskList(display);
				viewState.addAction(new Action(ActionType.SCROLL_TASK_LIST_TO, display.getTaskByIndex(taskIndex)));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_EDIT, display.getTaskByIndex(taskIndex).getName()));
				LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_EDIT, display.getTaskByIndex(taskIndex).getId()));

			} else {
				LogHelper.getInstance().getLogger().info(String.format(ViewConstants.ERROR_EDIT_NO_CHANGES, displayId));
				viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_EDIT_NO_CHANGES, displayId));
			}
			
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_EDIT);
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

			int id = master.getTaskIndexByUuid(this.uuid);

			master.getTaskByIndex(id).setStartDate(startDate);
			master.getTaskByIndex(id).setEndDate(endDate);
			master.getTaskByIndex(id).setName(content);
			master.getTaskByIndex(id).setPriority(priority);

			TaskStorage.getInstance().writeTasks(master);
			previousViewState.setTaskList(previousViewState.getTaskList());
			previousViewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_UNDO));
			LogHelper.getInstance().getLogger().info(String.format("UNDO_EDIT:" + ViewConstants.MESSAGE_UNDO));
			setExecuted(true);

		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage() + String.format(ViewConstants.ERROR_UNDO));
			previousViewState.setStatus(StatusType.ERROR, String.format(ViewConstants.MESSAGE_UNDO));
		}

		return previousViewState;
	}

	// @@author A0125360R
	// Base on displayed index, find task in master tasklist and update it.
	// Returns true if something has been edited
	private boolean editTask(TaskList display, TaskList master, Task task, int taskIndex) {
		UUID uuid = display.getTaskUuidByIndex(taskIndex);
		int masterListIndex = master.getTaskIndexByUuid(uuid);
		
		//store the task's values before it gets edited
		this.uuid = uuid; 
		startDate =  master.getTaskByIndex(masterListIndex).getStartDate();
		endDate =  master.getTaskByIndex(masterListIndex).getEndDate();
		priority =  master.getTaskByIndex(masterListIndex).getPriority();
		content =  master.getTaskByIndex(masterListIndex).getName().toString();
	
		return master.updateTask(task, masterListIndex);
	}

	// @@author generated
	public void setDisplayId(Integer id) {
		this.displayId = id;
	}

	public Integer getDisplayId() {
		return displayId;
	}
}
	

