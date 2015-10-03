package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.model.ViewState;
import app.util.Common;

public class CommandInvalid extends Command {

	public CommandInvalid() {
		super();
		this.setCommandType(CommandType.INVALID);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		ViewState viewState = new ViewState();
		if (getCommandString().isEmpty()) {
			return viewState;
		}
		String word = Common.getFirstWord(getCommandString());
		viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_INVALID_CMD, word));
		
		return viewState;
	}

}
