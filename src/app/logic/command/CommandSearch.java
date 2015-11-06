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
import app.util.Predicates;
import app.util.LogHelper;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

public class CommandSearch extends Command {

	private ViewState previousViewState;
	
	// @@author A0132764E
	public CommandSearch() {
		super();
		this.setCommandType(CommandType.SEARCH);
	}
	
	// @@author A0132764E
	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info("Executing CommandSearch object.");

		this.previousViewState = new ViewState(previousViewState);
		ViewState viewState = new ViewState();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList retrievedTaskList = master.getTaskListByCompletion(false);

		LogHelper.getInstance().getLogger()
				.info("\nContent: " + this.getContent() + "\nPriority: " + this.getPriority() + "\nStart: "
						+ this.getStartDate() + "\nEnd: " + this.getEndDate() + "\nType: " + this.getDisplayType());

		try {
			if (this.getDisplayType() == DisplayType.ALL) {
				retrievedTaskList = master;
			} else if (this.getDisplayType() == DisplayType.COMPLETED) {
				retrievedTaskList = master.getTaskListByCompletion(true);
			} 

			List<Predicate<Task>> predicates = new ArrayList<Predicate<Task>>();
			if (!this.getContent().isEmpty()) {
				predicates.add(Predicates.keywordMatches(this.getContent()));
			}
			if (this.getPriority() != null) {
				predicates.add(Predicates.priorityEquals(this.getPriority()));
			}
			if (this.getStartDate() != null && this.getEndDate() != null) {
				predicates.add(Predicates.betweenDates(this.getStartDate(), this.getEndDate()));
			} else if (this.getEndDate() != null) {
					predicates.add(Predicates.endDateBefore(this.getEndDate()));
			} else if (this.getStartDate() != null) {
					predicates.add(Predicates.startDateAfter(this.getStartDate()));
			}

			
			TaskList results = retrievedTaskList.search(predicates);
			
			viewState.setTaskList(results);
			viewState.setHeader(ViewConstants.HEADER_SEARCH);
			
			viewState.setStatus(StatusType.SUCCESS,
					String.format(ViewConstants.MESSAGE_SEARCH, results.getTaskList().size()));
			
			if (predicates.isEmpty() && this.getDisplayType() == null){
				viewState.setStatus(StatusType.SUCCESS,ViewConstants.ERROR_SEARCH_NO_PARAMETER);
			}
			this.setExecuted(true);
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe("Error: "+e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_SEARCH, this.getContent()));
		}
		
		return viewState;
	}
	
	//TODO: Kenny/Benjamin?
	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}

		return previousViewState;
	}
}
