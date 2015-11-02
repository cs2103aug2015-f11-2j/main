package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.constants.HelpConstants;
import app.constants.ViewConstants;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandHelp extends Command {

	public CommandHelp() {
		super();
		this.setCommandType(CommandType.HELP);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {

		// LogHelper.getLogger().info("Executing CommandHelp object.");

		LogHelper.getInstance().getLogger().info("Executing CommandHelp object.");
		ViewState viewState = new ViewState();

		viewState.setHeader(String.format(ViewConstants.HEADER_HELP));
		viewState.setTextArea(HelpConstants.LIST_ALL);
		viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.HEADER_HELP));

		setExecuted(true);
		viewState.setActiveView(ViewType.TEXT_VIEW);
		return viewState;
	}

	@Override
	public ViewState undo() {
		// TODO Auto-generated method stub
		return null;
	}

}
