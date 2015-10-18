package app.constants;

public class TaskConstants {

	// Enum values for the different priority levels
	public enum Priority {
		HIGH, MEDIUM, LOW, NONE;
	}

	// Enum value for different types of display
	public enum DisplayType {
		COMPLETED, UNCOMPLETED, ALL, INVALID;
	}
	
	//Enum value for specifying fields to remove from a task
	public enum RemovableField {
		DATE, PRIORITY;
	}

}
