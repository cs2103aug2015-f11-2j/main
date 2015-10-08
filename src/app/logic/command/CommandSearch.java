package app.logic.command;

import app.constants.ViewConstants;

import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.constants.ViewConstants.StatusType;
import app.logic.CommandController;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;
import app.util.Common;
import app.util.LogHelper;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

public class CommandSearch extends Command {

	public CommandSearch() {
		super();
		this.setCommandType(CommandType.SEARCH);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getLogger().info("Executing CommandSearch object.");

		ViewState viewState = new ViewState();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList retrievedTaskList = master.getTaskListByCompletion(false);

		if (getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_SEARCH_NO_PARAMETER);
			return viewState;
		}
		LogHelper.getLogger().info("\nContent: " + this.getContent() + "\nPriority: " + this.getPriority() + "\nStart: "
				+ this.getStartDate() + "\nEnd: " + this.getEndDate() + "\nType: " + this.getDisplayType());

		try {
		if (this.getDisplayType() == DisplayType.ALL) {
			retrievedTaskList = master;
			LogHelper.getLogger().info("All");
		} else if (this.getDisplayType() == DisplayType.COMPLETED) {
			retrievedTaskList = master.getTaskListByCompletion(true);
			LogHelper.getLogger().info("Comp");
		}

		List<Predicate<Task>> predicates = new ArrayList<Predicate<Task>>();
		predicates.add(Common.keywordMatches(this.getContent()));
		if (!this.getPriority().equals(Priority.NONE)) {
			predicates.add(Common.priorityEquals(this.getPriority()));
		}
		if (!(this.getStartDate() == null) && !(this.getEndDate() == null)) {
			predicates.add(Common.betweenDates(this.getStartDate(), this.getEndDate()));
		} else {
			if (this.getEndDate() != null) {
				predicates.add(Common.endDateBefore(this.getEndDate()));
			}
			if (this.getStartDate() != null) {
				predicates.add(Common.startDateAfter(this.getStartDate()));
			}	
		}

		TaskList results = retrievedTaskList.search(predicates);
		viewState.setTaskList(results);
		viewState.setHeader(ViewConstants.HEADER_SEARCH);
		viewState.setStatus(StatusType.SUCCESS,
				String.format(ViewConstants.SEARCH_MESSAGE, results.getTaskList().size()));
		setExecuted(true);
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_DELETE, this.getContent()));
		}
		return viewState;
	}
}
