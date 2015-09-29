package app.constants;

import java.util.LinkedList;
import java.util.Queue;

import app.Main;

public class ViewConstants {

	// Style classes for the different statusbar statuses
	public static String STATUS_STYLE_SUCCESS = "success";
	public static String STATUS_STYLE_ERROR = "error";
	public static String STATUS_STYLE_INFO = "info";
	
	// Style classes for task item colors
	public static String ITEM_PURPLE = "purple";
	public static String ITEM_BLUE = "blue";
	public static String ITEM_GREEN = "green";
	public static String ITEM_YELLOW = "yellow";
	public static String ITEM_ORANGE = "orange";
	public static String ITEM_RED = "red";
	public static String ITEM_PINK = "pink";

	// THEME command
	// Resource locations for the theme CSS files.
	public static String THEME_LIGHT_CSS = Main.class.getResource("view/css/theme_light.css").toExternalForm();
	public static String THEME_DARK_CSS = Main.class.getResource("view/css/theme_dark.css").toExternalForm();
	public static String THEME_LIGHT = "light";
	public static String THEME_DARK = "dark";
	public static String MESSAGE_AVAILABLE_THEMES = "Available themes: light, dark";
	public static String MESSAGE_CURRENT_THEME = "Current theme: %1$s";
	
	// ADD command
	public static String MESSAGE_ADD = "Added task: %1$s";
	public static String ERROR_ADD = "Error adding task: %1$s";
	public static String ERROR_ADD_NO_TASK = "No task specified";
	
	// SEARCH command
	public static String ERROR_SEARCH_NO_PARAMETER = "No search parameters specified";
	public static String SEARCH_RESULTS = "Search results for: %1$s";
	public static String SEARCH_RESULTS_COUNT = "Matching task(s) found: %1$s";
	
	// MARK command
	public static String MESSAGE_MARK = "Marked task(s) by id: %1$s";
	public static String ERROR_MARK = "Error marking task: %1$s";
	public static String ERROR_MARK_NO_TASK = "No task specified to mark";
	public static String ERROR_MARK_INVALID_ID = "Error marking tasks, invalid task ID";
	
	// DISPLAY command
	public static String MESSAGE_DISPLAY = "Displaying %1$s tasks";
	public static String ERROR_DISPLAY = "Error displaying tasks";
	public static String ERROR_DISPLAY_INVALID_ARGUMENT = "Invalid option. Available: all, completed, uncompleted (default)";
	
	// INVALID command
	public static String ERROR_INVALID_CMD = "Invalid command: %1$s";
	
	// Header constants
	public static String HEADER_DISPLAY = "Displaying %1$s tasks";

	// Enum values for the different statusbar statuses
	public enum StatusType {
		SUCCESS, ERROR, INFO;
	}
	
	// Enum values for the different primary views
	public enum ViewType {
		TASK_LIST, TEXT_VIEW;
	}
	
	public enum ScrollDirection {
		UP, DOWN;
	}
	
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
