package app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import app.constants.TaskConstants.Priority;
import app.constants.TaskConstants.RemovableField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskList {
	ObservableList<Task> taskList;

	public TaskList() {
		taskList = FXCollections.observableArrayList();
	}

	public TaskList(ArrayList<Task> list) {
		taskList = FXCollections.observableArrayList(list);
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
	 * Sorts this task list in-place.
	 */
	public void sort() {
		taskList.sort((t1, t2) -> t1.compareTo(t2));
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
			uuidList.add(getTaskUuidByIndex(i - 1));
		}
		return uuidList;
	}

	// Build an ArrayList of Integers which are the exact index of tasksUuidList
	// tasks in taskList
	public ArrayList<Integer> getTasksIdList(ArrayList<UUID> tasksUuidList) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int j = 0; j < tasksUuidList.size(); j++) {
			for (int i = 0; i < taskList.size(); i++) {
				if (getTaskUuidByIndex(i).equals(tasksUuidList.get(j))) {
					idList.add(i);
				}
			}
		}
		return idList;
	}

	// Takes in the index of a specific task and return its isCompleted value
	public boolean isTaskCompleted(int i) {
		return taskList.get(i).isCompleted();
	}

	// Takes in the index of a task and returns its uuid
	public UUID getTaskUuidByIndex(int index) {
		return taskList.get(index).getId();
	}

	// Takes in the uuid of a task and returns its index in the taskList
	public Integer getTaskIndexByUuid(UUID uuid) {
		for (int i = 0; i < taskList.size(); i++) {
			if (uuid.equals(getTaskUuidByIndex(i))) {
				return i;
			}
		}
		return null;
	}

	// Takes in a task to compare with the old task at specified index,
	// and updates the old task with respect to the new one
	public boolean updateTask(Task task, int index) {
		boolean isEdited = false;
		// Edit if name is not empty, not null, and not same as previous
		if (!task.getName().equals("") && task.getName() != null && 
				!task.getName().equals(taskList.get(index).getName())) {
			taskList.get(index).setName(task.getName());
			isEdited = true;
		}
		// Edit if either date is not null
		if (task.getEndDate() != null || task.getStartDate() != null) {
			taskList.get(index).setEndDate(task.getEndDate());
			taskList.get(index).setStartDate(task.getStartDate());
			isEdited = true;
		}
		// Edit if priority is not none (default priority) or same as previous
		if (task.getPriority() != Priority.NONE && 
				task.getPriority() != taskList.get(index).getPriority()) {
			taskList.get(index).setPriority(task.getPriority());
			isEdited = true;
		}
		// Remove date and/or priority if specified
		for (RemovableField field : task.getRemoveField()) {
			if (field.equals(RemovableField.PRIORITY) && 
					taskList.get(index).getPriority() != Priority.NONE) {
				taskList.get(index).setPriority(Priority.NONE);
				isEdited = true;
			}
			if (field.equals(RemovableField.DATE) && 
					(taskList.get(index).getStartDate() != null || 
					taskList.get(index).getEndDate() != null)) {
				taskList.get(index).setStartDate(null);
				taskList.get(index).setEndDate(null);
				isEdited = true;
			}
		}

		return isEdited;
	}

	public Task getTaskByIndex(int index) {
		return taskList.get(index);
	}

	public TaskList search(List<Predicate<Task>> predicates) {
		Predicate<Task> query = compositePredicate(predicates);
		TaskList results = new TaskList();
		// filter the task list using the predicates
		// for each result, add it to the results list
		taskList.stream().filter(query).forEach(t -> results.addTask(t));
		return results;
	}

	// combine all predicates with AND
	private Predicate<Task> compositePredicate(List<Predicate<Task>> predicates) {
		return predicates.stream().reduce(t -> true, Predicate::and);
	}
}
