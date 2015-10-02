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
			// default display argument is uncompleted
			if (this.getContent().isEmpty() || arg.equals(DisplayType.UNCOMPLETED.toString().toLowerCase())) {
				retrievedTaskList = master.getTaskListByCompletion(false);
				CommandController.getInstance().setDisplayedTaskList(retrievedTaskList);
				CommandController.getInstance().setHeader(String.format(ViewConstants.HEADER_DISPLAY, DisplayType.UNCOMPLETED.toString().toLowerCase()));
				setFeedback(String.format(ViewConstants.MESSAGE_DISPLAY, DisplayType.UNCOMPLETED.toString().toLowerCase()));
				setStatusType(StatusType.SUCCESS);
			} else if (arg.equals(DisplayType.COMPLETED.toString().toLowerCase())) {
				retrievedTaskList = master.getTaskListByCompletion(true);
				CommandController.getInstance().setDisplayedTaskList(retrievedTaskList);
				CommandController.getInstance().setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				setFeedback(String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setStatusType(StatusType.SUCCESS);
			} else if (arg.equals(DisplayType.ALL.toString().toLowerCase())) {
				CommandController.getInstance().setDisplayedTaskList(master);
				CommandController.getInstance().setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				setFeedback(String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setStatusType(StatusType.SUCCESS);
			} else if (arg.equals(DisplayType.INVALID.toString().toLowerCase())) {
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
