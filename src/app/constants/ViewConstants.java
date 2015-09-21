package app.constants;

import app.Main;

public class ViewConstants {

	// Style classes for the different statusbar statuses
	public static String STATUS_STYLE_SUCCESS = "success";
	public static String STATUS_STYLE_ERROR = "error";
	public static String STATUS_STYLE_INFO = "info";

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
	
	// MARK command
	public static String MESSAGE_MARK = "Marked task(s) by id: %1$s";
	public static String ERROR_MARK = "Error marking task: %1$s";
	public static String ERROR_MARK_NO_TASK = "No task specified to mark";
	public static String ERROR_MARK_INVALID_ID = "Error marking tasks, invalid task ID";
	
	// DISPLAY command
	public static String MESSAGE_DISPLAY = "Displaying %1$s tasks";
	public static String ERROR_DISPLAY = "Error displaying tasks";
	public static String ERROR_DISPLAY_INVALID_ARGUMENT = "Error displaying tasks, no such argument";
	
	// INVALID command
	public static String ERROR_INVALID_CMD = "Invalid command: %1$s";

	// Enum values for the different statusbar statuses
	public enum StatusType {
		SUCCESS, ERROR, INFO;
	}
	
	// Enum values for the different primary views
	public enum ViewType {
		TASK_LIST, TEXT_VIEW;
	}
}
