package app.model.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.helper.CommandParser;

public class CommandInvalid extends Command {

	public CommandInvalid() {
		super();
	}

	public CommandInvalid(CommandType type) {
		super(type);
	}

	@Override
	public void execute() {
		setStatusType(StatusType.ERROR);
		if (getCommandString().isEmpty()) {
			return;
		}
		
		String word = CommandParser.getFirstWord(getCommandString());
		setFeedback("Invalid command: " + word);
	}

}
