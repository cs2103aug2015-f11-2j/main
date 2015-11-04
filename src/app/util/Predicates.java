package app.util;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import app.constants.TaskConstants.Priority;
import app.model.Task;

public class Predicates {
	/**
	 * Placeholder
	 * 
	 * @param Placeholder
	 * @param Placeholder
	 * @return Placeholder
	 */
	public static Predicate<Task> endDateBefore(LocalDateTime i) {
		return t -> (t.getEndDate() != null) ? (t.getEndDate().isBefore(i) || t.getEndDate().isEqual(i)) : false;
	}

	/**
	 * Placeholder
	 * 
	 * @param Placeholder
	 * @param Placeholder
	 * @return Placeholder
	 */
	public static Predicate<Task> startDateAfter(LocalDateTime i) {
		return t -> (t.getEndDate() != null) ? (t.getEndDate().isAfter(i) || t.getEndDate().isEqual(i)) : false;
	}

	/**
	 * Placeholder
	 * 
	 * @param Placeholder
	 * @param Placeholder
	 * @return Placeholder
	 */
	public static Predicate<Task> betweenDates(LocalDateTime start, LocalDateTime end) {
		return t -> (t.getStartDate() != null
				|| t.getEndDate() != null)
						? (((t.getStartDate() != null)
								? (t.getStartDate().isEqual(start) || t.getStartDate().isAfter(start))
								: true)
								&& ((t.getEndDate() != null)
										? (t.getEndDate().isEqual(end)
												|| (t.getEndDate().isBefore(end) && t.getEndDate().isAfter(start)))
										: true))
						: false;

	}

	/**
	 * Placeholder
	 * 
	 * @param Placeholder
	 * @param Placeholder
	 * @return Placeholder
	 */
	public static Predicate<Task> priorityEquals(Priority priority) {
		return t -> (t.getPriority() != null)
				? (t.getPriority().toString().toLowerCase().equalsIgnoreCase(priority.toString().toLowerCase()))
				: false;
	}

	/**
	 * Placeholder
	 * 
	 * @param Placeholder
	 * @param Placeholder
	 * @return Placeholder
	 */
	public static Predicate<Task> keywordMatches(String keyword) {
		// return t -> t.getName().toLowerCase().matches(".*\\b" +
		// keyword.toLowerCase() + "\\b.*");
		return t -> t.getName().toLowerCase().contains(keyword.toLowerCase());
	}
}
