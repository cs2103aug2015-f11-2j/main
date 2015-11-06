package app.constants;

public class HelpConstants {
	
	// NEW LINE
	public static final String NEW_LINE = "\n";
	
	// DOUBLE LINE
	public static final String DOUBLE_LINE = "\n\n";
	
	// ADD
	public static final String HELP_ADD_DESCRIPTION = "Add a new task";
	public static final String HELP_ADD_OVERVIEW = "add <task> [from <date> to <date>] [by <date>] [priority high | medium | low]";
	public static final String HELP_ADD_EXAMPLES = "[example]: add do tutorials by sunday 10pm\n"
			+ "add math exam from 26/11/15 1pm to 3pm priority high";

	// EDIT
	public static final String HELP_EDIT_DESCRIPTION = "Edit a task";
	public static final String HELP_EDIT_OVERVIEW = "edit <id> [task] [from <date> to <date>] [by <date>] [priority high | medium | low]";
	public static final String HELP_EDIT_EXAMPLES = "[example]: ";

	// SEARCH
	public static final String HELP_SEARCH_DESCRIPTION = "Search for tasks";
	public static final String HELP_SEARCH_OVERVIEW = "search [keyword] [between <date> and <date> | after <date> | before <date> | date none] [priority high | medium | low | none] [type comp | pend]";
	public static final String HELP_SEARCH_EXAMPLES = "[example]: search hotdogs before monday priority high type pend";

	// DELETE
	public static final String HELP_DELETE_DESCRIPTION = "Delete single/multiple task(s)";
	public static final String HELP_DELETE_OVERVIEW = "delete <id>";
	public static final String HELP_DELETE_EXAMPLES = "[example]: delete 2 3 4";
	
	// DISPLAY
	public static final String HELP_DISPLAY_DESCRIPTION = "Display tasks";
	public static final String HELP_DISPLAY_OVERVIEW = "display [completed | uncompleted | all]";
	public static final String HELP_DISPLAY_EXAMPLES = "[example]: display\n"
			+ "display completed";

	// MARK
	public static final String HELP_MARK_DESCRIPTION = "Mark tasks as completed or uncompleted";
	public static final String HELP_MARK_OVERVIEW = "mark <id | all>";
	public static final String HELP_MARK_EXAMPLES = "[example]: mark 1 2 3";

	// UNDO
	public static final String HELP_UNDO_DESCRIPTION = "Undo the last successfully executed command";
	public static final String HELP_UNDO_OVERVIEW = "undo";
	
	// THEME
	public static final String HELP_THEME_DESCRIPTION = "Change the current theme";
	public static final String HELP_THEME_OVERVIEW = "theme [light | dark]";
	public static final String HELP_THEME_EXAMPLES = "[example]: theme light";
	
	// SAVE
	public static final String HELP_SAVE_DESCRIPTION = "Change the location of storage files";
	public static final String HELP_SAVE_OVERVIEW = "save [log] <path>";
	public static final String HELP_SAVE_EXAMPLES = "[example]: save /path/to/storage.txt\n"
			+ "save [log] /path/to/log.txt";
	
	// EXIT
	public static final String HELP_EXIT_DESCRIPTION = "Exit Next";
	public static final String HELP_EXIT_OVERVIEW = "exit";
	
}
