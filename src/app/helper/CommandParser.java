package app.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import app.constants.CommandConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.model.command.Command;

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
	private static final List<String> TOMORROW_PATTERNS = getUnmodifiableList("tomorrow", "tmr");
	private static final List<String> PRIORITY_LEVELS = getUnmodifiableList("high", "medium", "low");

	private static final List<String> DISPLAY_COMPLETED = getUnmodifiableList("c", "comp", "complete", "completed");
	private static final List<String> DISPLAY_UNCOMPLETED = getUnmodifiableList("p", "pend", "pending", "i", "incomp",
			"incomplete", "u", "uncomp", "uncompleted");
	private static final List<String> DISPLAY_ALL = getUnmodifiableList("a", "al", "all");

	private List<String> allKeywords;

	public CommandParser() {
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
	public void parseDatesAndPriority(Command cmd) {
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
		String startDateString = getStringFromArrayIndexRange(startDateStart + 1, startDateEnd, arr);
		String endDateString = getStringFromArrayIndexRange(endDateStart + 1, endDateEnd, arr);
		LocalDateTime parsedStart = determineStartDate(startDateString);
		LocalDateTime parsedEnd = determineEndDate(endDateString, parsedStart);

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
		if (parsedStart != null && parsedEnd != null && !isValidDateRange(parsedStart, parsedEnd)) {
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
			parsedStart = null;
		}
		if (endDateEnd < contentEnd) {
			endDateStart = endDateEnd = -1;
			parsedEnd = null;
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
		return Priority.NONE;
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
	 * Tries to return a LocalDateTime object from a given string representing a
	 * start date.
	 * 
	 * @param dateString The string representation of the date
	 * @return LocalDateTime if date can be parsed, else null
	 */
	private LocalDateTime determineStartDate(String dateString) {
		return determineDate(dateString, null, false);
	}

	/**
	 * Tries to return a LocalDateTime object from a given string representing
	 * an end date.
	 * 
	 * @param dateString The string representation of the date
	 * @param reference The reference used if dateString contains only time
	 * @return LocalDateTime if date can be parsed, else null
	 */
	private LocalDateTime determineEndDate(String dateString, LocalDateTime reference) {
		return determineDate(dateString, reference, true);
	}

	/**
	 * Tries to return a LocalDateTime object from a given string
	 * representation. A reference date is specified. For instance, "3pm" would
	 * result in a date equal to the reference date but with a time of "3pm".
	 * 
	 * @param dateString The string representation of the date
	 * @param reference The reference used if dateString contains only time
	 * @param isEndDate Determines the default time used if dateString contains
	 *            only date
	 * @return LocalDateTime object if date can be parsed, else null
	 */
	private LocalDateTime determineDate(String dateString, LocalDateTime reference, boolean isEndDate) {
		dateString = dateString.toLowerCase();
		LocalDateTime date = null;

		// default time is 0000 if startDate, 2359 if endDate
		LocalTime defaultTime = LocalTime.of(0, 0);
		if (isEndDate) {
			defaultTime = LocalTime.of(23, 59);
		}

		// Special cases: "now", "today", "tomorrow"
		if (dateString.equalsIgnoreCase("now")) {
			date = LocalDateTime.now();
			return processDate(date);
		} else if (dateString.equalsIgnoreCase("today")) {
			date = LocalDateTime.now();
			date = setDefaultTime(date, defaultTime);
			return processDate(date);
		} else if (TOMORROW_PATTERNS.contains(dateString)) {
			date = LocalDateTime.now().plusDays(1);
			date = setDefaultTime(date, defaultTime);
			return processDate(date);
		}

		// check date patterns only
		for (String datePattern : DATE_PATTERNS) {
			date = getDateFromPattern(dateString, datePattern);
			if (date != null) {
				date = setDefaultTime(date, defaultTime);
				return processDate(date);
			}
			// check date + time patterns
			for (String timePattern : TIME_PATTERNS) {
				date = getDateFromPattern(dateString, datePattern + " " + timePattern);
				if (date != null) {
					return processDate(date);
				}
			}
		}

		// check time patterns only
		for (String timePattern : TIME_PATTERNS) {
			date = getDateFromPattern(dateString, timePattern);

			int dayOffset = 0;
			boolean skipReference = false;

			// check "today" + time pattern
			if (date == null) {
				date = getDateFromPattern(dateString, "'today' " + timePattern);
				if (date != null) {
					skipReference = true;
				}
			}

			// check "tomorrow" + time pattern
			if (date == null) {
				for (String tomorrowPattern : TOMORROW_PATTERNS) {
					String pattern = String.format("'%s' %s", tomorrowPattern, timePattern);
					date = getDateFromPattern(dateString, pattern);
					if (date != null) {
						skipReference = true;
						dayOffset = 1;
						break;
					}
				}
			}

			if (date != null) {
				if (!skipReference && reference != null) {
					LocalDateTime newDate = reference.withHour(date.getHour());
					newDate = newDate.withMinute(date.getMinute());
					return processDate(newDate);
				} else {
					LocalDateTime todayDate = LocalDateTime.now();
					todayDate = todayDate.plusDays(dayOffset).withHour(date.getHour()).withMinute(date.getMinute());
					return processDate(todayDate);
				}
			}

			// check day + time patterns
			for (String dayPattern : DAY_PATTERNS) {
				date = getDateFromPattern(dateString, dayPattern + " " + timePattern);
				if (date != null) {
					date = buildDateWithNextDay(date.getDayOfWeek().getValue(), date.getHour(), date.getMinute());
					return processDate(date);
				}
			}
		}

		// check day pattern only
		for (String dayPattern : DAY_PATTERNS) {
			date = getDateFromPattern(dateString, dayPattern);
			if (date != null) {
				date = buildDateWithNextDay(date.getDayOfWeek().getValue(), date.getHour(), date.getMinute());
				date = setDefaultTime(date, defaultTime);
				return processDate(date);
			}
		}
		return null;
	}

	/**
	 * Sets date with the default time specified.
	 * @param date The date to modify
	 * @param defaultTime The default time to use
	 * @return The date with the default time applied
	 */
	private LocalDateTime setDefaultTime(LocalDateTime date, LocalTime defaultTime) {
		if (date != null && defaultTime != null) {
			return date.withHour(defaultTime.getHour()).withMinute(defaultTime.getMinute());
		}
		return null;
	}

	/**
	 * Strips seconds and nanoseconds from the date (set to zero).
	 * @param date The date to strip seconds/nanoseconds from
	 * @return The date without seconds/nanoseconds
	 */
	private LocalDateTime processDate(LocalDateTime date) {
		if (date != null) {
			return date.withSecond(0).withNano(0);
		}
		return null;
	}

	/**
	 * Checks that start date is before end date
	 * 
	 * @param start The start date
	 * @param end The end date
	 * @return True if start date is before end date
	 */
	private boolean isValidDateRange(LocalDateTime start, LocalDateTime end) {
		return start.isBefore(end);
	}

	/**
	 * Builds a Date object with the day set to the nearest day specified by the
	 * 'day' parameter. The hours and minutes of the Date object are also set as
	 * specified.
	 * 
	 * For example, if 'day' is 2, the returned date is the first Tuesday from
	 * now.
	 * 
	 * @param day An integer indicating the day: 1 -> monday, 2 -> tuesday, ...,
	 *            7 -> sunday
	 * @param hours The hours to set the date to
	 * @param minutes The minutes to set the date to
	 * @return The constructed date with the day, hours, and minutes set.
	 */
	private LocalDateTime buildDateWithNextDay(int day, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.now();
		int diff = (day - date.getDayOfWeek().getValue());
		if (diff < 0) {
			diff += 7;
		}
		date = date.plusDays(diff).withHour(hours).withMinute(minutes);
		return date;
	}

	/**
	 * Tries to parse a string representation of a date using the given pattern.
	 * 
	 * @param dateString The string representation of the date
	 * @param pattern The pattern to try parsing with
	 * @return LocalDateTime object if date can be parsed, else null
	 */
	private LocalDateTime getDateFromPattern(String dateString, String pattern) {
		// Use SimpleDateFormat because it's much more flexible for datetimes
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			Date date = sdf.parse(dateString);
			String dateTimeFormat = sdf.format(date);
			if (dateTimeFormat.equalsIgnoreCase(dateString)) {
				return toLocalDateTime(date);
			}
		} catch (ParseException e) {
			// nothing
		}

		return null;
	}

	/**
	 * Converts a Date object to LocalDateTime object
	 * 
	 * @param date The Date object to convert
	 * @return The equivalent LocalDateTime object
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
		return dateTime;
	}

	/**
	 * Converts a LocalDateTime object to Date object
	 * 
	 * @param dateTime The LocalDateTime object to convert
	 * @return The equivalent Date object
	 */
	public static Date toDate(LocalDateTime dateTime) {
		Date date = Date.from(dateTime.toInstant((ZoneOffset) ZoneOffset.systemDefault()));
		return date;
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
	public static String removeFirstWord(String commandString) {
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

	/**
	 * Builds an Integer ArrayList from the command content containing task
	 * id(s)
	 * 
	 * @param content The content of the Command object
	 * @return An ArrayList of the content(id) separated by comma
	 */
	public ArrayList<Integer> getIdArrayList(String content) {
		ArrayList<Integer> idArray = new ArrayList<Integer>();
		try {
			String[] arr = content.split(",");
			for (int i = 0; i < arr.length; i++) {
				idArray.add(Integer.valueOf(arr[i].trim()));
			}
			return idArray;
		} catch (Exception e) {
			idArray.clear();
			Integer invalidTaskId = -1;
			idArray.add(invalidTaskId);
			return idArray;
		}
	}

	/**
	 * Compare the command content with possible arguments and return the
	 * intended argument for CommandDisplay
	 * 
	 * @param content The content of the Command object
	 * @return A String containing the intended argument
	 */
	public String getCommandDisplayArg(String content) {
		DisplayType displayType = determineDisplayType(content);
		String type = "";
		switch (displayType) {
			case COMPLETED :
				type = DisplayType.COMPLETED.toString().toLowerCase();
				break;
			case UNCOMPLETED :
				type = DisplayType.UNCOMPLETED.toString().toLowerCase();
				break;
			case ALL :
				type = DisplayType.ALL.toString().toLowerCase();
				break;
			case INVALID :
				type = DisplayType.INVALID.toString().toLowerCase();
				break;
			default :
				type = DisplayType.INVALID.toString().toLowerCase();
				break;
		}
		return type;
	}
	
	private DisplayType determineDisplayType(String arg) {
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
