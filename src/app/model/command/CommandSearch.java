package app.model.command;

import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.controller.CommandController;
import app.helper.LogHelper;
import app.model.Task;
import app.model.TaskList;

public class CommandSearch extends Command {
	
	private Task task;

	public CommandSearch() {
		super();
		this.setCommandType(CommandType.SEARCH);
	}

	@Override
	public void execute() {
		LogHelper.getLogger().info("Search.");
		if (this.getContent().isEmpty()) {
			setFeedback(ViewConstants.ERROR_SEARCH_NO_PARAMETER);
			setStatusType(StatusType.ERROR);
			return;
		} 
		
		//searching of sandwich starts here
		
		CommandController.getInstance().setActiveView(ViewType.TEXT_VIEW);
	}
}
