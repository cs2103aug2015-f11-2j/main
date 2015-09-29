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

		String[] keywords = parser.getCommandSearch(this.getContent());
		int resultsCount = 0;
		// not very elegant nor ideal at the moment, to be improved
		for (int i = 0; i < keywords.length; i++) {
			for (int j = 0; j < master.getTaskList().size(); j++) {
				if (master.getTaskList().get(j).getName().toLowerCase()
						.matches(".*\\b" + keywords[i].toLowerCase() + "\\b.*")) {
					resultsTaskList.addTask(master.getTaskList().get(j));
					resultsCount++;
				}
			}
		}
		CommandController.getInstance().setDisplayedTaskList(resultsTaskList);
		CommandController.getInstance().setHeader(ViewConstants.SEARCH_RESULTS);
		setFeedback(String.format(ViewConstants.SEARCH_RESULTS_COUNT, resultsCount));
	}
}
