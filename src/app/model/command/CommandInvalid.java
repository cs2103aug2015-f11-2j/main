package app.model.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.helper.CommandParser;

public class CommandInvalid extends Command {

	public CommandInvalid() {
		super();
		this.setCommandType(CommandType.INVALID);
	}

	@Override
	public void execute() {
		setStatusType(StatusType.ERROR);
		if (getCommandString().isEmpty()) {
			return;
		}
		String word = CommandParser.getFirstWord(getCommandString());
		setFeedback(String.format(ViewConstants.ERROR_INVALID_CMD, word));
	}

}
