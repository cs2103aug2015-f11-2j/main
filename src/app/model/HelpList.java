package app.model;

import java.util.ArrayList;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HelpList {
	ObservableList<Task> helpList;

	public HelpList() {
		helpList = FXCollections.observableArrayList();
	}

	public HelpList(ArrayList<Task> list) {
		helpList = FXCollections.observableArrayList(list);
	}

	// Constructor to make a copy from another HelpList
	public HelpList(HelpList helpList) {
		this.helpList = FXCollections.observableArrayList(helpList.getTaskList());
	}

	public ObservableList<Task> getTaskList() {
		return helpList;
	}

	public void setAll(HelpList tasks) {
		helpList.setAll(tasks.getTaskList());
	}



	/**
	 * Sorts this task list in-place.
	 */
	public void sort() {
		helpList.sort((t1, t2) -> t1.compareTo(t2));
	}

	// toggle isCompleted for the task at index location
	public void markTaskByIndex(Integer index) {
		Task specifiedTask = helpList.get(index);
		specifiedTask.setCompleted(!specifiedTask.isCompleted());
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
	// tasks in helpList
	public ArrayList<Integer> getTasksIdList(ArrayList<UUID> tasksUuidList) {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int j = 0; j < tasksUuidList.size(); j++) {
			for (int i = 0; i < helpList.size(); i++) {
				if (getTaskUuidByIndex(i).equals(tasksUuidList.get(j))) {
					idList.add(i);
				}
			}
		}
		return idList;
	}

	// Takes in the index of a specific task and return its isCompleted value
	public boolean isTaskCompleted(int i) {
		return helpList.get(i).isCompleted();
	}

	// Takes in the index of a task and returns its uuid
	public UUID getTaskUuidByIndex(int index) {
		return helpList.get(index).getId();
	}

	// Takes in the uuid of a task and returns its index in the helpList
	public Integer getTaskIndexByUuid(UUID uuid) {
		for (int i = 0; i < helpList.size(); i++) {
			if (uuid.equals(getTaskUuidByIndex(i))) {
				return i;
			}
		}
		return null;
	}


	public Task getTaskByIndex(int index) {
		return helpList.get(index);
	}


}
