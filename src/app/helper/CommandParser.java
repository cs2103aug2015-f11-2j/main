package app.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import app.constants.CommandConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.Priority;
import app.model.command.Command;
import app.model.command.CommandAdd;
import app.model.command.CommandInvalid;
import app.model.command.CommandTheme;

public class CommandParser {

	private static final List<String> PRIORITY_KEYWORDS = getUnmodifiableList("priority");
	private static final List<String> START_DATE_KEYWORDS = getUnmodifiableList("start", "from", "begin");
	private static final List<String> END_DATE_KEYWORDS = getUnmodifiableList("by", "due", "end", "to");
	private static final List<String> DATE_PATTERNS = getUnmodifiableList("dd/MM/yy", "d/MM/yy", "dd/M/yy", "d/M/yy",
			"dd/MM/yyyy", "d/MM/yyyy", "dd/M/yyyy", "d/M/yyyy", "dd-MM-yy", "d-MM-yy", "dd-M-yy", "d-M-yy",
			"dd-MM-yyyy", "d-MM-yyyy", "dd-M-yyyy", "d-M-yyyy");
	private static final List<String> TIME_PATTERNS = getUnmodifiableList("h:mma", "hh:mma", "HHmm", "Hmm", "HHmm'hrs'",
			"Hmm'hrs'", "ha", "hha");
	private static final List<String> DAY_PATTERNS = getUnmodifiableList("EEEE", "EEE");
	private static final List<String> PRIORITY_LEVELS = getUnmodifiableList("high", "medium", "low");

	private List<String> allKeywords;

	public CommandParser() {
		allKeywords = new ArrayList<String>();
		allKeywords.addAll(START_DATE_KEYWORDS);
		allKeywords.addAll(END_DATE_KEYWORDS);
		allKeywords.addAll(PRIORITY_KEYWORDS);
	}

	/**
	 * This method takes an input command string and parses it appropriately
	 * based on its determined CommandType.
	 * 
	 * @param commandString The command string to parse
	 * @return The constructed Command object with parameters set
	 */
	public Command parseCommand(String commandString) {
		Command cmd = createCommand(commandString);
		cmd.setCommandString(commandString);
		cmd.setContent(removeFirstWord(cmd.getCommandString()));

		switch (cmd.getCommandType()) {
		case ADD:
			parseCommandParams(cmd);
			break;
		default:
			break;
		}

		return cmd;
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
	private void parseCommandParams(Command cmd) {
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
		String startDateString = getStringFromArrayIndexRange(startDateStart + 1, startDateEnd, arr);
		String endDateString = getStringFromArrayIndexRange(endDateStart + 1, endDateEnd, arr);
		Date parsedStart = determineDate(startDateString);
		Date parsedEnd = determineDate(endDateString, parsedStart);

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
			if (!betweenInclusive(i, priorityStart, priorityEnd) && !betweenInclusive(i, startDateStart, startDateEnd)
					&& !betweenInclusive(i, endDateStart, endDateEnd)) {
				contentEnd = i;
			}
		}
		// Remove any tokens we merged over.
		if (priorityEnd < contentEnd) {
			priorityStart = priorityEnd = -1;
		}
		if (startDateEnd < contentEnd) {
			startDateStart = startDateEnd = -1;
		}
		if (endDateEnd < contentEnd) {
			endDateStart = endDateEnd = -1;
		}

		// Builds the content from the token indexes
		// Parses the priority level if exists
		String content = getStringFromArrayIndexRange(contentStart, contentEnd, arr);
		String priorityString = getStringFromArrayIndexRange(priorityStart, priorityEnd, arr);
		Priority priority = getPriority(priorityString);

		// Sets the parsed parameters
		cmd.setContent(content);
		cmd.setPriority(priority);
		cmd.setStartDate(parsedStart);
		cmd.setEndDate(parsedEnd);
	}

	/**
	 * Builds a string from a string array using specified start/end indexes.
	 * The start and end indexes are both inclusive.
	 * 
	 * @param start The start index
	 * @param end The end index
	 * @param array The array of strings
	 * @return The constructed string
	 */
	private String getStringFromArrayIndexRange(int start, int end, String[] array) {
		String result = "";
		for (int i = start; i < array.length && i >= 0 && i <= end; i++) {
			result += array[i] + " ";
		}
		return result.trim();
	}

	/**
	 * Returns the Priority object representing the priority level
	 * 
	 * @param priorityString The priority level as a string
	 * @return The corresponding priority level
	 */
	private Priority getPriority(String priorityString) {
		assert priorityString.length() == 2;
		String priorityLevel = removeFirstWord(priorityString);
		if (priorityLevel.contains("high")) {
			return Priority.HIGH;
		} else if (priorityLevel.contains("medium")) {
			return Priority.MEDIUM;
		} else if (priorityLevel.contains("low")) {
			return Priority.LOW;
		}
		return null;
	}

	/**
	 * Compares the subject with a given range and returns true if subject is
	 * between or equal to lower and upper indexes.
	 * 
	 * @param subject The subject of the comparison
	 * @param lower The lower index
	 * @param upper The upper index
	 * @return True if subject is between or equal to lower and upper
	 */
	private boolean betweenInclusive(int subject, int lower, int upper) {
		boolean result = (subject >= lower && subject <= upper);
		return result;
	}

	/**
	 * Tries to return a Date object from a given string representation. No
	 * reference date is specified.
	 * 
	 * @param dateString The string representation of the date
	 * @return Date if date can be parsed, else null
	 */
	private Date determineDate(String dateString) {
		return determineDate(dateString, null);
	}

