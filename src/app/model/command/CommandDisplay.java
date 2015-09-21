package app.model.command;

import app.constants.ViewConstants;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.controller.CommandController;
import app.helper.CommandParser;
import app.helper.LogHelper;
import app.model.TaskList;

public class CommandDisplay extends Command {

	public CommandDisplay() {
		super();
		this.setCommandType(CommandType.DISPLAY);
	}

	@Override
	public void execute() {
		LogHelper.getLogger().info("Executing CommandDisplay object.");
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList retrievedTaskList = new TaskList();
		CommandParser parser = new CommandParser();

		try {
			String arg = parser.getCommandDisplayArg(this.getContent());
			if (this.getContent().isEmpty() || arg.equals("uncompleted")) {
				// default display argument is uncompleted
				retrievedTaskList = master.getTaskListByCompletion(false);
				CommandController.getInstance().getDisplayedTaskList().setAll(retrievedTaskList);
				CommandController.getInstance().setHeader(String.format(ViewConstants.HEADER_DISPLAY, "uncompleted"));
				setFeedback(String.format(ViewConstants.MESSAGE_DISPLAY, "uncompleted"));
				setStatusType(StatusType.SUCCESS);
			} else if (arg.equals("completed")) {
				retrievedTaskList = master.getTaskListByCompletion(true);
				CommandController.getInstance().getDisplayedTaskList().setAll(retrievedTaskList);
				CommandController.getInstance().setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				setFeedback(String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setStatusType(StatusType.SUCCESS);
			} else if (arg.equals("all")) {
				CommandController.getInstance().getDisplayedTaskList().setAll(master);
				CommandController.getInstance().setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				setFeedback(String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setStatusType(StatusType.SUCCESS);
			} else if (arg.equals("invalid")) {
				setFeedback(String.format(ViewConstants.ERROR_DISPLAY_INVALID_ARGUMENT));
				setStatusType(StatusType.ERROR);
			}
			CommandController.getInstance().setActiveView(ViewType.TASK_LIST);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			setFeedback(String.format(ViewConstants.ERROR_DISPLAY));
			setStatusType(StatusType.ERROR);
		}
	}
}
