package app.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import app.constants.CommandConstants;
import app.constants.CommandConstants.CommandType;

public class Common {
	// @@author A0125360R
	/**
	 * Pluralize a string if given count is more than 1
	 * 
	 * @param count The number of element referred to by the string
	 * @param singular The singular form of the string
	 * @return The plural form of the string by adding a "s" behind if given
	 *         count is more than 1
	 */
	public static String pluralize(int count, String singular) {
		return pluralize(count, singular, null);
	}

	// @@author A0125360R
	/**
	 * Pluralize a string if given count is more than 1
	 * 
	 * @param count The number of element referred to by the string
	 * @param singular The singular form of the string
	 * @param plural The plural form of the string
	 * @return The plural form of the string if given count is more than 1
	 */
	public static String pluralize(int count, String singular, String plural) {
		if (count == 1) {
			return singular;
		} else if (count > 1 && plural != null) {
			return plural;
		} else if (count > 1 && plural == null) {
			return singular + "s";
		}
		return singular;
	}

	// @@author A0126120B
	/**
	 * Builds a string from a string array using specified start/end indexes.
	 * The start and end indexes are both inclusive.
	 * 
	 * @param start The start index
	 * @param end The end index
	 * @param array The array of strings
	 * @return The constructed string
	 */
	public static String getStringFromArrayIndexRange(int start, int end, String[] array) {
		String result = "";
		for (int i = start; i < array.length && i >= 0 && i <= end; i++) {
			result += array[i] + " ";
		}
		return result.trim();
	}

	// @@author A0126120B
	/**
	 * Builds a read-only list from the given arguments.
	 * 
	 * @param args Elements used to create the list
	 * @return A read-only list with the specified elements
	 */
	public static List<String> getUnmodifiableList(String... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}

	// @@author A0125360R
	/**
	 * Builds an Integer ArrayList from the command content containing task
	 * ID(s)
	 * 
	 * @param content The content of the Command object
	 * @return An integer ArrayList of the ID(s) or null 
	 */
	public static ArrayList<Integer> getIdArrayList(String content) {
		ArrayList<Integer> idArray = new ArrayList<Integer>();
		try {
			String[] arr = content.replaceAll("^[,\\s]+", "").split("[,\\s]+");
			for (int i = 0; i < arr.length; i++) {
				idArray.add(Integer.valueOf(arr[i].trim()));
			}
			return idArray;
		} catch (Exception e) {
			return null;
		}
	}

	// @@author A0126120B
	/**
	 * Compares the subject with a given range and returns true if subject is
	 * between or equal to lower and upper indexes.
	 * 
	 * @param subject The subject of the comparison
	 * @param lower The lower index
	 * @param upper The upper index
	 * @return True if subject is between or equal to lower and upper
	 */
	public static boolean betweenInclusive(int subject, int lower, int upper) {
		boolean result = (subject >= lower && subject <= upper);
		return result;
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
		String word = Pattern.quote(getFirstWord(commandString));
		return commandString.replaceFirst(word, "").trim();
	}

	// @@author A0125360R-reused
	/**
	 * Removes the duplicated elements in the ArrayList
	 * 
	 * @param <T>
	 * @param listWithDuplicates The ArrayList with duplicated elements
	 * @return An ArrayList with no duplicate elements
	 */
	public static <T> ArrayList<T> removeDuplicatesFromArrayList(ArrayList<T> listWithDuplicates) {
		Set<T> noDuplicates = new LinkedHashSet<T>(listWithDuplicates);
		return new ArrayList<T>(noDuplicates);
	}
	
	// @@author A0125360R
	/**
	 * Builds a string of task ID(s) separated by comma
	 * from an Integer ArrayList of task ID
	 * 
	 * @param arr The Integer ArrayList of task ID(s)
	 * @return A string containing the task ID(s) separated by comma
	 */
	public static String getIdListString(ArrayList<Integer> arr) {
		String idList = "";
		for (int i = 0; i < arr.size(); i++) {
			idList += String.valueOf(arr.get(i)) + ", ";
		}
		idList = idList.replaceAll(",[ \t]*$", "");
		return idList;
	}
	
	// @@author A0125360R
	/**
	 * Builds a String of UUID(s) separated by comma
	 * from an ArrayList of task UUID
	 * 
	 * @param arr The UUID ArrayList of task ID(s)
	 * @return A String containing the task UUID(s) separated by comma
	 */
	public static String getUuidListString(ArrayList<UUID> arr) {
		String idList = "";
		for (int i = 0; i < arr.size(); i++) {
			idList += String.valueOf(arr.get(i)) + ", ";
		}
		idList = idList.replaceAll(",[ \t]*$", "");
		return idList;
	}
	
	// @@author A0126120B
	public static List<String> getAliasesForCommandType(CommandType type) {
		switch (type) {
		case ADD:
			return CommandConstants.ALIASES_ADD;
		case DELETE:
			return CommandConstants.ALIASES_DELETE;
		case HELP:
			return CommandConstants.ALIASES_HELP;
		case THEME:
			return CommandConstants.ALIASES_THEME;
		case EXIT:
			return CommandConstants.ALIASES_EXIT;
		case MARK:
			return CommandConstants.ALIASES_MARK;
		case DISPLAY:
			return CommandConstants.ALIASES_DISPLAY;
		case EDIT:
			return CommandConstants.ALIASES_EDIT;
		case SEARCH:
			return CommandConstants.ALIASES_SEARCH;
		case SAVE:
			return CommandConstants.ALIASES_SAVE;
		case UNDO:
			return CommandConstants.ALIASES_UNDO;
		default:
			return new ArrayList<String>();
		}
	}
}
