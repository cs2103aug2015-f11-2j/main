package app.constants;

public class HelpConstants {
	// ADD
	public static final String HELP_ADD_DESCRIPTION = "Add a new task";
	public static final String HELP_ADD_OVERVIEW = "add <task> [from <date> to <date>] [by <date>] [priority high | medium | low]";
	public static final String HELP_ADD_EXAMPLES = "add do tutorials by sunday 10pm\n"
			+ "add math exam from 26/11/15 1pm to 3pm priority high";

	// EDIT
	public static final String HELP_EDIT_DESCRIPTION = "Edit a task";
	public static final String HELP_EDIT_OVERVIEW = "edit <id> [task] [from <date> to <date>] [by <date>] [priority high | medium | low]";
	public static final String HELP_EDIT_EXAMPLES = "";

	// SEARCH
	public static final String HELP_SEARCH_DESCRIPTION = "Search for tasks";
	public static final String HELP_SEARCH_OVERVIEW = "search <keyword> [between <date> and <date> | after <date> | before <date>] [priority high | medium | low] [type comp | pend]";
	public static final String HELP_SEARCH_EXAMPLES = "search hotdogs before monday priority high type pend";
}
