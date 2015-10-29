package app.constants;

public class HelpConstants {
	
	// GET ENTIRE HELP LIST
	public static final String LIST_ALL =  
			HelpConstants.HELP_ADD_DESCRIPTION + "\n" + HelpConstants.HELP_ADD_OVERVIEW + "\n" + HelpConstants.HELP_ADD_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_EDIT_DESCRIPTION + "\n" + HelpConstants.HELP_EDIT_OVERVIEW + "\n" + HelpConstants.HELP_EDIT_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_SEARCH_DESCRIPTION + "\n" + HelpConstants.HELP_SEARCH_OVERVIEW + "\n" + HelpConstants.HELP_SEARCH_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_DELETE_DESCRIPTION + "\n" + HelpConstants.HELP_DELETE_OVERVIEW + "\n" + HelpConstants.HELP_DELETE_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_DISPLAY_DESCRIPTION + "\n" + HelpConstants.HELP_DISPLAY_OVERVIEW + "\n" + HelpConstants.HELP_DISPLAY_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_MARK_DESCRIPTION + "\n" + HelpConstants.HELP_MARK_OVERVIEW + "\n" + HelpConstants.HELP_MARK_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_UNDO_DESCRIPTION + "\n" + HelpConstants.HELP_UNDO_OVERVIEW
			+ "\n\n" + HelpConstants.HELP_THEME_DESCRIPTION + "\n" + HelpConstants.HELP_THEME_OVERVIEW + "\n" + HelpConstants.HELP_THEME_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_EXIT_DESCRIPTION + "\n" + HelpConstants.HELP_EXIT_OVERVIEW;

	
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
	public static final String HELP_SEARCH_OVERVIEW = "search [keyword] [between <date> and <date> | after <date> | before <date>] [priority high | medium | low] [type comp | pend]";
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
	public static final String HELP_MARK_OVERVIEW = "mark <id>";
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
	public static final String HELP_SAVE_EXAMPLES = "[example]: save /path/to/storage.txt"
			+ "save [log] /path/to/log.txt";
	
	// EXIT
	public static final String HELP_EXIT_DESCRIPTION = "Exit Next";
	public static final String HELP_EXIT_OVERVIEW = "exit";
	
}
