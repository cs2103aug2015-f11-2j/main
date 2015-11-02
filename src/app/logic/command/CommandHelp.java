package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.constants.HelpConstants;
import app.constants.ViewConstants;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandHelp extends Command {

	private ViewState previousViewState;

	public CommandHelp() {
		super();
		this.setCommandType(CommandType.HELP);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		// LogHelper.getLogger().info("Executing CommandHelp object.");

		LogHelper.getInstance().getLogger().info("Executing CommandHelp object.");

		// store previous state for undo command
		this.previousViewState = new ViewState(previousViewState);
		if (previousViewState.getActiveView() == null) {
			this.previousViewState.setActiveView(ViewType.TASK_LIST);
		}

		ViewState viewState = new ViewState();
		viewState.setHeader(String.format(ViewConstants.HEADER_HELP));
		
		
		String getHelpList = new String(); 
		
		if (this.getContent().isEmpty()) {
			getHelpList = formList();
		} else {
			getHelpList = helpList(this.getContent().toUpperCase());
		}
		
		viewState.setTextArea(getHelpList);
		viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.HEADER_HELP));

		setExecuted(true);
		viewState.setActiveView(ViewType.TEXT_VIEW);
		return viewState;
	}

	
	private String formList() {
		String formList = new String();
		formList = formList + helpList(CommandType.ADD.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.EDIT.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.SEARCH.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.DELETE.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.DISPLAY.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.MARK.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.UNDO.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.SAVE.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.THEME.toString());
		formList = formList + HelpConstants.DOUBLE_LINE + helpList(CommandType.EXIT.toString());
		return formList;
	}
	
	
	private String helpList(String type) {

		if (type.equals(CommandType.ADD.toString())) {
			type = combineLine(HelpConstants.HELP_ADD_DESCRIPTION, HelpConstants.HELP_ADD_OVERVIEW,
					HelpConstants.HELP_ADD_EXAMPLES);

		} else if (type.equals(CommandType.DELETE.toString())) {
			type = combineLine(HelpConstants.HELP_DELETE_DESCRIPTION, HelpConstants.HELP_DELETE_OVERVIEW,
					HelpConstants.HELP_DELETE_EXAMPLES);

		} else if (type.equals(CommandType.EDIT.toString())) {
			type = combineLine(HelpConstants.HELP_EDIT_DESCRIPTION, HelpConstants.HELP_EDIT_OVERVIEW,
					HelpConstants.HELP_EDIT_EXAMPLES);

		} else if (type.equals(CommandType.SEARCH.toString())) {
			type = combineLine(HelpConstants.HELP_SEARCH_DESCRIPTION, HelpConstants.HELP_SEARCH_OVERVIEW,
					HelpConstants.HELP_SEARCH_EXAMPLES);

		} else if (type.equals(CommandType.DISPLAY.toString())) {
			type = combineLine(HelpConstants.HELP_DISPLAY_DESCRIPTION, HelpConstants.HELP_DISPLAY_OVERVIEW,
					HelpConstants.HELP_DISPLAY_EXAMPLES);

		} else if (type.equals(CommandType.MARK.toString())) {
			type = combineLine(HelpConstants.HELP_MARK_DESCRIPTION, HelpConstants.HELP_MARK_OVERVIEW,
					HelpConstants.HELP_MARK_EXAMPLES);

		} else if (type.equals(CommandType.SAVE.toString())) {
			type = combineLine(HelpConstants.HELP_SAVE_DESCRIPTION, HelpConstants.HELP_SAVE_OVERVIEW,
					HelpConstants.HELP_SAVE_EXAMPLES);

		} else if (type.equals(CommandType.THEME.toString())) {
			type = combineLine(HelpConstants.HELP_THEME_DESCRIPTION, HelpConstants.HELP_THEME_OVERVIEW,
					HelpConstants.HELP_THEME_EXAMPLES);

		} else if (type.equals(CommandType.UNDO.toString())) {
			type = combineLine(HelpConstants.HELP_UNDO_DESCRIPTION, HelpConstants.HELP_UNDO_OVERVIEW, "");

		} else if (type.equals(CommandType.EXIT.toString())) {
			type = combineLine(HelpConstants.HELP_EXIT_DESCRIPTION, HelpConstants.HELP_EXIT_OVERVIEW, "");
		
		} else {
			// Show all if no such help command.
			type = formList();
		}
		
		return type;
	}

	
	private String combineLine(String description, String overview, String examples) {
		return (description + HelpConstants.NEW_LINE + overview + HelpConstants.NEW_LINE + examples);
	}

	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}

		return previousViewState;
	}

}
