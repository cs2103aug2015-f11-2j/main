package app.constants;

import java.util.List;

import app.util.Common;

public class CommandConstants {

	// @@author A0132764E
	// Aliases for commands
	public static final List<String> ALIASES_ADD = Common.getUnmodifiableList("add", "a", "+");
	public static final List<String> ALIASES_DELETE = Common.getUnmodifiableList("delete", "del", "d", "remove", "rm", "-");
	public static final List<String> ALIASES_HELP = Common.getUnmodifiableList("help", "?" , "h");
	public static final List<String> ALIASES_THEME = Common.getUnmodifiableList("theme", "t");
	public static final List<String> ALIASES_EXIT = Common.getUnmodifiableList("exit", "quit");
	public static final List<String> ALIASES_MARK = Common.getUnmodifiableList("mark", "m");
	public static final List<String> ALIASES_DISPLAY = Common.getUnmodifiableList("display", "show", "view", "v");
	public static final List<String> ALIASES_EDIT = Common.getUnmodifiableList("edit", "modify", "change", "update", "e");
	public static final List<String> ALIASES_SEARCH = Common.getUnmodifiableList("search", "s");
	public static final List<String> ALIASES_SAVE = Common.getUnmodifiableList("save");
	public static final List<String> ALIASES_UNDO = Common.getUnmodifiableList("undo", "u");

	// Enum values for the different types of commands
	public enum CommandType {
		ADD, DELETE, SEARCH, EXIT, HELP, THEME, INVALID, MARK, DISPLAY, EDIT, SAVE, UNDO;
	}
}
