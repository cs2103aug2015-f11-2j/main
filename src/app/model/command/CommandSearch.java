package app.model.command;

import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.controller.CommandController;
import app.helper.CommandParser;
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
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList resultsTaskList = new TaskList();
		String keyword = this.getContent();
		int resultsCount = 0;
		// not very elegant nor ideal at the moment, to be improved
		for (int i = 0; i < master.getTaskList().size(); i ++){
			if (master.getTaskList().get(i).getName().toLowerCase().contains(keyword.toLowerCase())) {
				resultsTaskList.addTask(master.getTaskList().get(i));
				resultsCount++;
			}
		}
		CommandController.getInstance().setDisplayedTaskList(resultsTaskList);
		CommandController.getInstance().setHeader(String.format(ViewConstants.SEARCH_RESULTS, keyword));
		setFeedback(String.format(ViewConstants.SEARCH_RESULTS_COUNT, resultsCount));
	}
}
