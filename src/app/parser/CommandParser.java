package app.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import app.constants.TaskConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.logic.command.Command;
import app.model.ParserToken;
import app.util.Common;
import app.util.LogHelper;

public class CommandParser {

	private static final List<String> PRIORITY_KEYWORDS = Common.getUnmodifiableList("priority", "p", "pri");
	private static final List<String> START_DATE_KEYWORDS = Common.getUnmodifiableList("start", "from", "begin");
	private static final List<String> END_DATE_KEYWORDS = Common.getUnmodifiableList("by", "due", "end", "to");
	private static final List<String> PRIORITY_LEVELS = Common.getUnmodifiableList("high", "medium", "low");

	private static final List<String> DISPLAY_COMPLETED = Common.getUnmodifiableList("c", "comp", "complete",
			"completed");
	private static final List<String> DISPLAY_UNCOMPLETED = Common.getUnmodifiableList("pend", "pending", "i", "incomp",
			"incomplete", "u", "uncomp", "uncompleted");
	private static final List<String> DISPLAY_ALL = Common.getUnmodifiableList("a", "al", "all");
	private static final List<String> DISPLAY_TYPE_KEYWORDS = Common.getUnmodifiableList("type");

	private static final List<String> SEARCH_START_DATE_KEYWORDS = Common.getUnmodifiableList("after", "since");
	private static final List<String> SEARCH_END_DATE_KEYWORDS = Common.getUnmodifiableList("before");
	private static final List<String> SEARCH_START_DATERANGE_KEYWORDS = Common.getUnmodifiableList("between");
	private static final List<String> SEARCH_END_DATERANGE_KEYWORDS = Common.getUnmodifiableList("and");

	private static List<String> allKeywords;
	private static List<String> allSearchKeywords;
	private static List<String> displayTypes;

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

