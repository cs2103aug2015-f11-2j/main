package app.model.command;

import app.constants.ViewConstants;

import app.constants.CommandConstants.CommandType;
import app.constants.CommandConstants.DisplayType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.controller.CommandController;
import app.helper.CommandParser;
import app.helper.LogHelper;
import app.model.TaskList;
import app.model.ViewState;

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
		CommandParser parser = new CommandParser();

		try {
			String arg = parser.getCommandDisplayArg(this.getContent());
			// default display argument is uncompleted
			if (this.getContent().isEmpty() || arg.equals(DisplayType.UNCOMPLETED.toString().toLowerCase())) {
				retrievedTaskList = master.getTaskListByCompletion(false);
				viewState.setTaskList(retrievedTaskList);
				viewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY, DisplayType.UNCOMPLETED.toString().toLowerCase()));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DISPLAY, DisplayType.UNCOMPLETED.toString().toLowerCase()));
				setExecuted(true);
			} else if (arg.equals(DisplayType.COMPLETED.toString().toLowerCase())) {
				retrievedTaskList = master.getTaskListByCompletion(true);
				viewState.setTaskList(retrievedTaskList);
				viewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
			} else if (arg.equals(DisplayType.ALL.toString().toLowerCase())) {
				viewState.setTaskList(master);
				viewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
			} else if (arg.equals(DisplayType.INVALID.toString().toLowerCase())) {
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
