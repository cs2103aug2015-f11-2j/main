package app.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import app.constants.CommandConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.logic.command.Command;
import app.util.Common;

class Token {
	int start = -1;
	int end = -1;

	public boolean isEmpty() {
		return (start == -1 || end == -1);
	}

	public void clear() {
		start = end = -1;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
}

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
	private static final List<String> DISPLAY_TYPE_KEYWORDS = Common.getUnmodifiableList("type");

	private static final List<String> SEARCH_START_DATE_KEYWORDS = Common.getUnmodifiableList("after", "since");
	private static final List<String> SEARCH_END_DATE_KEYWORDS = Common.getUnmodifiableList("before");
	private static final List<String> SEARCH_START_DATERANGE_KEYWORDS = Common.getUnmodifiableList("between");
	private static final List<String> SEARCH_END_DATERANGE_KEYWORDS = Common.getUnmodifiableList("and");

	private static List<String> allKeywords;
	private static List<String> allSearchKeywords;
	private static List<String> displayTypeKeywords;

	static {
		allKeywords = new ArrayList<String>();
		allKeywords.addAll(START_DATE_KEYWORDS);
		allKeywords.addAll(END_DATE_KEYWORDS);
		allKeywords.addAll(PRIORITY_KEYWORDS);

		allSearchKeywords = new ArrayList<String>();
		allSearchKeywords.addAll(SEARCH_START_DATE_KEYWORDS);
		allSearchKeywords.addAll(SEARCH_END_DATE_KEYWORDS);
		allSearchKeywords.addAll(SEARCH_START_DATERANGE_KEYWORDS);
		allSearchKeywords.addAll(PRIORITY_KEYWORDS);
		allSearchKeywords.addAll(DISPLAY_TYPE_KEYWORDS);

		displayTypeKeywords = new ArrayList<String>();
		displayTypeKeywords.addAll(DISPLAY_COMPLETED);
		displayTypeKeywords.addAll(DISPLAY_UNCOMPLETED);
		displayTypeKeywords.addAll(DISPLAY_ALL);
	}

	public static Token searchStartToken(String[] arr) {
		Token token = new Token();
		for (int i = 0; i < arr.length; i++) {
			if (SEARCH_START_DATE_KEYWORDS.contains(arr[i])) {
				token.setStart(i);
				int j = i + 1;
				while (j < arr.length && !allSearchKeywords.contains(arr[j])) {
					token.setEnd(j);
					i = j++;
				}
			}
		}
		return token;
	}

	public static Token startToken(String[] arr) {
		Token token = new Token();
		for (int i = 0; i < arr.length; i++) {
			if (START_DATE_KEYWORDS.contains(arr[i])) {
				token.setStart(i);
				int j = i + 1;
				while (j < arr.length && !allKeywords.contains(arr[j])) {
					token.setEnd(j);
					i = j++;
				}
			}
		}
		return token;
	}

	public static Token endToken(String[] arr) {
		Token token = new Token();
		for (int i = 0; i < arr.length; i++) {
			if (END_DATE_KEYWORDS.contains(arr[i])) {
				token.setStart(i);
				int j = i + 1;
				while (j < arr.length && !allKeywords.contains(arr[j])) {
					token.setEnd(j);
					i = j++;
				}
			}
		}
		return token;
	}

	public static Token searchEndToken(String[] arr) {
		Token token = new Token();
		for (int i = 0; i < arr.length; i++) {
			if (SEARCH_END_DATE_KEYWORDS.contains(arr[i])) {
				token.setStart(i);
				int j = i + 1;
				while (j < arr.length && !allSearchKeywords.contains(arr[j])) {
					token.setEnd(j);
					i = j++;
				}
			}
		}
		return token;
	}

	public static Token priorityToken(String[] arr) {
		Token token = new Token();
		for (int i = 0; i < arr.length; i++) {
			if (PRIORITY_KEYWORDS.contains(arr[i])) {
				if (i + 1 < arr.length && PRIORITY_LEVELS.contains(arr[i + 1])) {
					token.setStart(i);
					token.setEnd(i + 1);
					i++;
				}
			}
		}
		return token;
	}

	public static Token displayTypeToken(String[] arr) {
		Token token = new Token();
		for (int i = 0; i < arr.length; i++) {
			if (DISPLAY_TYPE_KEYWORDS.contains(arr[i])) {
				if (i + 1 < arr.length && displayTypeKeywords.contains(arr[i + 1])) {
					token.setStart(i);
					token.setEnd(i + 1);
					i++;
				}
			}
		}
		return token;
	}

