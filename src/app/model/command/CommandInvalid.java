package app.model.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.helper.CommandParser;
import app.model.ViewState;

public class CommandInvalid extends Command {

	public CommandInvalid() {
		super();
		this.setCommandType(CommandType.INVALID);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		setStatusType(StatusType.ERROR);
		ViewState viewState = new ViewState();
		if (getCommandString().isEmpty()) {
			return viewState;
		}
		String word = CommandParser.getFirstWord(getCommandString());
		viewState.setStatus(String.format(ViewConstants.ERROR_INVALID_CMD, word));
		
		return viewState;
	}

}