	/**
	 * Tries to return a Date object from a given string representation. A
	 * reference date is specified. For instance, "3pm" would result in a date
	 * equal to the reference date but with a time of "3pm".
	 * 
	 * @param dateString The string representation of the date
	 * @return Date if date can be parsed, else null
	 */
	@SuppressWarnings("deprecation")
	private Date determineDate(String dateString, Date reference) {
		Date date = null;

		// Special case: now
		if (dateString.equalsIgnoreCase("now")) {
			date = new Date();
			return date;
		}

		// check date patterns only
		for (String datePattern : DATE_PATTERNS) {
			date = getDateFromPattern(dateString, datePattern);
			if (date != null) {
				return date;
			}
			// check date + time patterns
			for (String timePattern : TIME_PATTERNS) {
				date = getDateFromPattern(dateString, datePattern + " " + timePattern);
				if (date != null) {
					return date;
				}
			}
		}

		// check time patterns only
		for (String timePattern : TIME_PATTERNS) {
			date = getDateFromPattern(dateString, timePattern);
			if (date != null) {
				if (reference != null) {
					Date newDate = (Date) reference.clone();
					newDate.setHours(date.getHours());
					newDate.setMinutes(date.getMinutes());
					return newDate;
				} else {
					Date todayDate = new Date();
					todayDate.setHours(date.getHours());
					todayDate.setMinutes(date.getMinutes());
					return todayDate;
				}
			}

			// check day + time patterns
			for (String dayPattern : DAY_PATTERNS) {
				date = getDateFromPattern(dateString, dayPattern + " " + timePattern);
				if (date != null) {
					date = buildDateWithNearestDay(date.getDay(), date.getHours(), date.getMinutes());
					return date;
				}
			}
		}

		// check day pattern only
		for (String dayPattern : DAY_PATTERNS) {
			date = getDateFromPattern(dateString, dayPattern);
			if (date != null) {
				date = buildDateWithNearestDay(date.getDay(), date.getHours(), date.getMinutes());
				return date;
			}
		}
		return null;
	}

	/**
	 * Builds a Date object with the day set to the nearest day specified by the
	 * 'day' parameter. The hours and minutes of the Date object are also set as
	 * specified.
	 * 
	 * For example, if 'day' is 2, the returned date is the first Tuesday from
	 * now.
	 * 
	 * @param day An integer indicating the day: 0 -> sunday, 1 -> monday, ...,
	 *            6 -> saturday
	 * @param hours The hours to set the date to
	 * @param minutes The minutes to set the date to
	 * @return The constructed date with the day, hours, and minutes set.
	 */
	private Date buildDateWithNearestDay(int day, int hours, int minutes) {
		Date date = new Date();
		date.setHours(hours);
		date.setMinutes(minutes);
		int dayOffset = (day - date.getDay()) % 7;
		date.setDate(date.getDate() + dayOffset);
		return date;
	}

	/**
	 * Tries to parse a string representation of a date using the given pattern.
	 * 
	 * @param dateString The string representation of the date
	 * @param pattern The pattern to try parsing with
	 * @return Date if date can be parsed, else null
	 */
	private Date getDateFromPattern(String dateString, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			Date date = sdf.parse(dateString);
			String dateTimeFormat = sdf.format(date);
			if (dateTimeFormat.equalsIgnoreCase(dateString)) {
				return date;
			}
		} catch (ParseException e) {
			// nothing
		}
		return null;
	}

	/**
	 * Creates the relevant Command subclass based on the specified command
	 * string. The created subclass only has its commandType variable set.
	 * 
	 * @param commandString The command string
	 * @return The relevant Command subclass with appropriate commandType
	 *         variable set.
	 */
	private Command createCommand(String commandString) {
		CommandType commandType = determineCommandType(commandString);

		switch (commandType) {
		case ADD:
			return new CommandAdd(commandType);
		case THEME:
			return new CommandTheme(commandType);
		case INVALID: // Intentional fall-through and default case
		default:
			return new CommandInvalid(commandType);
		}
	}

	/**
	 * Determines the CommandType of the specified command string
	 * 
	 * @param commandString The command string
	 * @return The determined CommandType object
	 */
	private CommandType determineCommandType(String commandString) {
		String word = getFirstWord(commandString).toLowerCase();
		if (CommandConstants.ALIASES_ADD.contains(word)) {
			return CommandType.ADD;
		} else if (CommandConstants.ALIASES_REMOVE.contains(word)) {
			return CommandType.REMOVE;
		} else if (CommandConstants.ALIASES_THEME.contains(word)) {
			return CommandType.THEME;
		} else if (CommandConstants.ALIASES_HELP.contains(word)) {
			return CommandType.HELP;
		}
		return CommandType.INVALID;
	}

	/**
	 * Returns the first whitespace delimited word of the specified string.
	 * 
	 * @param words The string to get the first word from
	 * @return The first word of the specified string
	 */
	public static String getFirstWord(String words) {
		return words.trim().split("\\s+")[0];
	}

	/**
	 * Removes the first whitespace delimited word of the specified string.
	 * 
	 * @param commandString The string to remove the first word from
	 * @return The resultant string without the first word
	 */
	private static String removeFirstWord(String commandString) {
		return commandString.replace(getFirstWord(commandString), "").trim();
	}

	/**
	 * Builds a read-only list from the given arguments.
	 * 
	 * @param args Elements used to create the list
	 * @return A read-only list with the specified elements
	 */
	private static List<String> getUnmodifiableList(String... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}
}
