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
		LogHelper.getLogger().info("Executing CommandMark object.");
		ViewState viewState = new ViewState();
		if (this.getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_NO_TASK);
			return viewState;
		}

		ArrayList<Integer> displayIdsToMarkList = Common.getIdArrayList(this.getContent());

		if (displayIdsToMarkList == null) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_INVALID_ID);
			return viewState;
		}

		try {
			TaskList display = previousViewState.getTaskList();
			TaskList master = CommandController.getInstance().getMasterTaskList();
			markSelectedTasks(displayIdsToMarkList, display, master);
			LogHelper.getLogger().info("Marked specified task.");
			viewState.setTaskList(display);
			ArrayList<Integer> markedCompleted = getIdListByCompletion(displayIdsToMarkList, display, true);
			ArrayList<Integer> markedUncompleted = getIdListByCompletion(displayIdsToMarkList, display, false);
			viewState = setFeedbackByMarkedTaskCompletion(markedCompleted, markedUncompleted, viewState);
			viewState.setActiveView(ViewType.TASK_LIST);
		} catch (IndexOutOfBoundsException e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_MARK_INVALID_ID));
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_MARK,
					Common.pluralize(displayIdsToMarkList.size(), "task"), getIdListString(displayIdsToMarkList)));
		}
		return viewState;
	}

	// Set appropriate feedback based on marked tasks' completion
	private ViewState setFeedbackByMarkedTaskCompletion(ArrayList<Integer> markedCompleted,
			ArrayList<Integer> markedUncompleted, ViewState viewState) {
		assert(markedCompleted.size() > 0 || markedUncompleted.size() > 0);
		
		if (markedCompleted.size() > 0 && markedUncompleted.size() > 0) {
			viewState.setStatus(StatusType.SUCCESS,
					String.format(ViewConstants.MESSAGE_MARK_COMPLETED + "; " + ViewConstants.MESSAGE_MARK_UNCOMPLETED,
							Common.pluralize(markedCompleted.size(), "task"), getIdListString(markedCompleted),
							Common.pluralize(markedUncompleted.size(), "task"), getIdListString(markedUncompleted)));
			setExecuted(true);
		} else if (markedCompleted.size() > 0 && markedUncompleted.size() == 0) {
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_MARK_COMPLETED,
					Common.pluralize(markedCompleted.size(), "task"), getIdListString(markedCompleted)));
			setExecuted(true);
		} else if (markedCompleted.size() == 0 && markedUncompleted.size() > 0) {
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_MARK_UNCOMPLETED,
					Common.pluralize(markedUncompleted.size(), "task"), getIdListString(markedUncompleted)));
			setExecuted(true);
		}
		return viewState;
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

	// Filter the ArrayList of task Ids to get an ArrayList of only completed or
	// uncompleted tasks Ids
	private ArrayList<Integer> getIdListByCompletion(ArrayList<Integer> arr, TaskList taskList, boolean isCompleted) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++) {
			if (taskList.isTaskCompleted(arr.get(i) - 1) == isCompleted) {
				idList.add(arr.get(i));
			}
		}
		return idList;
	}

	// converts the ArrayList of id into a String, with each id separated by
	// comma
	private String getIdListString(ArrayList<Integer> arr) {
		String idList = "";
		for (int i = 0; i < arr.size(); i++) {
			idList += String.valueOf(arr.get(i)) + ", ";
		}
		idList = idList.replaceAll(",[ \t]*$", "");
		return idList;
	}
}
