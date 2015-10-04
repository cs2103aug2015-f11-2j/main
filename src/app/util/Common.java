package app.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Common {
	/**
	 * Pluralize a string if given count is more than 1
	 * 
	 * @param singular The singular form of the string
	 * @return The plural form of the string by adding a "s" behind if given count is more than 1
	 */
	public static String pluralize(int count, String singular) {
		return pluralize(count, singular, null);
	}
	
	/**
	 * Pluralize a string if given count is more than 1
	 * 
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
	
	/**
	 * Builds a read-only list from the given arguments.
	 * 
	 * @param args Elements used to create the list
	 * @return A read-only list with the specified elements
	 */
	public static List<String> getUnmodifiableList(String... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}

	/**
	 * Builds an Integer ArrayList from the command content containing task
	 * id(s)
	 * 
	 * @param content The content of the Command object
	 * @return An integer ArrayList of the content(id) separated by comma
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
		return commandString.replace(getFirstWord(commandString), "").trim();
	}
}