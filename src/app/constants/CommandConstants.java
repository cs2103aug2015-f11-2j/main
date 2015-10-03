package app.constants;

import java.util.List;

import app.util.Common;

public class CommandConstants {

	// Aliases for commands
	public static final List<String> ALIASES_ADD = Common.getUnmodifiableList("add", "a", "+");
	public static final List<String> ALIASES_REMOVE = Common.getUnmodifiableList("remove", "delete", "rm", "-");
	public static final List<String> ALIASES_HELP = Common.getUnmodifiableList("help", "?");
	public static final List<String> ALIASES_THEME = Common.getUnmodifiableList("theme");
	public static final List<String> ALIASES_EXIT = Common.getUnmodifiableList("exit", "quit");
	public static final List<String> ALIASES_MARK = Common.getUnmodifiableList("mark", "m");
	public static final List<String> ALIASES_DISPLAY = Common.getUnmodifiableList("display", "show", "view");
	public static final List<String> ALIASES_EDIT = Common.getUnmodifiableList("edit", "modify", "change", "update", "ed");

	// Enum values for the different types of commands
	public enum CommandType {
		ADD, REMOVE, UPDATE, SEARCH, EXIT, HELP, THEME, INVALID, MARK, DISPLAY, EDIT;
	}
	
	// Enum value for different types of display arguments
	public enum DisplayType {
		COMPLETED, UNCOMPLETED, ALL, INVALID;
	}
}
