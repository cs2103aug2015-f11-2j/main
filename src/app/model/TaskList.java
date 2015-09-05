package app.model;

import java.util.ArrayList;

public class TaskList {
	ArrayList<Task> taskList;
	
	public TaskList() {
		taskList = new ArrayList<Task>();
	}

	public ArrayList<Task> getTaskList() {
		return taskList;
	}
	
	public void addTask(Task task) {
		taskList.add(task);
	}
	
}
