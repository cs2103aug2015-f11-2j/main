package app.logic.command;

import app.constants.ViewConstants;

import app.constants.CommandConstants.CommandType;
import app.constants.CommandConstants.DisplayType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.model.TaskList;
import app.model.ViewState;
import app.parser.CommandParser;
import app.util.LogHelper;

public class CommandDisplay extends Command {

	public CommandDisplay() {
		super();
		this.setCommandType(CommandType.DISPLAY);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getLogger().info("Executing CommandDisplay object.");
		ViewState viewState = new ViewState();

		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList retrievedTaskList = new TaskList();

		try {
			DisplayType type = CommandParser.determineDisplayType(this.getContent());
			String arg = type.toString().toLowerCase();
			
			// default display argument is uncompleted
			if (this.getContent().isEmpty() || type == DisplayType.UNCOMPLETED) {
				retrievedTaskList = master.getTaskListByCompletion(false);
				viewState.setTaskList(retrievedTaskList);
				viewState.setHeader(
						String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS,
						String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
				
			} else if (type == DisplayType.COMPLETED) {
				retrievedTaskList = master.getTaskListByCompletion(true);
				viewState.setTaskList(retrievedTaskList);
				viewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
				
			} else if (type == DisplayType.ALL) {
				viewState.setTaskList(master);
				viewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
				
			} else if (type == DisplayType.INVALID) {
				viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_DISPLAY_INVALID_ARGUMENT));
			}
			
			viewState.setActiveView(ViewType.TASK_LIST);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_DISPLAY));
		}
		return viewState;
	}
}
