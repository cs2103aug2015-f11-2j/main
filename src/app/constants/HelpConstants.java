package app.constants;

public class HelpConstants {
	
	// GET ENTIRE HELP LIST
	public static final String LIST_ALL =  
			HelpConstants.HELP_ADD_DESCRIPTION + "\n" + HelpConstants.HELP_ADD_OVERVIEW + "\n" + HelpConstants.HELP_ADD_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_EDIT_DESCRIPTION + "\n" + HelpConstants.HELP_EDIT_OVERVIEW + "\n" + HelpConstants.HELP_EDIT_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_SEARCH_DESCRIPTION + "\n" + HelpConstants.HELP_SEARCH_OVERVIEW + "\n" + HelpConstants.HELP_SEARCH_EXAMPLES
			+ "\n\n" + HelpConstants.HELP_DELETE_DESCRIPTION + "\n" + HelpConstants.HELP_DELETE_OVERVIEW + "\n" + HelpConstants.HELP_DELETE_EXAMPLES;

	
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

	// MARK

	// UNDO
	
	
	
}
