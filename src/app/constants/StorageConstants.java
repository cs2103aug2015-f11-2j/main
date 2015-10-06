package app.constants;

public class StorageConstants {
	// file and directory locations
	public static String FILE_CONFIG_PROPERTIES = "config.properties";
	public static String FILE_SAVE = "next.txt";
	
	// application configuration properties
	public static String PROPERTIES_SAVE_LOCATION = "saveLocation";
	public static String PROPERTIES_LOG_FILE_LOCATION = "logFileLocation";
	public static String PROPERTIES_SELECTED_THEME = "selectedTheme";
	
	// error messages
	public static String ERROR_INITIALIZE_APPSTORAGE = "IOException while initializing AppStorage";
	public static String ERROR_INITIALIZE_TASKSTORAGE = "IOException while initializing TaskStorage";
	public static String ERROR_WRITE_PROPERTIES = "IOException while writing properties to file";
	public static String ERROR_WRITE_TASKS = "IOException while writing tasks to file";
	public static String ERROR_READ_PROPERTIES = "IOException while reading properties from file";
	public static String ERROR_READ_TASKS = "IOException while reading tasks from file";
	public static String ERROR_GET_WORKING_DIRECTORY = "IOException while getting current working directory path";
}
