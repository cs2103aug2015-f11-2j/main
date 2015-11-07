package app.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import app.constants.StorageConstants;
import app.model.Task;
import app.model.TaskList;
import app.util.LogHelper;
import app.util.Observer;

// @@author A0125960E
public class TaskStorage implements Observer {
	private static TaskStorage taskStorage;

	private File storageFile;
	private Gson gson;

	/**
	 * Initializes TaskStorage.
	 */
	private TaskStorage() {
		gson = new GsonBuilder().setPrettyPrinting().create();

		update();

		if (!storageFile.exists()) {
			if (storageFile.getParentFile() != null) {
				storageFile.getParentFile().mkdirs();
			}

			try {
				storageFile.createNewFile();
			} catch (IOException e) {
				LogHelper.getInstance().getLogger().severe(StorageConstants.ERROR_INITIALIZE_TASKSTORAGE);
			}

			writeTasks(new TaskList());
		}
	}

	// @@author A0126120B
	/**
	 * @return	TaskStorage instance.
	 */
	public static TaskStorage getInstance() {
		if (taskStorage == null) {
			taskStorage = new TaskStorage();
		}

		return taskStorage;
	}

	// @@author A0125960E
	/**
	 * Write tasks in JSON strings to the storage file.
	 * 
	 * @param taskList	List of tasks to be stored.
	 */
	public void writeTasks(TaskList taskList) {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(storageFile))) {
			gson.toJson(taskList.getTaskList(), bufferedWriter);
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(StorageConstants.ERROR_WRITE_TASKS);
		}
	}

	/**
	 * Read tasks from the storage file.
	 * 
	 * @return	TaskList of the stored tasks.
	 */
	public TaskList readTasks() {
		TaskList taskList = null;

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(storageFile))) {
			Type type = new TypeToken<ArrayList<Task>>(){}.getType();
			ArrayList<Task> arrayList = gson.fromJson(bufferedReader, type);
			taskList = new TaskList(arrayList);
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(StorageConstants.ERROR_READ_TASKS);
			taskList = new TaskList();
		}

		return taskList;
	}

	/**
	 * Updates the storage file location.
	 */
	@Override
	public void update() {
		storageFile = new File(AppStorage.getInstance().getStorageFileLocation());
	}
}
