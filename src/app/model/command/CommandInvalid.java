package app.model.command;

import app.helper.CommandParser;
import app.view.ViewManager.StatusType;

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
