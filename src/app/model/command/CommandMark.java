package app.model.command;

import app.constants.ViewConstants;

import java.util.ArrayList;

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
		ArrayList<Integer> idToMark = parser.getIdArrayList(this.getContent());
		if (idToMark.get(0) == -1) {
			setFeedback(ViewConstants.ERROR_MARK_INVALID_ID);
			setStatusType(StatusType.ERROR);
			return;
		}

		try {
			TaskList master = CommandController.getInstance().getMasterTaskList();
			for (int i = 0; i < idToMark.size(); i++) {
				master.markTaskById(idToMark.get(i));
			}
			CommandController.getInstance().getDisplayedTaskList().setAll(master);
			setFeedback(String.format(ViewConstants.MESSAGE_MARK, getIdList(idToMark)));
			setStatusType(StatusType.SUCCESS);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			setFeedback(String.format(ViewConstants.ERROR_MARK, getIdList(idToMark)));
			setStatusType(StatusType.ERROR);
		}
		
		CommandController.getInstance().setActiveView(ViewType.TASK_LIST);
	}
	
	// converts the ArrayList of id into a String, with each id separated by comma
	private String getIdList(ArrayList<Integer> arr) {
		String idList = "";
		for (int i = 0; i < arr.size(); i++) {
			idList += String.valueOf(arr.get(i)) + ", ";
		}
		idList = idList.replaceAll(",[ \t]*$", "");
		return idList;
	}
}
