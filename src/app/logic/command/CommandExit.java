package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.model.ViewState;

public class CommandExit extends Command {

	public CommandExit() {
		super();
		this.setCommandType(CommandType.EXIT);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		System.exit(0);
		// Will never return
		return new ViewState();
	}

	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
		
		// TODO: undo code here
		return new ViewState();
	}
}
