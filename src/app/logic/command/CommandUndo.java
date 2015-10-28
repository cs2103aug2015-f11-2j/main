package app.logic.command;

<<<<<<< HEAD
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ViewType;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandUndo extends Command {
	
=======
import java.util.Stack;

import app.constants.CommandConstants.CommandType;
import app.logic.CommandController;
import app.model.ViewState;

public class CommandUndo extends Command {

>>>>>>> origin/undo
	public CommandUndo() {
		super();
		this.setCommandType(CommandType.UNDO);
	}
<<<<<<< HEAD
	
	
	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info("Executing CommandDelete object.");
		ViewState viewState = new ViewState();
		
		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}


	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
		
		// TODO: undo code here
		return new ViewState();
	}
=======

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

>>>>>>> origin/undo
}