		displayTypes = new ArrayList<String>();	
		displayTypes.addAll(DISPLAY_COMPLETED);
		displayTypes.addAll(DISPLAY_UNCOMPLETED);
		displayTypes.addAll(DISPLAY_ALL);
	}

	/**
	 * Parses and sets search-related parameters for the Command object
	 * specified. This method should only be used for the search command.
	 * 
	 * The following parameters are set:
	 * 
	 * - Content, and if exists, startDate, endDate, priority, displayType.
	 * 
	 * @param cmd The Command object to set parameters for
	 */
	public static void parseSearch(Command cmd) {
		String[] arr = cmd.getContent().split(" ");
		ParserToken contentToken = new ParserToken();
		contentToken.setStart(0);

		ParserToken startToken = dateToken(arr, SEARCH_START_DATE_KEYWORDS, allSearchKeywords);
		ParserToken endToken = dateToken(arr, SEARCH_END_DATE_KEYWORDS, allSearchKeywords);
		ParserToken priorityToken = singleArgToken(arr, PRIORITY_KEYWORDS, PRIORITY_LEVELS);
		ParserToken displayToken = singleArgToken(arr, DISPLAY_TYPE_KEYWORDS, displayTypes);

		// Daterange keywords for search: BETWEEN <date> AND <date>
		List<String> inclusiveEndKeywords = new ArrayList<String>(allSearchKeywords);
		inclusiveEndKeywords.addAll(SEARCH_END_DATERANGE_KEYWORDS);
		ParserToken rangeStartToken = dateToken(arr, SEARCH_START_DATERANGE_KEYWORDS, inclusiveEndKeywords);
		ParserToken rangeEndToken = dateToken(arr, SEARCH_END_DATERANGE_KEYWORDS, allSearchKeywords);

		// Try to parse daterange keywords
		String rangeStartDateString = Common.getStringFromArrayIndexRange(rangeStartToken.getStart() + 1,
				rangeStartToken.getEnd(), arr);
		String rangeEndDateString = Common.getStringFromArrayIndexRange(rangeEndToken.getStart() + 1,
				rangeEndToken.getEnd(), arr);
		LocalDateTime parsedRangeStart = DateParser.determineStartDate(rangeStartDateString);
		LocalDateTime parsedRangeEnd = DateParser.determineEndDate(rangeEndDateString, parsedRangeStart);

		// if exists, start & end date MUST be joined (between <date> and
		// <date>)
		if (!rangeStartToken.isEmpty() && !rangeEndToken.isEmpty()
				&& rangeStartToken.getEnd() + 1 != rangeEndToken.getStart()) {
			rangeStartToken.clear();
			rangeEndToken.clear();
		}

		// validate daterange
		clearIfCannotParse(rangeStartToken, parsedRangeStart);
		clearIfCannotParse(rangeEndToken, parsedRangeEnd);
		clearIfStartAfterEnd(rangeStartToken, rangeEndToken, parsedRangeStart, parsedRangeEnd);

		// if start/end tokens are not touching, then keep only the last
		if (!startToken.isEmpty() && !endToken.isEmpty() && startToken.getEnd() + 1 != endToken.getStart()) {
			if (endToken.getStart() > startToken.getEnd()) {
				startToken.clear();
			} else if (startToken.getStart() > endToken.getEnd()) {
				endToken.clear();
			}
		}
		// replace start and end tokens if a valid range is detected
		if (rangeStartToken.getStart() > startToken.getEnd() && rangeStartToken.getStart() > endToken.getEnd()) {
			startToken = rangeStartToken;
			endToken = rangeEndToken;
		}

		// Try to parse the dates detected.
		String startDateString = Common.getStringFromArrayIndexRange(startToken.getStart() + 1, startToken.getEnd(),
				arr);
		String endDateString = Common.getStringFromArrayIndexRange(endToken.getStart() + 1, endToken.getEnd(), arr);
		LocalDateTime parsedStart = DateParser.determineStartDate(startDateString);
		LocalDateTime parsedEnd = DateParser.determineEndDate(endDateString, parsedStart);

		// if start/end date is detected but cannot be parsed, we treat it as
		// part of the content instead.
		clearIfCannotParse(startToken, parsedStart);
		clearIfCannotParse(endToken, parsedEnd);

		// If start date > end date, the date range is invalid and is removed
		clearIfStartAfterEnd(startToken, endToken, parsedStart, parsedEnd);

		// Merge disjointed content tokens.
		updateContentEnd(contentToken, arr, priorityToken, displayToken, startToken, endToken);

		// Remove any tokens we merged over.
		clearTokensBeforeContent(contentToken, startToken, endToken, priorityToken, displayToken);
		if (startToken.isEmpty()) {
			parsedStart = null;
		}
		if (endToken.isEmpty()) {
			parsedEnd = null;
		}

		String content = Common.getStringFromArrayIndexRange(contentToken.getStart(), contentToken.getEnd(), arr);
		String priorityString = Common.getStringFromArrayIndexRange(priorityToken.getStart() + 1,
				priorityToken.getEnd(), arr);
		String typeString = Common.getStringFromArrayIndexRange(displayToken.getStart() + 1, displayToken.getEnd(),
				arr);
		Priority priority = determinePriority(priorityString);
		DisplayType type = determineDisplayType(typeString);

		// Sets the parsed parameters
		cmd.setContent(content);
		cmd.setPriority(priority);
		cmd.setStartDate(parsedStart);
		cmd.setEndDate(parsedEnd);
		cmd.setDisplayType(type);
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
		ParserToken contentToken = new ParserToken();
		contentToken.setStart(0);

		ParserToken startToken = dateToken(arr, START_DATE_KEYWORDS, allKeywords);
		ParserToken endToken = dateToken(arr, END_DATE_KEYWORDS, allKeywords);
		ParserToken priorityToken = singleArgToken(arr, PRIORITY_KEYWORDS, PRIORITY_LEVELS);

		// Try to parse the dates detected.
		String startDateString = Common.getStringFromArrayIndexRange(startToken.getStart() + 1, startToken.getEnd(),
				arr);
		String endDateString = Common.getStringFromArrayIndexRange(endToken.getStart() + 1, endToken.getEnd(), arr);
		LocalDateTime parsedStart = DateParser.determineStartDate(startDateString);
		LocalDateTime parsedEnd = DateParser.determineEndDate(endDateString, parsedStart);

		// if only start date exists, clear it
		if (!startToken.isEmpty() && endToken.isEmpty()) {
			startToken.clear();
		}

		// if exists, start & end date MUST be joined (from <date> to <date>)
		if (!startToken.isEmpty() && !endToken.isEmpty() && startToken.getEnd() + 1 != endToken.getStart()) {
			startToken.clear();
		}

		// if start/end date is detected but cannot be parsed, we treat it as
		// part of the content instead.
		clearIfCannotParse(startToken, parsedStart);
		clearIfCannotParse(endToken, parsedEnd);

		// If start date > end date, the date range is invalid and is removed
		clearIfStartAfterEnd(startToken, endToken, parsedStart, parsedEnd);

		// Merge disjointed content tokens.
		updateContentEnd(contentToken, arr, priorityToken, startToken, endToken);

		// Remove any tokens we merged over.
		clearTokensBeforeContent(contentToken, startToken, endToken, priorityToken);
		if (startToken.isEmpty()) {
			parsedStart = null;
		}
		if (endToken.isEmpty()) {
			parsedEnd = null;
		}

		// Builds the content from the token indexes
		// Parses the priority level if exists
		String content = Common.getStringFromArrayIndexRange(contentToken.getStart(), contentToken.getEnd(), arr);
		String priorityString = Common.getStringFromArrayIndexRange(priorityToken.getStart(), priorityToken.getEnd(),
				arr);
		Priority priority = determinePriority(priorityString);

		// Sets the parsed parameters
		cmd.setContent(content);
		cmd.setPriority(priority);
		cmd.setStartDate(parsedStart);
		cmd.setEndDate(parsedEnd);
	}

	/**
	 * @return A token describing a possible date string. The token is indicated
	 *         by a set of keywords to start searching from and breakpoints at
	 *         which to stop searching.
	 */
	private static ParserToken dateToken(String[] arr, List<String> keywords, List<String> breakpoints) {
		ParserToken token = new ParserToken();
		for (int i = 0; i < arr.length; i++) {
			if (keywords.contains(arr[i])) {
				token.setStart(i);
				int j = i + 1;
				while (j < arr.length && !breakpoints.contains(arr[j])) {
					token.setEnd(j);
					i = j++;
				}
			}
		}
		return token;
	}

	/**
	 * @return A token describing the last detected instance of a keyword
	 *         followed by a single argument.
	 */
	private static ParserToken singleArgToken(String[] arr, List<String> keywords, List<String> args) {
		ParserToken token = new ParserToken();
		for (int i = 0; i < arr.length; i++) {
			if (keywords.contains(arr[i])) {
				if (i + 1 < arr.length && args.contains(arr[i + 1])) {
					token.setStart(i);
					token.setEnd(i + 1);
					i++;
				}
			}
		}
		return token;
	}

	/**
	 * Clear the tokens and parsed dates if start date > end date
	 */
	private static void clearIfStartAfterEnd(ParserToken startToken, ParserToken endToken, LocalDateTime startDate,
			LocalDateTime endDate) {
		if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
			startToken.clear();
			endToken.clear();
			startDate = endDate = null;
		}
	}

	/**
	 * Clear token if the token exists yet the date cannot be parsed.
	 */
	private static void clearIfCannotParse(ParserToken token, LocalDateTime parsedDate) {
		if (!token.isEmpty() && parsedDate == null) {
			token.clear();
		}
	}

	/**
	 * Clear tokens that appear within the content token
	 */
	private static void clearTokensBeforeContent(ParserToken content, ParserToken... tokens) {
		for (ParserToken token : tokens) {
			if (token.getEnd() < content.getEnd()) {
				token.clear();
			}
		}
	}

	/**
	 * Merge disjointed content tokens. For example:
	 * 
	 * [ADD] [CONTENT] [PRIORITY] [CONTENT] [DEADLINE]
	 * 
	 * The priority token should be considered part of the content, hence:
	 * 
	 * [ADD] [--------- CONTENT ----------] [DEADLINE]
	 * 
	 */
	private static void updateContentEnd(ParserToken content, String[] arr, ParserToken... tokens) {
		for (int i = arr.length - 1; i >= 0; i--) {
			boolean isBetween = false;
			for (ParserToken token : tokens) {
				if (Common.betweenInclusive(i, token.getStart(), token.getEnd())) {
					isBetween = true;
					break;
				}
			}
			if (!isBetween) {
				content.setEnd(i);
				return;
			}
		}
	}

	/**
	 * Returns the Priority object representing the priority level
	 * 
	 * @param priorityString The priority level as a string
	 * @return The corresponding priority level
	 */
	private static Priority determinePriority(String priorityString) {
		if (priorityString.contains("high")) {
			return Priority.HIGH;
		} else if (priorityString.contains("medium")) {
			return Priority.MEDIUM;
		} else if (priorityString.contains("low")) {
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
	 * Parses the content of a task from the Edit command to filter out the task
	 * id
	 * 
	 * @param content The content or name of a task
	 * @return The displayed Id of the task
	 */
	public static int getTaskDisplayedIdFromContent(String content) throws NumberFormatException {
		return Integer.parseInt(Common.getFirstWord(content));
	}

	/**
	 * Parses the content of a task from the Edit command to filter out the task
	 * id
	 * 
	 * @param content The description or name of the task
	 * @return The correct description or name of the task if it exist
	 */
	public static String getTaskDescFromContent(String content) {
		return Common.removeFirstWord(content);
	}

}