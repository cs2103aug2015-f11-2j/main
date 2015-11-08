package app.logic.command;

import app.constants.StorageConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ActionType;
import app.model.Action;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandExit extends Command {
	// @@author A0125990Y
	public CommandExit() {
		super();
		this.setCommandType(CommandType.EXIT);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_EXECUTE_COMMAND, "CommandExit"));
		ViewState state = new ViewState();
		state.addAction(new Action(ActionType.EXIT, null));
		setExecuted(true);
		return state;
	}

	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}

		return new ViewState();
	}
}
