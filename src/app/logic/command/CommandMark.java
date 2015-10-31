package app.logic.command;

import app.constants.ViewConstants;

import java.util.ArrayList;
import java.util.UUID;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ActionType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.model.Action;
import app.model.TaskList;
import app.model.ViewState;
import app.storage.TaskStorage;
import app.util.Common;
import app.util.LogHelper;

public class CommandMark extends Command {

	public CommandMark() {
		super();
		this.setCommandType(CommandType.MARK);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info("Executing CommandMark object.");
		ViewState viewState = new ViewState();
		if (this.getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_NO_TASK);
			LogHelper.getInstance().getLogger().info(ViewConstants.ERROR_MARK_NO_TASK);
			return viewState;
		}

		ArrayList<Integer> displayIdsToMarkList = Common.getIdArrayList(this.getContent());
		
		try {
			displayIdsToMarkList = Common.removeDuplicatesFromArrayList(displayIdsToMarkList);
			TaskList display = previousViewState.getTaskList();
			TaskList master = CommandController.getInstance().getMasterTaskList();
			markSelectedTasks(displayIdsToMarkList, display, master);
			viewState.setTaskList(display);
			Integer taskIndex = getFirstTaskIndex(displayIdsToMarkList);
			viewState.addAction(new Action(ActionType.SCROLL_TASK_LIST_TO, display.getTaskByIndex(taskIndex)));	
			
			ArrayList<Integer> markedCompleted = getIdListByCompletion(displayIdsToMarkList, display, true);
			ArrayList<Integer> markedUncompleted = getIdListByCompletion(displayIdsToMarkList, display, false);
			setFeedbackByMarkedTaskCompletion(markedCompleted, markedUncompleted, viewState);
			
			ArrayList<UUID> markedCompletedUuid = display.getTasksUuidList(markedCompleted);
			ArrayList<UUID> markedUncompletedUuid = display.getTasksUuidList(markedUncompleted);
			logUuidByMarkedTaskCompletion(markedCompletedUuid, markedUncompletedUuid);
			
			viewState.setActiveView(ViewType.TASK_LIST);
		} catch (IndexOutOfBoundsException e) {
			LogHelper.getInstance().getLogger().info("IndexOutOfBoundsException:" + e.getMessage() +
					"; " + ViewConstants.ERROR_MARK_INVALID_ID);
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_INVALID_ID);
		} catch (NullPointerException e) {
			LogHelper.getInstance().getLogger().info("NullPointerException:" + e.getMessage() + 
					"; " + ViewConstants.ERROR_MARK_INVALID_ID);
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_INVALID_ID);
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK);
		}
		return viewState;
	}

	private Integer getFirstTaskIndex(ArrayList<Integer> displayIdsToMarkList) {
		return displayIdsToMarkList.get(0) - 1;
	}

	// Set appropriate feedback based on marked tasks' completion
	private void setFeedbackByMarkedTaskCompletion(ArrayList<Integer> markedCompleted,
			ArrayList<Integer> markedUncompleted, ViewState viewState) {
		String feedback = "";
		assert(markedCompleted.size() > 0 || markedUncompleted.size() > 0);
		
		if (markedCompleted.size() > 0 && markedUncompleted.size() > 0) {
			feedback = String.format(ViewConstants.MESSAGE_MARK_COMPLETED + "; " + ViewConstants.MESSAGE_MARK_UNCOMPLETED,
					Common.pluralize(markedCompleted.size(), "task"), Common.getIdListString(markedCompleted),
					Common.pluralize(markedUncompleted.size(), "task"), Common.getIdListString(markedUncompleted));
			viewState.setStatus(StatusType.SUCCESS, feedback);
			setExecuted(true);
		} else if (markedCompleted.size() > 0 && markedUncompleted.size() == 0) {
			feedback = String.format(ViewConstants.MESSAGE_MARK_COMPLETED,
					Common.pluralize(markedCompleted.size(), "task"), Common.getIdListString(markedCompleted));
			viewState.setStatus(StatusType.SUCCESS, feedback);
			setExecuted(true);
		} else if (markedCompleted.size() == 0 && markedUncompleted.size() > 0) {
			feedback = String.format(ViewConstants.MESSAGE_MARK_UNCOMPLETED,
					Common.pluralize(markedUncompleted.size(), "task"), Common.getIdListString(markedUncompleted));
			viewState.setStatus(StatusType.SUCCESS, feedback);
			setExecuted(true);
		}
	}

	private void logUuidByMarkedTaskCompletion(ArrayList<UUID> markedCompleted,
			ArrayList<UUID> markedUncompleted) {
		assert(markedCompleted.size() > 0 || markedUncompleted.size() > 0);
		
		if (markedCompleted.size() > 0 && markedUncompleted.size() > 0) {
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_MARK_COMPLETED + "; " + 
					ViewConstants.MESSAGE_MARK_UNCOMPLETED, Common.pluralize(markedCompleted.size(), "task"), 
					Common.getUuidListString(markedCompleted), Common.pluralize(markedUncompleted.size(), "task"), 
					Common.getUuidListString(markedUncompleted)));
		} else if (markedCompleted.size() > 0 && markedUncompleted.size() == 0) {
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_MARK_COMPLETED,
					Common.pluralize(markedCompleted.size(), "task"), Common.getUuidListString(markedCompleted)));
		} else if (markedCompleted.size() == 0 && markedUncompleted.size() > 0) {
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_MARK_UNCOMPLETED,
					Common.pluralize(markedUncompleted.size(), "task"), Common.getUuidListString(markedUncompleted)));
		}
	}
	
	// Locate the specific tasks based on displayed id and mark them
	private void markSelectedTasks(ArrayList<Integer> displayIdsToMarkList, TaskList display, TaskList master) {
		ArrayList<UUID> tasksUuidList = display.getTasksUuidList(displayIdsToMarkList);
		ArrayList<Integer> masterIdsList = master.getTasksIdList(tasksUuidList);
		for (int i = 0; i < masterIdsList.size(); i++) {
			master.markTaskByIndex(masterIdsList.get(i));
		}
		TaskStorage.getInstance().writeTasks(master);
	}

	// Filter the ArrayList of task Ids to get an ArrayList of only completed or uncompleted tasks IDs
	private ArrayList<Integer> getIdListByCompletion(ArrayList<Integer> arr, TaskList taskList, boolean isCompleted) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++) {
			if (taskList.isTaskCompleted(arr.get(i) - 1) == isCompleted) {
				idList.add(arr.get(i));
			}
		}
		return idList;
	}
}
