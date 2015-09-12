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
	private static final List<String> TIME_PATTERNS = getUnmodifiableList("h:mma", "hh:mma", "hhmm", "hhmm'hr's");
	private static final List<String> PRIORITY_LEVELS = getUnmodifiableList("high", "medium", "low");

	private List<String> allKeywords;

	private static List<String> getUnmodifiableList(String... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}

	public CommandParser() {
		allKeywords = new ArrayList<String>();
		allKeywords.addAll(START_DATE_KEYWORDS);
		allKeywords.addAll(END_DATE_KEYWORDS);
		allKeywords.addAll(PRIORITY_KEYWORDS);
	}

	public Command parseCommand(String commandString) {
		Command cmd = createCommand(commandString);
		cmd.setCommandString(commandString);
		parse(cmd);

		// TODO: this is placeholder code
		cmd.setContent(removeFirstWord(commandString));
		if (commandString.contains("priority high")) {
			cmd.setPriority(Priority.HIGH);
		} else if (commandString.contains("priority medium")) {
			cmd.setPriority(Priority.MEDIUM);
		} else if (commandString.contains("priority low")) {
			cmd.setPriority(Priority.LOW);
		}

		return cmd;
	}

	private void parse(Command cmd) {
		String[] arr = cmd.getCommandString().split(" ");

		int rangeStart = -1;
		int rangeEnd = -1;

		int endStart = -1;
		int endEnd = -1;

		int priorityStart = -1;
		int priorityEnd = -1;

		int contentStart = 1; // 2nd word
		int contentEnd = -1;
		
		/*
		 * Tokenize the input string into the following tokens:
		 * 
		 * PRIORITY, RANGE (from <date> to <date>), DEADLINE (due <date>).
		 */
		for (int i = 0; i < arr.length; i++) {
			if (START_DATE_KEYWORDS.contains(arr[i])) {
				boolean endFound = false;
				rangeStart = i;
				int j = i + 1;
				while (j < arr.length && !START_DATE_KEYWORDS.contains(arr[j]) && !PRIORITY_KEYWORDS.contains(arr[j])) {
					if (END_DATE_KEYWORDS.contains(arr[j])) {
						if (endFound) {
							break; // break if we hit a 2nd occurrence
						}
						endFound = true;
					}
					rangeEnd = j;
					i = j++;
				}
			} else if (END_DATE_KEYWORDS.contains(arr[i])) {
				endStart = i;
				int j = i + 1;
				while (j < arr.length && !allKeywords.contains(arr[j])) {
					endEnd = j;
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
			if (!betweenInclusive(i, priorityStart, priorityEnd) && !betweenInclusive(i, rangeStart, rangeEnd)
					&& !betweenInclusive(i, endStart, endEnd)) {
				contentEnd = i;
			}
		}

		// Remove any tokens we merged over.
		if (priorityEnd < contentEnd) {
			priorityStart = priorityEnd = -1;
		}
		if (rangeEnd < contentEnd) {
			rangeStart = rangeEnd = -1;
		}
		if (endEnd < contentEnd) {
			endStart = endEnd = -1;
		}
		if (rangeEnd != -1 && rangeEnd < endStart) {
			rangeStart = rangeEnd = -1;
			contentEnd = rangeEnd;
		} else if (endEnd != -1 && endEnd < rangeStart) {
			endStart = endEnd = -1;
			contentEnd = endEnd;
		}
		
		
		String rangeDate = "";
		String endDate = "";
		for (int i=rangeStart;i<=rangeEnd && i >= 0;i++) {
			rangeDate += arr[i] + " ";
		}
		for (int i=endStart;i<=endEnd && i >= 0;i++) {
			endDate += arr[i] + " ";
		}
		System.out.println(cmd.getCommandString());
		System.out.println(rangeStart + " " + rangeEnd);
		System.out.println(endStart + " " + endEnd);
		//System.out.println("start: " + start);
		//System.out.println("end: " + getEndDate(endDate));
		System.out.println("---");
		setDates(endDate.trim(), cmd);
		System.out.println("===================================");
	}
	
	private String getEndDate(String endDateString) {
		return removeFirstWord(endDateString);
	}

	private boolean betweenInclusive(int subject, int lower, int upper) {
		boolean result = subject >= lower && subject <= upper;
		return result;
	}
	
	private boolean setDates(String dateString, Command cmd) {
		String keyword = getFirstWord(dateString);
		String endDateString = "";
		if (END_DATE_KEYWORDS.contains(keyword)) {
			endDateString = removeFirstWord(dateString);
		}
		System.out.println(dateString);
		
		for (String datePattern : DATE_PATTERNS) {
			for (String timePattern : TIME_PATTERNS) {
				SimpleDateFormat sdf = new SimpleDateFormat(datePattern + " " + timePattern);
				try {
					Date date = sdf.parse(endDateString);
					String dateTimeFormat = sdf.format(date);
					if (dateTimeFormat.equalsIgnoreCase(endDateString)) {
						cmd.setEndDate(date);
					}
				} catch (ParseException e) {
					// nothing
				}
			}
		}
		System.out.println("start: " + cmd.getStartDate());
		System.out.println("end: " + cmd.getEndDate());
		return true;
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
}
