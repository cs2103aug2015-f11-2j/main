package app.logic.command;

import java.util.Stack;

import app.constants.CommandConstants.CommandType;
import app.logic.CommandController;
import app.model.ViewState;

public class CommandUndo extends Command {

	public CommandUndo() {
		super();
		this.setCommandType(CommandType.UNDO);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		// Things to handle:
		// 1. What if stack is empty? (i.e. no commands to undo)
		// 2. Currently the whole state is reverted, including the previous
		// status message. You should set a new status message relating the
		// success/failure of the undo command.
		Stack<Command> executedCommands = CommandController.getInstance().getExecutedCommands();
		Command cmd = executedCommands.pop();
		ViewState undoneViewState = cmd.undo();
		setExecuted(true);
		return undoneViewState;
	}

	@Override
	public ViewState undo() {
		// TODO Auto-generated method stub
		return null;
	}

}
