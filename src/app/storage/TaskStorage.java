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

public class TaskStorage extends Observer {
	private static TaskStorage taskStorage;

	private File storageFile;
	private Gson gson;

	// @@author A0125960E
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
	public static TaskStorage getInstance() {
		if (taskStorage == null) {
			taskStorage = new TaskStorage();
		}

		return taskStorage;
	}

	// @@author A0125960E
	public void writeTasks(TaskList taskList) {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(storageFile))) {
			gson.toJson(taskList.getTaskList(), bufferedWriter);
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(StorageConstants.ERROR_WRITE_TASKS);
		}
	}

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

	@Override
	public void update() {
		storageFile = new File(AppStorage.getInstance().getStorageFileLocation());
	}
}
