package app.logic.command;

import app.constants.ViewConstants;

import java.util.ArrayList;
import java.util.UUID;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
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
			ArrayList<UUID> markedTasksUuidList = markSelectedTasks(displayIdsToMarkList, display, master);
			viewState.setTaskList(display);
			
			ArrayList<Integer> markedCompleted = getIdListByCompletion(displayIdsToMarkList, display, true);
			ArrayList<Integer> markedUncompleted = getIdListByCompletion(displayIdsToMarkList, display, false);
			viewState = setFeedbackByMarkedTaskCompletion(markedCompleted, markedUncompleted, viewState);
			
			ArrayList<UUID> markedCompletedUuid = getUuidListByCompletion(markedTasksUuidList, display, true);
			ArrayList<UUID> markedUncompletedUuid = getUuidListByCompletion(markedTasksUuidList, display, false);
			logUuidByMarkedTaskCompletion(markedCompletedUuid, markedUncompletedUuid);
					
			viewState.setActiveView(ViewType.TASK_LIST);
		} catch (IndexOutOfBoundsException e) {
			LogHelper.getInstance().getLogger().severe("IndexOutOfBoundsException:" + e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_MARK_INVALID_ID));
		} catch (NullPointerException e) {
			LogHelper.getInstance().getLogger().severe("NullPointerException:" + e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_MARK_INVALID_ID));
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_MARK));
		}
		return viewState;
	}

	// Set appropriate feedback based on marked tasks' completion
	private ViewState setFeedbackByMarkedTaskCompletion(ArrayList<Integer> markedCompleted,
			ArrayList<Integer> markedUncompleted, ViewState viewState) {
		String feedback = "";
		assert(markedCompleted.size() > 0 || markedUncompleted.size() > 0);
		
		if (markedCompleted.size() > 0 && markedUncompleted.size() > 0) {
			feedback = String.format(ViewConstants.MESSAGE_MARK_COMPLETED + "; " + ViewConstants.MESSAGE_MARK_UNCOMPLETED,
					Common.pluralize(markedCompleted.size(), "task"), getIdListString(markedCompleted),
					Common.pluralize(markedUncompleted.size(), "task"), getIdListString(markedUncompleted));
			viewState.setStatus(StatusType.SUCCESS, feedback);
			setExecuted(true);
		} else if (markedCompleted.size() > 0 && markedUncompleted.size() == 0) {
			feedback = String.format(ViewConstants.MESSAGE_MARK_COMPLETED,
					Common.pluralize(markedCompleted.size(), "task"), getIdListString(markedCompleted));
			viewState.setStatus(StatusType.SUCCESS, feedback);
			setExecuted(true);
		} else if (markedCompleted.size() == 0 && markedUncompleted.size() > 0) {
			feedback = String.format(ViewConstants.MESSAGE_MARK_UNCOMPLETED,
					Common.pluralize(markedUncompleted.size(), "task"), getIdListString(markedUncompleted));
			viewState.setStatus(StatusType.SUCCESS, feedback);
			setExecuted(true);
		}
		return viewState;
	}

	private void logUuidByMarkedTaskCompletion(ArrayList<UUID> markedCompleted,
			ArrayList<UUID> markedUncompleted) {
		assert(markedCompleted.size() > 0 || markedUncompleted.size() > 0);
		
		if (markedCompleted.size() > 0 && markedUncompleted.size() > 0) {
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_MARK_COMPLETED + "; " + 
					ViewConstants.MESSAGE_MARK_UNCOMPLETED, Common.pluralize(markedCompleted.size(), "task"), 
					getUuidListString(markedCompleted), Common.pluralize(markedUncompleted.size(), "task"), 
					getUuidListString(markedUncompleted)));
		} else if (markedCompleted.size() > 0 && markedUncompleted.size() == 0) {
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_MARK_COMPLETED,
					Common.pluralize(markedCompleted.size(), "task"), getUuidListString(markedCompleted)));
		} else if (markedCompleted.size() == 0 && markedUncompleted.size() > 0) {
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_MARK_UNCOMPLETED,
					Common.pluralize(markedUncompleted.size(), "task"), getUuidListString(markedUncompleted)));
		}
	}
	
	// Locate the specific tasks based on displayed id and mark them
	private ArrayList<UUID> markSelectedTasks(ArrayList<Integer> displayIdsToMarkList, TaskList display, TaskList master) {
		ArrayList<UUID> tasksUuidList = display.getTasksUuidList(displayIdsToMarkList);
		ArrayList<Integer> masterIdsList = master.getTasksIdList(tasksUuidList);
		for (int i = 0; i < masterIdsList.size(); i++) {
			master.markTaskByIndex(masterIdsList.get(i));
		}
		TaskStorage.getInstance().writeTasks(master);
		return tasksUuidList;
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
	
	// Filter the ArrayList of task UUIDs to get an ArrayList of only completed or uncompleted tasks UUIDs
	private ArrayList<UUID> getUuidListByCompletion(ArrayList<UUID> arr, TaskList taskList, boolean isCompleted) {
		ArrayList<UUID> uuidList = new ArrayList<UUID>();
		for (int i = 0; i < arr.size(); i++) {
			if (taskList.isTaskCompleted(taskList.getTaskIndexByUuid(arr.get(i))) == isCompleted) {
				uuidList.add(arr.get(i));
			}
		}
		return uuidList;
	}

	// converts the ArrayList of id into a String, with each id separated by comma
	private String getIdListString(ArrayList<Integer> arr) {
		String idList = "";
		for (int i = 0; i < arr.size(); i++) {
			idList += String.valueOf(arr.get(i)) + ", ";
		}
		idList = idList.replaceAll(",[ \t]*$", "");
		return idList;
	}
	
	// converts the ArrayList of UUID into a String, with each UUID separated by comma
	private String getUuidListString(ArrayList<UUID> arr) {
		String idList = "";
		for (int i = 0; i < arr.size(); i++) {
			idList += String.valueOf(arr.get(i)) + ", ";
		}
		idList = idList.replaceAll(",[ \t]*$", "");
		return idList;
	}
}
