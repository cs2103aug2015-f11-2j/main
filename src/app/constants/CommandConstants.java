package app.constants;

import java.util.List;

import app.util.Common;

public class CommandConstants {

	// Aliases for commands
	public static final List<String> ALIASES_ADD = Common.getUnmodifiableList("add", "ad", "a", "+");
	public static final List<String> ALIASES_REMOVE = Common.getUnmodifiableList("remove", "delete", "rm", "-", "de", "del", "dele", "delet");
	public static final List<String> ALIASES_HELP = Common.getUnmodifiableList("help", "?");
	public static final List<String> ALIASES_THEME = Common.getUnmodifiableList("theme", "th", "the", "them");
	public static final List<String> ALIASES_EXIT = Common.getUnmodifiableList("exit", "quit");
	public static final List<String> ALIASES_MARK = Common.getUnmodifiableList("mark", "m", "ma", "mar");
	public static final List<String> ALIASES_DISPLAY = Common.getUnmodifiableList("display", "show", "view", "di", "dis", "disp", "displ", "displa");
	public static final List<String> ALIASES_EDIT = Common.getUnmodifiableList("edit", "modify", "change", "update", "ed", "edi");
	public static final List<String> ALIASES_SEARCH = Common.getUnmodifiableList("search", "s", "se", "sea", "sear", "searc");
	public static final List<String> ALIASES_SAVE = Common.getUnmodifiableList("save", "sa", "sav");
	public static final List<String> ALIASES_UNDO = Common.getUnmodifiableList("undo", "u", "un", "und");

	// Enum values for the different types of commands
	public enum CommandType {
		ADD, DELETE, UPDATE, SEARCH, EXIT, HELP, THEME, INVALID, MARK, DISPLAY, EDIT, SAVE, UNDO;
	}
}
