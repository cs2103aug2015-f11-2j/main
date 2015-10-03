package app.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import app.constants.CommandConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.logic.command.Command;
import app.util.Common;

public class CommandParser {

	private static final List<String> PRIORITY_KEYWORDS = Common.getUnmodifiableList("priority");
	private static final List<String> START_DATE_KEYWORDS = Common.getUnmodifiableList("start", "from", "begin");
	private static final List<String> END_DATE_KEYWORDS = Common.getUnmodifiableList("by", "due", "end", "to");
	private static final List<String> PRIORITY_LEVELS = Common.getUnmodifiableList("high", "medium", "low");

	private static final List<String> DISPLAY_COMPLETED = Common.getUnmodifiableList("c", "comp", "complete",
			"completed");
	private static final List<String> DISPLAY_UNCOMPLETED = Common.getUnmodifiableList("p", "pend", "pending", "i",
			"incomp", "incomplete", "u", "uncomp", "uncompleted");
	private static final List<String> DISPLAY_ALL = Common.getUnmodifiableList("a", "al", "all");

	private static List<String> allKeywords;

	static {
		allKeywords = new ArrayList<String>();
		allKeywords.addAll(START_DATE_KEYWORDS);
		allKeywords.addAll(END_DATE_KEYWORDS);
		allKeywords.addAll(PRIORITY_KEYWORDS);
	}

	/**
	 * Parses and sets parameters for the Command object specified. The
	 * commandString of the specified Command object should already be set
	 * before calling this method.
	 * 
	 * The following parameters are set:
	 * 
	 * - Content, and if exists, startDate, endDate, priority.
	 * 
	 * @param cmd The Command object to set parameters for
	 */
	public static void parseDatesAndPriority(Command cmd) {
		String[] arr = cmd.getCommandString().split(" ");

		int startDateStart = -1;
		int startDateEnd = -1;
		int endDateStart = -1;
		int endDateEnd = -1;

		int priorityStart = -1;
		int priorityEnd = -1;

		int contentStart = 1; // 2nd word
		int contentEnd = -1;

		/*
		 * Tokenize the input string into the following tokens:
		 * 
		 * PRIORITY, START_DATE ("from <date>"), END_DATE ("due <date")
		 * 
		 * For "from <date> to <date>" ranges, START_DATE and END_DATE tokens
		 * will always be touching.
		 * 
		 * Example result:
		 * 
		 * Input: ---------- add buy milk from store from 3pm to 5pm
		 * 
		 * Tokenized output: [add] [buy milk from store] [from 3pm] [to 5pm]
		 */
		for (int i = 0; i < arr.length; i++) {
			if (START_DATE_KEYWORDS.contains(arr[i])) {
				boolean endFound = false;
				startDateStart = i;
				int j = i + 1;
				while (j < arr.length && !START_DATE_KEYWORDS.contains(arr[j]) && !PRIORITY_KEYWORDS.contains(arr[j])) {
					if (END_DATE_KEYWORDS.contains(arr[j])) {
						if (endFound) {
							break; // break if we hit a 2nd occurrence
						}
						startDateEnd = j - 1;
						endDateStart = j;
						endFound = true;
						i = j++;
						continue;
					}
					endDateEnd = j;
					i = j++;
				}
			} else if (END_DATE_KEYWORDS.contains(arr[i])) {
				startDateStart = startDateEnd = -1; // reset
				endDateStart = i;
				int j = i + 1;
				while (j < arr.length && !allKeywords.contains(arr[j])) {
					endDateEnd = j;
					i = j++;
				}
			} else if (PRIORITY_KEYWORDS.contains(arr[i])) {
				if (i + 1 < arr.length && PRIORITY_LEVELS.contains(arr[i + 1])) {
					priorityStart = i;
					priorityEnd = i + 1;
					i++;
				}
			}
		}

		// Try to parse the dates detected.
		String startDateString = Common.getStringFromArrayIndexRange(startDateStart + 1, startDateEnd, arr);
		String endDateString = Common.getStringFromArrayIndexRange(endDateStart + 1, endDateEnd, arr);
		LocalDateTime parsedStart = DateParser.determineStartDate(startDateString);
		LocalDateTime parsedEnd = DateParser.determineEndDate(endDateString, parsedStart);

		/*
		 * if start/end date is detected but cannot be parsed, we treat it as
		 * part of the content instead.
		 */
		if (!startDateString.isEmpty() && parsedStart == null) {
			startDateStart = startDateEnd = -1;
		}
		if (!endDateString.isEmpty() && parsedEnd == null) {
			endDateStart = endDateEnd = -1;
		}

		/*
		 * If start date is after end date, the date range is invalid and is
		 * removed.
		 */
		if (parsedStart != null && parsedEnd != null && !parsedStart.isBefore(parsedEnd)) {
			startDateStart = startDateEnd = endDateStart = endDateEnd = -1;
			parsedStart = parsedEnd = null;
		}

		/*
		 * Merge disjointed content tokens. For example:
		 * 
		 * [ADD] [CONTENT] [PRIORITY] [CONTENT] [DEADLINE]
		 * 
		 * The priority token should be considered part of the content, hence:
		 * 
		 * [ADD] [--------- CONTENT ----------] [DEADLINE]
		 * 
		 */
		for (int i = 1; i < arr.length; i++) {
			if (!Common.betweenInclusive(i, priorityStart, priorityEnd)
					&& !Common.betweenInclusive(i, startDateStart, startDateEnd)
					&& !Common.betweenInclusive(i, endDateStart, endDateEnd)) {
				contentEnd = i;
			}
		}

		// Remove any tokens we merged over.
		if (priorityEnd < contentEnd) {
			priorityStart = priorityEnd = -1;
		}
		if (startDateEnd < contentEnd) {
			startDateStart = startDateEnd = -1;
			parsedStart = null;
		}
		if (endDateEnd < contentEnd) {
			endDateStart = endDateEnd = -1;
			parsedEnd = null;
		}

		// Builds the content from the token indexes
		// Parses the priority level if exists
		String content = Common.getStringFromArrayIndexRange(contentStart, contentEnd, arr);
		String priorityString = Common.getStringFromArrayIndexRange(priorityStart, priorityEnd, arr);
		Priority priority = getPriority(priorityString);

		// Sets the parsed parameters
		cmd.setContent(content);
		cmd.setPriority(priority);
		cmd.setStartDate(parsedStart);
		cmd.setEndDate(parsedEnd);
	}

