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
import app.model.ViewState;

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

		CommandParser parser = new CommandParser();
		ArrayList<Integer> displayIdsToMarkList = parser.getIdArrayList(this.getContent());
		if (displayIdsToMarkList.get(0) == -1) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_MARK_INVALID_ID);
			return viewState;
		}

		try {
			TaskList display = previousViewState.getTaskList();
			TaskList master = CommandController.getInstance().getMasterTaskList();
			markSelectedTasks(displayIdsToMarkList, display, master);
			viewState.setTaskList(display);
			viewState.setStatus(StatusType.SUCCESS,
					String.format(ViewConstants.MESSAGE_MARK, getIdList(displayIdsToMarkList)));
			setExecuted(true);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(String.format(ViewConstants.ERROR_MARK, getIdList(displayIdsToMarkList)));
		}

		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}

	// Locate the specific tasks based on displayed id and mark them
	private void markSelectedTasks(ArrayList<Integer> displayIdsToMarkList, TaskList display, TaskList master) {
		ArrayList<UUID> tasksUuidList = display.getTasksUuidList(displayIdsToMarkList);
		ArrayList<Integer> masterIdsList = master.getTasksIdList(tasksUuidList);
		for (int i = 0; i < masterIdsList.size(); i++) {
			master.markTaskByIndex(masterIdsList.get(i));
		}
	}

	// converts the ArrayList of id into a String, with each id separated by
	// comma
	private String getIdList(ArrayList<Integer> arr) {
		String idList = "";
		for (int i = 0; i < arr.size(); i++) {
			idList += String.valueOf(arr.get(i)) + ", ";
		}
		idList = idList.replaceAll(",[ \t]*$", "");
		return idList;
	}
}
