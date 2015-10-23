package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ViewType;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandUndo extends Command {
	
	public CommandUndo() {
		super();
		this.setCommandType(CommandType.UNDO);
	}
	
	
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
}
