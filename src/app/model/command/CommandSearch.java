package app.model.command;

import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.controller.CommandController;
import app.helper.CommandParser;
import app.helper.LogHelper;
import app.model.TaskList;

public class CommandSearch extends Command {

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
		CommandParser parser = new CommandParser();
		String arg = parser.getHelpCommandDisplayArg(this.getContent());
		TaskList searchBoundList;
		if (arg.equals("completed")) {
			LogHelper.getLogger().info("Searching in completed list");
			searchBoundList = master.getTaskListByCompletion(true);
		} else if (arg.equals("uncompleted")) {
			LogHelper.getLogger().info("Searching in uncompleted list");
			searchBoundList = master.getTaskListByCompletion(false);
		} else {
			LogHelper.getLogger().info("Searching in masterlist");
			searchBoundList = master;
		}
		String[] keywords = parser.getCommandSearch(this.getContent());
		int resultsCount = 0;
		// not very elegant nor ideal at the moment, to be improved
		for (int i = 0; i < keywords.length; i++) {
			for (int j = 0; j < searchBoundList.getTaskList().size(); j++) {
				if (searchBoundList.getTaskList().get(j).getName().toLowerCase()
						.matches(".*\\b" + keywords[i].toLowerCase() + "\\b.*")) {
					resultsTaskList.addTask(searchBoundList.getTaskList().get(j));
					resultsCount++;
				}
			}
		}
		CommandController.getInstance().setDisplayedTaskList(resultsTaskList);
		CommandController.getInstance().setHeader(ViewConstants.SEARCH_RESULTS);
		setFeedback(String.format(ViewConstants.SEARCH_RESULTS_COUNT, resultsCount));
	}
}
