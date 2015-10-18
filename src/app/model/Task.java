package app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import app.constants.TaskConstants.Priority;
import app.constants.TaskConstants.RemovableField;
import app.logic.command.Command;

public class Task implements Comparable<Task> {
	private UUID id;
	private String name;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Priority priority;
	private ArrayList<RemovableField> removeField;
	private boolean isCompleted;

	public Task(Command cmd) {
		id = UUID.randomUUID();
		name = cmd.getContent();
		startDate = cmd.getStartDate();
		endDate = cmd.getEndDate();
		priority = cmd.getPriority();
		removeField = cmd.getRemoveField();
		isCompleted = false;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isFloating() {
		return (getStartDate() == null && getEndDate() == null);
	}

	public boolean isDeadline() {
		return (getStartDate() == null && getEndDate() != null);
	}

	public boolean isEvent() {
		return (getStartDate() != null && getEndDate() != null);
	}
	
	public ArrayList<RemovableField> getRemoveField() {
		return removeField;
	}

	public LocalDateTime getSortKey() {
		if (isDeadline()) {
			return getEndDate();
		} else if (isEvent()) {
			return getStartDate();
		}
		return null;
	}

	private LocalDateTime getSecondarySortKey() {
		if (isEvent()) {
			return getEndDate();
		}
		return null;
	}

	/**
	 * Compares this task with the one specified. The sorting order is as
	 * follows:
	 * 
	 * (1) Floating tasks, (2) Start date if not null, (3) End date if not null.
	 * 
	 * For similar dates, the subsequent sorting order is used:
	 * 
	 * (1) Priority is higher, (2) Name by lexicographical ordering
	 */
	@Override
	public int compareTo(Task task) {
		LocalDateTime thisSortKey = getSortKey();
		LocalDateTime taskSortKey = task.getSortKey();
		LocalDateTime thisSecondarySortKey = getSecondarySortKey();
		LocalDateTime taskSecondarySortKey = task.getSecondarySortKey();
		int result = 0;

		// Floating is always < non-floating
		if (isFloating() && !task.isFloating()) {
			return -1;
		} else if (!isFloating() && task.isFloating()) {
			return 1;
		} else if (thisSortKey != null && taskSortKey != null) {
			// Compare primary date sort keys (events: startDate, deadlines:
			// endDate), if similar, compare secondary date sort keys (events:
			// endDate, deadlines: null)
			result = thisSortKey.compareTo(taskSortKey);
			if (result == 0 && thisSecondarySortKey != null && taskSecondarySortKey != null) {
				result = thisSecondarySortKey.compareTo(taskSecondarySortKey);
			}
		}

		// Compare priority
		if (result == 0) {
			result = getPriority().compareTo(task.getPriority());
		}

		// Compare names
		if (result == 0) {
			result = getName().compareToIgnoreCase(task.getName());
		}

		return result;
	}
}
