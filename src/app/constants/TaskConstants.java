package app.constants;

public class TaskConstants {

	// @@author A0125360R
	public static final String MARK_ALL_TASK = "ALL";
	
	// @@author A0126120B
	// Enum values for the different priority levels
	public enum Priority {
		HIGH, MEDIUM, LOW, NONE;
	}

	// @@author A0125360R
	// Enum value for different types of display
	public enum DisplayType {
		COMPLETED, UNCOMPLETED, ALL, INVALID;
	}
	
	//Enum value for specifying fields to remove from a task
	public enum RemovableField {
		DATE, PRIORITY;
	}

}
