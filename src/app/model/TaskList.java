package app.model;


import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskList {
	ObservableList<Task> taskList;
	
	public TaskList() {
		taskList = FXCollections.observableArrayList();
	}

	public ObservableList<Task> getTaskList() {
		return taskList;
	}
	
	public void addAll(TaskList tasks) {
		taskList.addAll(tasks.getTaskList());
	}
	
	
	public void addTask(Task task) {
		taskList.add(task);
	}
	
}