	public static void parseSearch(Command cmd) {
		String[] arr = cmd.getContent().split(" ");
		Token contentToken = new Token();
		contentToken.setStart(0);

		Token startToken = searchStartToken(arr);
		Token endToken = searchEndToken(arr);
		Token priorityToken = priorityToken(arr);
		Token displayToken = displayTypeToken(arr);

		// only keep the last start or end token
		if (endToken.getStart() > startToken.getEnd()) {
			startToken.clear();
		} else if (startToken.getStart() > endToken.getStart()) {
			endToken.clear();
		}

		// Try to parse the dates detected.
		String startDateString = Common.getStringFromArrayIndexRange(startToken.getStart() + 1, startToken.getEnd(),
				arr);
		String endDateString = Common.getStringFromArrayIndexRange(endToken.getStart() + 1, endToken.getEnd(), arr);
		LocalDateTime parsedStart = DateParser.determineStartDate(startDateString);
		LocalDateTime parsedEnd = DateParser.determineEndDate(endDateString, parsedStart);

		/*
		 * if start/end date is detected but cannot be parsed, we treat it as
		 * part of the content instead.
		 */
		if (!startToken.isEmpty() && parsedStart == null) {
			startToken.clear();
		}
		if (!endToken.isEmpty() && parsedEnd == null) {
			endToken.clear();
		}

		/*
		 * If start date is after end date, the date range is invalid and is
		 * removed.
		 */
		if (parsedStart != null && parsedEnd != null && !parsedStart.isBefore(parsedEnd)) {
			startToken.clear();
			endToken.clear();
			parsedStart = parsedEnd = null;
		}

		/*
		 * Merge disjointed content tokens. For example:
		 */
		for (int i = 1; i < arr.length; i++) {
			if (!Common.betweenInclusive(i, priorityToken.getStart(), priorityToken.getEnd())
					&& !Common.betweenInclusive(i, displayToken.getStart(), displayToken.getEnd())
					&& !Common.betweenInclusive(i, startToken.getStart(), startToken.getEnd())
					&& !Common.betweenInclusive(i, endToken.getStart(), endToken.getEnd())) {
				contentToken.setEnd(i);
			}
		}

		// Remove any tokens we merged over.
		if (startToken.getEnd() < contentToken.getEnd()) {
			startToken.clear();
			parsedStart = null;
		}
		if (endToken.getEnd() < contentToken.getEnd()) {
			endToken.clear();
			parsedEnd = null;
		}
		if (priorityToken.getEnd() < contentToken.getEnd()) {
			priorityToken.clear();
		}
		if (displayToken.getEnd() < contentToken.getEnd()) {
			displayToken.clear();
		}

		String content = Common.getStringFromArrayIndexRange(contentToken.getStart(), contentToken.getEnd(), arr);
		String priorityString = Common.getStringFromArrayIndexRange(priorityToken.getStart(), priorityToken.getEnd(),
				arr);
		String typeString = Common.getStringFromArrayIndexRange(displayToken.getStart(), displayToken.getEnd(), arr);
		Priority priority = getPriority(priorityString);

		// Sets the parsed parameters
		cmd.setContent(content);
		cmd.setPriority(priority);

		System.out.println("----");
		System.out.println(parsedStart);
		System.out.println(parsedEnd);
		System.out.println(content);
		System.out.println(priority);
		System.out.println(typeString);
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
		String[] arr = cmd.getContent().split(" ");
		Token contentToken = new Token();
		contentToken.setStart(0);

		Token startToken = startToken(arr);
		Token endToken = endToken(arr);
		Token priorityToken = priorityToken(arr);

		// Try to parse the dates detected.
		String startDateString = Common.getStringFromArrayIndexRange(startToken.getStart() + 1, startToken.getEnd(),
				arr);
		String endDateString = Common.getStringFromArrayIndexRange(endToken.getStart() + 1, endToken.getEnd(), arr);
		LocalDateTime parsedStart = DateParser.determineStartDate(startDateString);
		LocalDateTime parsedEnd = DateParser.determineEndDate(endDateString, parsedStart);

		/*
		 * if only start date exists, clear it
		 */
		if (!startToken.isEmpty() && endToken.isEmpty()) {
			startToken.clear();
		}

		/*
		 * if start and end date exists, they MUST be together (from <date> to <date>)
		 */
		if (!startToken.isEmpty() && !endToken.isEmpty() && startToken.getEnd() + 1 != endToken.getStart()) {
			startToken.clear();
		}

		/*
		 * if start/end date is detected but cannot be parsed, we treat it as
		 * part of the content instead.
		 */
		if (!startToken.isEmpty() && parsedStart == null) {
			startToken.clear();
		}
		if (!endToken.isEmpty() && parsedEnd == null) {
			endToken.clear();
		}

		/*
		 * If start date is after end date, the date range is invalid and is
		 * removed.
		 */
		if (parsedStart != null && parsedEnd != null && !parsedStart.isBefore(parsedEnd)) {
			startToken.clear();
			endToken.clear();
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
		/*
		 * Merge disjointed content tokens. For example:
		 */
		for (int i = 1; i < arr.length; i++) {
			if (!Common.betweenInclusive(i, priorityToken.getStart(), priorityToken.getEnd())
					&& !Common.betweenInclusive(i, startToken.getStart(), startToken.getEnd())
					&& !Common.betweenInclusive(i, endToken.getStart(), endToken.getEnd())) {
				contentToken.setEnd(i);
			}
		}

		// Remove any tokens we merged over.
		if (startToken.getEnd() < contentToken.getEnd()) {
			startToken.clear();
			parsedStart = null;
		}
		if (endToken.getEnd() < contentToken.getEnd()) {
			endToken.clear();
			parsedEnd = null;
		}
		if (priorityToken.getEnd() < contentToken.getEnd()) {
			priorityToken.clear();
		}

		// Builds the content from the token indexes
		// Parses the priority level if exists
		String content = Common.getStringFromArrayIndexRange(contentToken.getStart(), contentToken.getEnd(), arr);
		String priorityString = Common.getStringFromArrayIndexRange(priorityToken.getStart(), priorityToken.getEnd(),
				arr);
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

}