package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.constants.HelpConstants;
import app.constants.StorageConstants;
import app.constants.ViewConstants;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandHelp extends Command {
	//@@author A0125990Y
	private ViewState previousViewState;

	public CommandHelp() {
		super();
		this.setCommandType(CommandType.HELP);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_EXECUTE_COMMAND, "CommandHelp"));

		// store previous state for undo command
		this.previousViewState = new ViewState(previousViewState);
		if (previousViewState.getActiveView() == null) {
			this.previousViewState.setActiveView(ViewType.TASK_LIST);
		}

		ViewState viewState = new ViewState();

		try {
			
			String getHelpList = new String();

			if (this.getContent().isEmpty()) {
				getHelpList = formList();
			} else {
				getHelpList = helpList(this.getContent().toUpperCase());
			}
			
			// return to current state if command does not exist.
			if (getHelpList.equals(this.getContent().toUpperCase())) {
				viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_HELP, this.getContent()));
				return viewState;
			}

			viewState.setTextArea(getHelpList);
			viewState.setHeader(String.format(ViewConstants.HEADER_HELP));
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.HEADER_HELP));
			viewState.setActiveView(ViewType.TEXT_VIEW);
			setExecuted(true);

		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_HELP, this.getContent()));
		}

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
