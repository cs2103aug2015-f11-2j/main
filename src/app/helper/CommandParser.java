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

	/**
	 * Creates the relevant Command subclass based on the specified command
	 * string. The created subclass only has its commandType variable set.
	 * 
	 * @param commandString The command string
	 * @return The relevant Command subclass with appropriate commandType
	 *         variable set.
	 */
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

	/**
	 * Determines the CommandType of the specified command string
	 * 
	 * @param commandString The command string
	 * @return The determined CommandType object
	 */
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

	/**
	 * Returns the first whitespace delimited word of the specified string.
	 * 
	 * @param words The string to get the first word from
	 * @return The first word of the specified string
	 */
	public static String getFirstWord(String words) {
		return words.trim().split("\\s+")[0];
	}

	/**
	 * Removes the first whitespace delimited word of the specified string.
	 * 
	 * @param commandString The string to remove the first word from
	 * @return The resultant string without the first word
	 */
	private static String removeFirstWord(String commandString) {
		return commandString.replace(getFirstWord(commandString), "").trim();
	}
}
