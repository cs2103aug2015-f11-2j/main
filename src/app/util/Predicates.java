package app.util;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import app.constants.TaskConstants.Priority;
import app.model.Task;

public class Predicates {
	/**
	 * Predicate<Task> for specified end before date
	 * 
	 * @param end The end time used for match
	 * @return Predicate<Task> that ends before specified date
	 */
	public static Predicate<Task> endDateBefore(LocalDateTime end) {
		return t -> (t.getEndDate() != null) ? (t.getEndDate().isBefore(i) || t.getEndDate().isEqual(i)) : false;
	}

	/**
	 * Predicate<Task> for specified start after date
	 * 
	 * @param start The start time used for match
	 * @return Predicate<Task> that starts after specified date
	 */
	public static Predicate<Task> startDateAfter(LocalDateTime start) {
		return t -> (t.getEndDate() != null) ? (t.getEndDate().isAfter(i) || t.getEndDate().isEqual(i)) : false;
	}

	/**
	 * Predicate<Task> for specified start and end time
	 * 
	 * @param start The start time of task 
	 * @param end The end time of task 
	 * @return Predicate<Task> that falls between specified start and end dates
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
	 * Predicate<Task> for specified priority
	 * 
	 * @param priority The Priority used for match
	 * @return Predicate<Task> with specified priority
	 */
	public static Predicate<Task> priorityEquals(Priority priority) {
		return t -> (t.getPriority() != null)
				? (t.getPriority().toString().toLowerCase().equalsIgnoreCase(priority.toString().toLowerCase()))
				: false;
	}

	/**
	 * Predicate<Task> for keyword matches
	 * 
	 * @param keyword The keyword used for match
	 * @return Predicate<Task> with specified keyword
	 */
	public static Predicate<Task> keywordMatches(String keyword) {
		// return t -> t.getName().toLowerCase().matches(".*\\b" +
		// keyword.toLowerCase() + "\\b.*");
		return t -> t.getName().toLowerCase().contains(keyword.toLowerCase());
	}
}
