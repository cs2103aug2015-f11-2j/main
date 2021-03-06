package app.logic.command;

import java.util.Stack;

import app.constants.StorageConstants;
import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.logic.CommandController;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandUndo extends Command {
	// @@author A0125990Y
	public CommandUndo() {
		super();
		this.setCommandType(CommandType.UNDO);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_EXECUTE_COMMAND, "CommandUndo"));

		Stack<Command> executedCommands = CommandController.getInstance().getExecutedCommands();
		
		if (executedCommands.empty()) {
			ViewState viewState = new ViewState();
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.NO_MORE_UNDO));
			return viewState;
		}

		Command cmd = executedCommands.pop();

		ViewState undoneViewState = cmd.undo();
		setExecuted(true);

		return undoneViewState;
	}

	@Override
	public ViewState undo() {
		return null;
	}

}