package app.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandConstants {

	// Aliases for commands
	public static final List<String> ALIASES_ADD = getUnmodifiableList("add", "a", "+");
	public static final List<String> ALIASES_REMOVE = getUnmodifiableList("remove", "delete", "rm", "-");
	public static final List<String> ALIASES_HELP = getUnmodifiableList("help", "?");
	public static final List<String> ALIASES_THEME = getUnmodifiableList("theme");
	public static final List<String> ALIASES_EXIT = getUnmodifiableList("exit", "quit");
	public static final List<String> ALIASES_MARK = getUnmodifiableList("mark", "m");
	public static final List<String> ALIASES_DISPLAY = getUnmodifiableList("display", "show", "view");
	public static final List<String> ALIASES_EDIT = getUnmodifiableList("edit", "modify", "change", "update", "ed");

	// Enum values for the different types of commands
	public enum CommandType {
		ADD, REMOVE, UPDATE, SEARCH, EXIT, HELP, THEME, INVALID, MARK, DISPLAY, EDIT;
	}
	
	// Enum value for different types of display arguments
	public enum DisplayType {
		COMPLETED, UNCOMPLETED, ALL, INVALID;
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
