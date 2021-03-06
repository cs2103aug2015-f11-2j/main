package app.logic;

import java.util.Stack;
import java.util.ArrayList;

import app.constants.CommandConstants;
import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.DisplayType;
import app.logic.command.Command;
import app.logic.command.CommandAdd;
import app.logic.command.CommandDelete;
import app.logic.command.CommandDisplay;
import app.logic.command.CommandEdit;
import app.logic.command.CommandExit;
import app.logic.command.CommandHelp;
import app.logic.command.CommandInvalid;
import app.logic.command.CommandMark;
import app.logic.command.CommandSave;
import app.logic.command.CommandSearch;
import app.logic.command.CommandTheme;
import app.logic.command.CommandUndo;
import app.model.Action;
import app.model.CommandList;
import app.model.TaskList;
import app.model.ViewState;
import app.parser.CommandParser;
import app.storage.AppStorage;
import app.storage.TaskStorage;
import app.util.Common;

/**
 * This class provides the layer of logic between the ViewManager and the rest
 * of the program. Classes from this app.controller package are the only ones to
 * interact with the ViewManager.
 */
public class CommandController {

	// @@author A0126120B
	private static CommandController commandController;

	private TaskList masterTaskList;
	private ViewState currentViewState;
	private Stack<Command> executedCommands;
	
	private CommandList commandHistory;

	// @@author A0125960E
	private CommandController() {
		masterTaskList = TaskStorage.getInstance().readTasks();
		executedCommands = new Stack<Command>();
		commandHistory = new CommandList();
		initializeViewState();
	}
	

	private void initializeViewState() {
		currentViewState = new ViewState();
		currentViewState.setTaskList(masterTaskList.getTaskListByCompletion(false));
		currentViewState.getTaskList().sort();
		currentViewState.setHeader(String.format(ViewConstants.HEADER_DISPLAY,
				DisplayType.UNCOMPLETED.toString()));

		if (!(AppStorage.getInstance().getSelectedTheme().equalsIgnoreCase(ViewConstants.THEME_LIGHT)
				|| AppStorage.getInstance().getSelectedTheme().equalsIgnoreCase(ViewConstants.THEME_DARK))) {
			AppStorage.getInstance().setToDefaultSelectedTheme();
		}
		
		if (AppStorage.getInstance().getSelectedTheme().equalsIgnoreCase(ViewConstants.THEME_LIGHT)) {
			currentViewState.setTheme(ViewConstants.THEME_LIGHT_CSS);
		} else if (AppStorage.getInstance().getSelectedTheme().equalsIgnoreCase(ViewConstants.THEME_DARK)) {
			currentViewState.setTheme(ViewConstants.THEME_DARK_CSS);
		}
	}

	// @@author A0126120B
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
		commandHistory.add(cmd.getCommandString());

		if (cmd.isExecuted()) {
			currentViewState.mergeWith(newViewState);
			currentViewState.getTaskList().sort();
			if (cmd.getCommandType() != CommandType.UNDO) {
				executedCommands.push(cmd); 
			}
		} else {
			// If not executed, simply update status bar and reset actions.
			currentViewState.mergeStatus(newViewState);
			currentViewState.setActions(new ArrayList<Action>());
		}
		
		return currentViewState;
	}

	/**
	 * Determines the CommandType of the specified command string
	 * 
	 * @param commandString The command string
	 * @return The determined CommandType object
	 */
	private CommandType determineCommandType(String commandString) {
		String word = Common.getFirstWord(commandString).toLowerCase();
		if (CommandConstants.ALIASES_ADD.contains(word)) {
			return CommandType.ADD;
		} else if (CommandConstants.ALIASES_DELETE.contains(word)) {
			return CommandType.DELETE;
		} else if (CommandConstants.ALIASES_THEME.contains(word)) {
			return CommandType.THEME;
		} else if (CommandConstants.ALIASES_HELP.contains(word)) {
			return CommandType.HELP;
		} else if (CommandConstants.ALIASES_MARK.contains(word)) {
			return CommandType.MARK;
		} else if (CommandConstants.ALIASES_DISPLAY.contains(word)) {
			return CommandType.DISPLAY;
		} else if (CommandConstants.ALIASES_EDIT.contains(word)) {
			return CommandType.EDIT;
		} else if (CommandConstants.ALIASES_SAVE.contains(word)) {
			return CommandType.SAVE;
		} else if (CommandConstants.ALIASES_SEARCH.contains(word)) {
			return CommandType.SEARCH;
		} else if (CommandConstants.ALIASES_UNDO.contains(word)) {
			return CommandType.UNDO;
		}  else if (CommandConstants.ALIASES_EXIT.contains(word)) {
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
		case DELETE:
			cmd = new CommandDelete();
			break;
		case DISPLAY:
			cmd = new CommandDisplay();
			break;
		case EDIT:
			cmd = new CommandEdit();
			break;
		case SAVE:
			cmd = new CommandSave();
			break;
		case UNDO:
			cmd = new CommandUndo();
			break;
		case SEARCH:
			cmd = new CommandSearch();
			break;
		case HELP:
			cmd = new CommandHelp();
			break;
		case EXIT:
			cmd = new CommandExit();
			break;
		case INVALID: // Intentional fall-through and default case
		default:
			cmd = new CommandInvalid();
		}

		cmd.setCommandString(commandString);
		cmd.setContent(Common.removeFirstWord(cmd.getCommandString()));
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
			CommandParser.parseDatesAndPriority(cmd);
			break;
		case DISPLAY:
			cmd.setContent(CommandParser.determineDisplayType(cmd.getContent()).toString());
			break;
		case EDIT:
			CommandParser.parseDatesAndPriority(cmd, true);
			CommandEdit e = (CommandEdit) cmd;
			e.setDisplayId(CommandParser.getTaskDisplayedIdFromContent(cmd.getContent()));
			e.setContent(CommandParser.getTaskDescFromContent(cmd.getContent()));
			break;
		case SAVE:
			CommandParser.parseSave(cmd);
			break;
		case SEARCH:
			CommandParser.parseSearch(cmd);
			break;
		case MARK:
			cmd.setContent(CommandParser.determineMarkAll(cmd.getContent()).toString());
			break;
		default:
			break;
		}
	}

	public TaskList getMasterTaskList() {
		return masterTaskList;
	}

	public ViewState getCurrentViewState() {
		return currentViewState;
	}
	
	public CommandList getCommandHistory() {
		return commandHistory;
	}

	public Stack<Command> getExecutedCommands() {
		return executedCommands;
	}
}
