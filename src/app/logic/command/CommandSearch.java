package app.logic.command;

import app.constants.ViewConstants;

import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;
import app.parser.CommandParser;
import app.util.LogHelper;
import javafx.collections.ObservableList;

import java.util.List;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Predicate;

public class CommandSearch extends Command {

	public CommandSearch() {
		super();
		this.setCommandType(CommandType.SEARCH);
		List<Predicate<Task>> predicates = new ArrayList<Predicate<Task>>();
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getLogger().info("Executing CommandSearch object.");

		ViewState viewState = new ViewState();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList retrievedTaskList = master;

		if (getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_SEARCH_NO_PARAMETER);
			return viewState;
		}
		LogHelper.getLogger().info("\nContent: " + this.getContent() + "\nPriority: " + this.getPriority() + "\nStart: "
				+ this.getStartDate() + "\nEnd: " + this.getEndDate() + "\nType: " + this.getDisplayType());

		if (this.getDisplayType() == DisplayType.UNCOMPLETED) {
			retrievedTaskList = master.getTaskListByCompletion(false);
			LogHelper.getLogger().info("Uncomp");
		} else if (this.getDisplayType() == DisplayType.COMPLETED) {
			retrievedTaskList = master.getTaskListByCompletion(true);
			LogHelper.getLogger().info("Comp");
		}

		List<Predicate<Task>> predicates = new ArrayList<Predicate<Task>>();
		predicates.add(keywordMatches(this.getContent()));
		if (!this.getPriority().equals(Priority.NONE)) {
			predicates.add(priorityEquals(this.getPriority()));
		}
		if (!(this.getStartDate() == null) && !(this.getEndDate() == null)) {
			predicates.add(betweenDates(this.getStartDate(), this.getEndDate()));
		} else {
			if (this.getEndDate() != null)
				predicates.add(endDateBefore(this.getEndDate()));
			if (this.getStartDate() != null)
				predicates.add(startDateAfter(this.getStartDate()));
		}

		TaskList results = retrievedTaskList.search(predicates);
		viewState.setTaskList(results);
		viewState.setHeader(ViewConstants.HEADER_SEARCH);
		viewState.setStatus(StatusType.SUCCESS,
				String.format(ViewConstants.SEARCH_MESSAGE, results.getTaskList().size()));
		setExecuted(true);
		return viewState;
	}

	private static Predicate<Task> endDateBefore(LocalDateTime i) {
		return t -> (t.getEndDate() != null) ? (t.getEndDate().isBefore(i) || t.getEndDate().isEqual(i)) : false;
	}

	private static Predicate<Task> startDateAfter(LocalDateTime i) {
		return t -> (t.getStartDate() != null) ? (t.getStartDate().isAfter(i) || t.getStartDate().isEqual(i)) : false;
	}

	private static Predicate<Task> betweenDates(LocalDateTime start, LocalDateTime end) {
		return t -> (t.getStartDate() != null && t.getEndDate() != null)
				? ((t.getStartDate().isBefore(end) || t.getStartDate().isEqual(end))
						&& (t.getEndDate().isAfter(start) || t.getEndDate().isEqual(start)))
				: false;
	}

	private static Predicate<Task> priorityEquals(Priority priority) {
		return t -> (t.getPriority() != null)
				? (t.getPriority().toString().toLowerCase().equalsIgnoreCase(priority.toString().toLowerCase()))
				: false;
	}

	private static Predicate<Task> keywordMatches(String keyword) {
		return t -> t.getName().toLowerCase().matches(".*\\b" + keyword.toLowerCase() + "\\b.*");
	}
}
