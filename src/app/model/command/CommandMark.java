package app.model.command;

import app.constants.ViewConstants;

import java.util.ArrayList;
import java.util.UUID;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.controller.CommandController;
import app.helper.CommandParser;
import app.helper.LogHelper;
import app.model.TaskList;

public class CommandMark extends Command {

	public CommandMark() {
		super();
		this.setCommandType(CommandType.MARK);
	}

	@Override
	public void execute() {
		LogHelper.getLogger().info("Executing CommandMark object.");
		if (this.getContent().isEmpty()) {
			setFeedback(ViewConstants.ERROR_MARK_NO_TASK);
			setStatusType(StatusType.ERROR);
			return;
		}
		
		CommandParser parser = new CommandParser();
		ArrayList<Integer> displayIdsToMarkList = parser.getIdArrayList(this.getContent());
		if (displayIdsToMarkList == null) {
			setFeedback(ViewConstants.ERROR_MARK_INVALID_ID);
			setStatusType(StatusType.ERROR);
			return;
		}

		try {
			TaskList display = CommandController.getInstance().copyDisplayedTaskList();
			TaskList master = CommandController.getInstance().getMasterTaskList();
			markSelectedTasks(displayIdsToMarkList, display, master);
			LogHelper.getLogger().info("Marked specified task.");
			CommandController.getInstance().setDisplayedTaskList(display);
			ArrayList<Integer> markedCompleted = getIdListByCompletion(displayIdsToMarkList, display, true);
			ArrayList<Integer> markedUncompleted = getIdListByCompletion(displayIdsToMarkList, display, false);
			setFeedbackByMarkedTaskCompletion(markedCompleted, markedUncompleted, parser);
			setStatusType(StatusType.SUCCESS);
		} catch (IndexOutOfBoundsException e) {
			LogHelper.getLogger().severe(e.getMessage());
			setFeedback(String.format(ViewConstants.ERROR_MARK_INVALID_ID));
			setStatusType(StatusType.ERROR);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			setFeedback(String.format(ViewConstants.ERROR_MARK, parser.pluralize(displayIdsToMarkList.size(), "task"), getIdListString(displayIdsToMarkList)));
			setStatusType(StatusType.ERROR);
		}
		CommandController.getInstance().setActiveView(ViewType.TASK_LIST);
	}

	// Set appropriate feedback based on marked tasks' completion
	private void setFeedbackByMarkedTaskCompletion(ArrayList<Integer> markedCompleted, ArrayList<Integer> markedUncompleted, CommandParser parser) {
		if (markedCompleted.size() > 0 && markedUncompleted.size() > 0) {
			setFeedback(String.format(ViewConstants.MESSAGE_MARK_COMPLETED + "; " + ViewConstants.MESSAGE_MARK_UNCOMPLETED, parser.pluralize(markedCompleted.size(), "task"),
					getIdListString(markedCompleted), parser.pluralize(markedUncompleted.size(), "task"), getIdListString(markedUncompleted)));
		} else if (markedCompleted.size() > 0 && markedUncompleted.size() == 0) {
			setFeedback(String.format(ViewConstants.MESSAGE_MARK_COMPLETED, parser.pluralize(markedCompleted.size(), "task"), getIdListString(markedCompleted)));
		} else if (markedCompleted.size() == 0 && markedUncompleted.size() > 0) {
			setFeedback(String.format(ViewConstants.MESSAGE_MARK_UNCOMPLETED, parser.pluralize(markedUncompleted.size(), "task"), getIdListString(markedUncompleted)));
		}
	}

	// Locate the specific tasks based on displayed id and mark them
	private void markSelectedTasks(ArrayList<Integer> displayIdsToMarkList, TaskList display, TaskList master) {
		ArrayList<UUID> tasksUuidList = display.getTasksUuidList(displayIdsToMarkList);
		ArrayList<Integer> masterIdsList = master.getTasksIdList(tasksUuidList);
		for (int i = 0; i < masterIdsList.size(); i++) {
			master.markTaskByIndex(masterIdsList.get(i));
		}
	}
	
	// Filter the ArrayList of task Ids to get an ArrayList of only completed or uncompleted tasks Ids
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
}
