package app.constants;

// @@author A0125960E
public class StorageConstants {
	// file locations
	public static final String FILE_CONFIGURATION = "config.properties";
	public static final String FILE_DEFAULT_STORAGE = "next.txt";
	public static final String FILE_DEFAULT_LOG = "logs/next.log";

	// configuration file properties
	public static final String PROPERTIES_STORAGE_FILE_LOCATION = "storageFileLocation";
	public static final String PROPERTIES_LOG_FILE_LOCATION = "logFileLocation";
	public static final String PROPERTIES_SELECTED_THEME = "selectedTheme";
	
	// index of observers in the observer list
	public static final int OBSERVER_INDEX_STORAGE = 0;
	public static final int OBSERVER_INDEX_LOG = 1;

	// error messages
	public static final String ERROR_INITIALIZE_TASKSTORAGE = "IOException while initializing TaskStorage";
	public static final String ERROR_WRITE_PROPERTIES = "IOException while writing properties to configuration file";
	public static final String ERROR_WRITE_TASKS = "IOException while writing tasks to storage file";
	public static final String ERROR_READ_PROPERTIES = "IOException while reading properties from configuration file";
	public static final String ERROR_READ_TASKS = "IOException while reading tasks from storage file";
	public static final String ERROR_TO_CANONICAL_PATH = "IOException while converting path to canonical";
	
	public static final String LOG_USER_INPUT = "User pressed enter key with input: %1$s";
	public static final String LOG_INITIALIZE_VIEW = "Initializing view: %1$s";
	public static final String LOG_EXECUTE_COMMAND = "Executing %1$s object";
}
