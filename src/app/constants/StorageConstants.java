package app.constants;

// @@author A0125960E
public class StorageConstants {
	// file locations
	public static String FILE_CONFIGURATION = "config.properties";
	public static String FILE_DEFAULT_STORAGE = "next.txt";
	public static String FILE_DEFAULT_LOG = "logs/next.log";

	// configuration file properties
	public static String PROPERTIES_STORAGE_FILE_LOCATION = "storageFileLocation";
	public static String PROPERTIES_LOG_FILE_LOCATION = "logFileLocation";
	public static String PROPERTIES_SELECTED_THEME = "selectedTheme";
	
	// location of parameters
	public static int PARAM_POSITION_STORAGE = 0;
	public static int PARAM_POSITION_LOG = 1;

	// error messages
	public static String ERROR_INITIALIZE_TASKSTORAGE = "IOException while initializing TaskStorage";
	public static String ERROR_WRITE_PROPERTIES = "IOException while writing properties to configuration file";
	public static String ERROR_WRITE_TASKS = "IOException while writing tasks to storage file";
	public static String ERROR_READ_PROPERTIES = "IOException while reading properties from configuration file";
	public static String ERROR_READ_TASKS = "IOException while reading tasks from storage file";
	public static String ERROR_TO_CANONICAL_PATH = "IOException while converting path to canonical";
}