	/**
	 * Returns the Priority object representing the priority level
	 * 
	 * @param priorityString The priority level as a string
	 * @return The corresponding priority level
	 */
	private static Priority getPriority(String priorityString) {
		assert priorityString.length() == 2;
		String priorityLevel = Common.removeFirstWord(priorityString);
		if (priorityLevel.contains("high")) {
			return Priority.HIGH;
		} else if (priorityLevel.contains("medium")) {
			return Priority.MEDIUM;
		} else if (priorityLevel.contains("low")) {
			return Priority.LOW;
		}
		return Priority.NONE;
	}

	/**
	 * Determine the display argument from the entered string
	 * 
	 * @param arg The specified display option
	 * @return The specified DisplayType parsed from arg
	 */
	public static DisplayType determineDisplayType(String arg) {
		String type = arg.toLowerCase().trim();
		if (DISPLAY_COMPLETED.contains(type)) {
			return DisplayType.COMPLETED;
		} else if (DISPLAY_UNCOMPLETED.contains(type)) {
			return DisplayType.UNCOMPLETED;
		} else if (DISPLAY_ALL.contains(type)) {
			return DisplayType.ALL;
		}
		return DisplayType.INVALID;
	}
	
	/**
	 * Parses the content of a task from the Edit command to filter out the task id
	 * 
	 * @param content The content or name of a task
	 * @return The displayed Id of the task
	 */
	public static int getTaskDisplayedIdFromContent(String content) throws NumberFormatException {
		int displayedId;
		if (content.contains(" ")) {
			displayedId = Integer.parseInt(content.substring(0, content.indexOf(" ")));
		} else {
			displayedId = Integer.parseInt(content);
		}
		return displayedId;
	}
	
	/**
	 * Parses the content of a task from the Edit command to filter out the task id
	 * 
	 * @param id The displayed Id of the task
	 * @param content The description or name of the task
	 * @return The correct description or name of the task if it exist
	 */
	public static String getTaskDescFromContent(int id, String content) {
		String desc = "";
		if (content.contains(" ")) {
			desc = content.substring(content.indexOf(" "));
		} else {
			return null;
		}
		return desc.trim();
	}

}