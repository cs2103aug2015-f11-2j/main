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

	private  ArrayList<UUID> storePreviousId  = new ArrayList<UUID>(); 
	private ViewState previousViewState;
	
	public CommandMark() {
		super();
		this.setCommandType(CommandType.MARK);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info("Executing CommandMark object.");
		this.previousViewState = new ViewState(previousViewState); 
		
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
			
			ArrayList<Integer> markedCompleted = getIdListByCompletion(displayIdsToMarkList, display, true);
			ArrayList<Integer> markedUncompleted = getIdListByCompletion(displayIdsToMarkList, display, false);
			viewState = setFeedbackByMarkedTaskCompletion(markedCompleted, markedUncompleted, viewState);
			
			ArrayList<UUID> markedCompletedUuid = display.getTasksUuidList(markedCompleted);
			ArrayList<UUID> markedUncompletedUuid = display.getTasksUuidList(markedUncompleted);
			logUuidByMarkedTaskCompletion(markedCompletedUuid, markedUncompletedUuid);
			
			for (UUID i : markedCompletedUuid) {
				storePreviousId.add(i);				
			}
			
			for (UUID i : markedUncompletedUuid) {
				storePreviousId.add(i);				
			}
					
			viewState.setActiveView(ViewType.TASK_LIST);
		} catch (IndexOutOfBoundsException e) {
			LogHelper.getInstance().getLogger().severe("IndexOutOfBoundsException:" + e.getMessage() +
					"; " + ViewConstants.ERROR_MARK_INVALID_ID);
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_INVALID_ID);
		} catch (NullPointerException e) {
			LogHelper.getInstance().getLogger().severe("NullPointerException:" + e.getMessage() + 
					"; " + ViewConstants.ERROR_MARK_INVALID_ID);
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_INVALID_ID);

		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK);
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
	
	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
		
		try {
		
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList displayed = previousViewState.getTaskList();
		
		int id;
		for (UUID i : storePreviousId){
			id = master.getTaskIndexByUuid(i);
			master.markTaskByIndex(id);
		}

		TaskStorage.getInstance().writeTasks(master);
		previousViewState.setTaskList(displayed);
		previousViewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_UNDO));
		LogHelper.getInstance().getLogger().info(String.format("UNDO_MARK:" + ViewConstants.MESSAGE_UNDO));
		setExecuted(true);
		
		} 	catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage() + String.format(ViewConstants.ERROR_UNDO));
			previousViewState.setStatus(StatusType.ERROR, String.format(ViewConstants.MESSAGE_UNDO));
		}	
		
		return previousViewState;
	}
}
