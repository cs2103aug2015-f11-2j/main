package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ViewType;
import app.constants.HelpConstants;
import app.model.ViewState;
import app.util.LogHelper;

public class CommandHelp extends Command {

	public CommandHelp() {
		super();
		this.setCommandType(CommandType.HELP);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {

		//LogHelper.getLogger().info("Executing CommandHelp object.");

		LogHelper.getInstance().getLogger().info("Executing CommandHelp object.");
		ViewState viewState = new ViewState();

		viewState.setTextArea(HelpConstants.LIST_ALL);
		
		setExecuted(true);
		viewState.setActiveView(ViewType.TEXT_VIEW);
		return viewState;
	}

}
