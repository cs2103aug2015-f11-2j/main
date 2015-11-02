package app.logic.command;

import app.constants.ViewConstants;

import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.DisplayType;
import app.constants.ViewConstants.ActionType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.model.Action;
import app.model.TaskList;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandDisplay extends Command {
	
	private ViewState previousViewState;

	public CommandDisplay() {
		super();
		this.setCommandType(CommandType.DISPLAY);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info("Executing CommandDisplay object.");
		this.previousViewState = new ViewState(previousViewState);
		ViewState viewState = new ViewState();

		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList retrievedTaskList = new TaskList();

		try {
			String arg = this.getContent();

			// default display argument is uncompleted
			if (arg.equals(DisplayType.UNCOMPLETED.toString())) {
				retrievedTaskList = master.getTaskListByCompletion(false);
				viewState.setTaskList(retrievedTaskList);
				viewState.setHeader(
						String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS,
						String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				viewState.addAction(new Action(ActionType.SCROLL_TASK_LIST_TO_TOP, null));
				LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
				
			} else if (arg.equals(DisplayType.COMPLETED.toString())) {
				retrievedTaskList = master.getTaskListByCompletion(true);
				viewState.setTaskList(retrievedTaskList);
				viewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				viewState.addAction(new Action(ActionType.SCROLL_TASK_LIST_TO_TOP, null));
				LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
				
			} else if (arg.equals(DisplayType.ALL.toString())) {
				viewState.setTaskList(master);
				viewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY, arg));
				viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				viewState.addAction(new Action(ActionType.SCROLL_TASK_LIST_TO_TOP, null));
				LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_DISPLAY, arg));
				setExecuted(true);
				
			} else if (arg.equals(DisplayType.INVALID.toString())) {
				viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_DISPLAY_INVALID_ARGUMENT);
				LogHelper.getInstance().getLogger().info(ViewConstants.ERROR_DISPLAY_INVALID_ARGUMENT);
			}
			
			viewState.setActiveView(ViewType.TASK_LIST);
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_DISPLAY));
		}
		
		return viewState;
	}

	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
		return previousViewState;
	}
}
