package app.model;

import java.util.ArrayList;
import java.util.UUID;

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

	// toggle isCompleted for the task at index location
	public void markTaskByIndex(Integer index) {
		Task specifiedTask = taskList.get(index);
		specifiedTask.setCompleted(!specifiedTask.isCompleted());
	}
	
	// Build a TaskList containing either only completed or uncompleted tasks
	public TaskList getTaskListByCompletion(boolean isCompleted) {
		TaskList tasks = new TaskList();
		for (Task t : taskList) {
			if (t.isCompleted() == isCompleted) {
				tasks.addTask(t);
			}
		}
		return tasks;
	}

	// Build an ArrayList of UUID of the tasks in the displayedTaskIdList
	public ArrayList<UUID> getTasksUuidList(ArrayList<Integer> displayedTaskIdList) {
		ArrayList<UUID> uuidList = new ArrayList<UUID>();
		for (Integer i : displayedTaskIdList) {
			uuidList.add(taskList.get(i-1).getId());
		}
		return uuidList;
	}

	// Build an ArrayList of Integers which are the exact index of tasksUuidList tasks in taskList
	public ArrayList<Integer> getTasksIdList(ArrayList<UUID> tasksUuidList) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int j = 0; j < tasksUuidList.size(); j++) {
			for (int i = 0; i < taskList.size(); i++) {
				if (taskList.get(i).getId().equals(tasksUuidList.get(j))) {
					idList.add(i);
				}
			}
		}
		return idList;
	}
}
