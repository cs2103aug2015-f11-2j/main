package app.model;

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
	
	public void setAll(TaskList tasks) {
		taskList.setAll(tasks.getTaskList());
	}
	
	
	public void addTask(Task task) {
		taskList.add(task);
	}

	public void markTaskById(Integer index) {
		Task specifiedTask = taskList.get(index-1);
		if (specifiedTask.isCompleted() == false) {
			specifiedTask.setCompleted(true);
		} else if (specifiedTask.isCompleted() == true) {
			specifiedTask.setCompleted(false);
		}
	}
	
}
