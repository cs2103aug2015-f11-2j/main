package app.helper;

import app.constants.CommandConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.Priority;
import app.model.command.Command;
import app.model.command.CommandAdd;
import app.model.command.CommandInvalid;
import app.model.command.CommandTheme;

public class CommandParser {

	public Command parseCommand(String commandString) {
		Command cmd = createCommand(commandString);
		cmd.setCommandString(commandString);
		
		// TODO: this is placeholder code
		cmd.setContent(removeFirstWord(commandString));
		if (commandString.contains("priority high")) {
			cmd.setPriority(Priority.HIGH);
		} else if (commandString.contains("priority medium")) {
			cmd.setPriority(Priority.MEDIUM);
		} else if (commandString.contains("priority low")) {
			cmd.setPriority(Priority.LOW);
		}
		
		return cmd;
	}

	private Command createCommand(String commandString) {
		CommandType commandType = determineCommandType(commandString);
		
		switch (commandType) {
		case ADD:
			return new CommandAdd(commandType);
		case THEME:
			return new CommandTheme(commandType);
		case INVALID: // Intentional fall-through and default case
		default:
			return new CommandInvalid(commandType);
		}
	}

	private CommandType determineCommandType(String commandString) {
		String word = getFirstWord(commandString).toLowerCase();
		if (CommandConstants.ALIASES_ADD.contains(word)) {
			return CommandType.ADD;
		} else if (CommandConstants.ALIASES_REMOVE.contains(word)) {
			return CommandType.REMOVE;
		} else if (CommandConstants.ALIASES_THEME.contains(word)) {
			return CommandType.THEME;
		} else if (CommandConstants.ALIASES_HELP.contains(word)) {
			return CommandType.HELP;
		}
		return CommandType.INVALID;
	}

	public static String getFirstWord(String words) {
		return words.trim().split("\\s+")[0];
	}
	
	private static String removeFirstWord(String commandString) {
		return commandString.replace(getFirstWord(commandString), "").trim();
	}
}
