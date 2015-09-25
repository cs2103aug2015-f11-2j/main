package app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskList {
	ObservableList<Task> taskList;

	public TaskList() {
		taskList = FXCollections.observableArrayList();
	}
	
	// Constructor to make a copy from another TaskList
	public TaskList(TaskList taskList) {
		this.taskList = FXCollections.observableArrayList(taskList.getTaskList());
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

	/**
	 * Sorts this task list in place.
	 */
	public void sort() {
		taskList.sort(this::compareTask);
	}

	/**
	 * The method used to compare tasks. The sorting order is as follows:
	 * 
	 * (1) Floating tasks, (2) Start date if not null, (3) End date if not null.
	 * 
	 * For similar dates, the subsequent sorting order is used:
	 * 
	 * (1) Priority is higher, (2) Name by lexicographical ordering
	 */
	private int compareTask(Task task1, Task task2) {
		LocalDateTime task1Key = getSortKey(task1);
		LocalDateTime task2Key = getSortKey(task2);
		int result = 0;

		if (task1Key != null && task2Key != null) {
			result = task1Key.compareTo(task2Key);
		} else if (task1Key == null && task2Key != null) {
			return -1;
		} else if (task1Key != null && task2Key == null) {
			return 1;
		}

		if (result == 0) {
			result = task1.getPriority().compareTo(task2.getPriority());
		}

		if (result == 0) {
			result = task1.getName().compareToIgnoreCase(task2.getName());
		}

		return result;
	}

	private LocalDateTime getSortKey(Task task) {
		if (task.getStartDate() == null && task.getEndDate() != null) {
			return task.getEndDate();
		} else if (task.getStartDate() != null && task.getEndDate() != null) {
			return task.getStartDate();
		}
		return null;
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
			uuidList.add(taskList.get(i - 1).getId());
		}
		return uuidList;
	}

	// Build an ArrayList of Integers which are the exact index of tasksUuidList
	// tasks in taskList
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
