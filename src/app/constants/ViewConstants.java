package app.constants;

import java.util.LinkedList;
import java.util.Queue;

import app.Main;

public class ViewConstants {

	// @@author A0126120B
	// Style classes for the different statusbar statuses
	public static String STATUS_STYLE_SUCCESS = "success";
	public static String STATUS_STYLE_ERROR = "error";
	public static String STATUS_STYLE_INFO = "info";
	
	// @@author A0126120B
	// Style classes for task item colors
	public static String ITEM_PURPLE = "purple";
	public static String ITEM_BLUE = "blue";
	public static String ITEM_GREEN = "green";
	public static String ITEM_YELLOW = "yellow";
	public static String ITEM_ORANGE = "orange";
	public static String ITEM_RED = "red";
	public static String ITEM_PINK = "pink";

	// @@author A0126120B
	// THEME command
	// Resource locations for the theme CSS files.
	public static String THEME_LIGHT_CSS = Main.class.getResource("view/css/theme_light.css").toExternalForm();
	public static String THEME_DARK_CSS = Main.class.getResource("view/css/theme_dark.css").toExternalForm();
	public static String THEME_LIGHT = "light";
	public static String THEME_DARK = "dark";
	public static String MESSAGE_AVAILABLE_THEMES = "Available themes: light, dark";
	public static String MESSAGE_CURRENT_THEME = "Current theme: %1$s";
	
	// @@author A0126120B
	// ADD command
	public static String MESSAGE_ADD = "Added task: %1$s";
	public static String ERROR_ADD = "Error adding task: %1$s";
	public static String ERROR_ADD_NO_TASK = "No task specified";
	
	// @@author A0125990Y
	// DELETE command
	public static String MESSAGE_DELETE = "Deleted task: %1$s";
	public static String ERROR_DELETE = "Error deleting task: %1$s";
	public static String ERROR_DELETE_NO_TASK = "No task specified";
	public static String ERROR_DELETE_INVALID_ID = "Invalid task ID entered";
	
	// @@author A0125360R
	// MARK command
	public static String MESSAGE_MARK_COMPLETED = "Marked %s completed: %s";
	public static String MESSAGE_MARK_UNCOMPLETED = "Marked %s uncompleted: %s";
	public static String ERROR_MARK = "Error marking task";
	public static String ERROR_MARK_NO_TASK = "No task specified to mark";
	public static String ERROR_MARK_INVALID_ID = "Invalid task ID entered";
	
	// @@author A0125360R
	// DISPLAY command
	public static String MESSAGE_DISPLAY = "Displaying %1$s tasks";
	public static String ERROR_DISPLAY = "Error displaying tasks";
	public static String ERROR_DISPLAY_INVALID_ARGUMENT = "Invalid option. Available: all, completed, uncompleted (default)";
	
	// @@author A0125360R
	// EDIT command
	public static String MESSAGE_EDIT = "Edited task: %1$s";
	public static String ERROR_EDIT = "Error editing task";
	public static String ERROR_EDIT_NO_TASK = "No task specified to edit";
	public static String ERROR_EDIT_INVALID_TASK_ID = "Invalid task ID entered";
	public static String ERROR_EDIT_NO_CHANGES = "No changes made for task %1$s";

	// @@author A0125960E
	// SAVE command
	public static String SAVE_STORAGE = "storage";
	public static String SAVE_LOG = "log";
	public static String SAVE_DEFAULT = "default";
	public static String MESSAGE_SAVE = "Saved %1$s file location: %2$s";
	public static String ERROR_SAVE = "Error saving file location: %1$s";
	public static String ERROR_SAVE_NO_LOCATION = "No %1$s file location specified";
	public static String ERROR_SAVE_NO_CHANGES = "Same %1$s file location. No changes to %1$s file location: %2$s";
	public static String ERROR_SAVE_COPY_FILE = "IOException while copying %1$s file to specified location: %2$s";
	public static String ERROR_SAVE_DELETE_FILE = "IOException while deleting previous %1$s file: %2$s";
	public static String ERROR_SAVE_FILE_ALREADY_EXISTS = "File already exists in specified location: %1$s";

	// @@author A0132764E
	// SEARCH command
	public static String ERROR_SEARCH_NO_PARAMETER = "No search parameters specified";
	public static String HEADER_SEARCH = "Search results";
	public static String MESSAGE_SEARCH = "%d match(es)";
	public static String ERROR_SEARCH = "Error";

	// @@author A0125990Y
	// UNDO command
	public static String MESSAGE_UNDO = "Undo success. Returning to previous state";
	public static String ERROR_UNDO = "Undo fail: No more previous state";
	public static String NO_MORE_UNDO = "No commands to undo";
	public static String UNDO_ADD = "Undo previous ADD operation: ";
	public static String UNDO_DELETE = "Undo previous DELETE operation: ";
	public static String UNDO_EDIT = "Undo previous EDIT operation: ";
	public static String UNDO_MARK = "Undo previous MARK operation: ";
	
	// INVALID command
	public static String ERROR_INVALID_CMD = "Invalid command: %1$s";
	
	// Header constants
	public static String HEADER_DISPLAY = "Displaying %1$s tasks";
	
	// @@author A0125990Y
	// Help Message 
	public static String HEADER_HELP = " Displaying Help List";
	public static String ERROR_HELP = " No such Help List";

	// @@author A0126120B
	// Enum values for the different statusbar statuses
	public enum StatusType {
		SUCCESS, ERROR, INFO;
	}
	
	// @@author A0126120B
	// Enum values for the different primary views
	public enum ViewType {
		TASK_LIST, TEXT_VIEW;
	}
	
	// @@author A0126120B
	public enum ScrollDirection {
		UP, DOWN;
	}
	
	// @@author A0126120B
	public enum ActionType {
		SCROLL_TASK_LIST_TO, SCROLL_TASK_LIST_TO_TOP, EXIT;
	}
	
	// @@author A0126120B
	public static Queue<String> getItemColorsQueue() {
		Queue<String> queue = new LinkedList<String>();
		queue.add(ITEM_PURPLE);
		queue.add(ITEM_BLUE);
		queue.add(ITEM_GREEN);
		queue.add(ITEM_YELLOW);
		queue.add(ITEM_ORANGE);
		queue.add(ITEM_RED);
		queue.add(ITEM_PINK);
		return queue;
	}
}
