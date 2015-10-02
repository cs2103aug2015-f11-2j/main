package app.controller;

import app.constants.CommandConstants;
import app.constants.CommandConstants.CommandType;
import app.helper.CommandParser;
import app.model.TaskList;
import app.model.ViewState;
import app.model.command.Command;
import app.model.command.CommandAdd;
import app.model.command.CommandDisplay;
import app.model.command.CommandExit;
import app.model.command.CommandInvalid;
import app.model.command.CommandMark;
import app.model.command.CommandTheme;

/**
 * This class provides the layer of logic between the ViewManager and the rest
 * of the program. Classes from this app.controller package are the only ones to
 * interact with the ViewManager.
 */
public class CommandController {

	private static CommandController commandController;

	private TaskList masterTaskList;
	private CommandParser parser;

	private ViewState currentViewState;

	private CommandController() {
		parser = new CommandParser();
		masterTaskList = new TaskList();
		currentViewState = new ViewState();
		currentViewState.setTaskList(new TaskList());
	}

	/**
	 * This method implements the Singleton design pattern.
	 * 
	 * @return This instance of CommandController.
	 */

	public static CommandController getInstance() {
		if (commandController == null) {
			commandController = new CommandController();
		}
		return commandController;
	}

	/**
	 * Entry method for executing a command string. The command string is parsed
	 * and the relevant logic is executed.
	 * 
	 * @param commandString The full command string.
	 */
	public ViewState executeCommand(String commandString) {
		commandString = commandString.trim();
		Command cmd = createCommand(commandString);
		ViewState newViewState = cmd.execute(currentViewState);

		if (cmd.isExecuted()) {
			currentViewState.mergeWith(newViewState);
			return currentViewState;
		}

		// Return null to indicate a no-change operation
		return null;
	}

	/**
	 * Determines the CommandType of the specified command string
	 * 
	 * @param commandString The command string
	 * @return The determined CommandType object
	 */
	private CommandType determineCommandType(String commandString) {
		String word = CommandParser.getFirstWord(commandString).toLowerCase();
		if (CommandConstants.ALIASES_ADD.contains(word)) {
			return CommandType.ADD;
		} else if (CommandConstants.ALIASES_REMOVE.contains(word)) {
			return CommandType.REMOVE;
		} else if (CommandConstants.ALIASES_THEME.contains(word)) {
			return CommandType.THEME;
		} else if (CommandConstants.ALIASES_HELP.contains(word)) {
			return CommandType.HELP;
		} else if (CommandConstants.ALIASES_MARK.contains(word)) {
			return CommandType.MARK;
		} else if (CommandConstants.ALIASES_DISPLAY.contains(word)) {
			return CommandType.DISPLAY;
		} else if (CommandConstants.ALIASES_EXIT.contains(word)) {
			return CommandType.EXIT;
		}
		return CommandType.INVALID;
	}

	/**
	 * Creates the relevant Command subclass based on the CommandType parsed
	 * from the supplied input parameter. Also based on the CommandType, the
	 * created Command subclass is then parsed appropriately. The result is a
	 * Command object with all its fields populated based on the input
	 * parameter.
	 * 
	 * @param commandString The command string
	 * @return A Command subclass with its fields set to the parsed result
	 */
	public Command createCommand(String commandString) {
		CommandType commandType = determineCommandType(commandString);
		Command cmd;

		switch (commandType) {
		case ADD:
			cmd = new CommandAdd();
			break;
		case THEME:
			cmd = new CommandTheme();
			break;
		case MARK:
			cmd = new CommandMark();
			break;
		case DISPLAY:
			cmd = new CommandDisplay();
			break;
		case EXIT:
			cmd = new CommandExit();
			break;
		case INVALID: // Intentional fall-through and default case
		default:
			cmd = new CommandInvalid();
		}

		cmd.setCommandString(commandString);
		cmd.setContent(CommandParser.removeFirstWord(cmd.getCommandString()));
		parseCommand(cmd);
		return cmd;
	}

	/**
	 * Parses the supplied Command object based on its CommandType.
	 * 
	 * @param cmd The object to parse
	 */
	private void parseCommand(Command cmd) {
		// Additional parsing for certain command types
		switch (cmd.getCommandType()) {
		case ADD:
			parser.parseDatesAndPriority(cmd);
			break;
		default:
			break;
		}
	}

	public TaskList getMasterTaskList() {
		return masterTaskList;
	}
}
