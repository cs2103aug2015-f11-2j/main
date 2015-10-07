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

public class TaskStorage {
	private static TaskStorage taskStorage;

	private File file;
	private Gson gson;

	private TaskStorage() {
		gson = new GsonBuilder().setPrettyPrinting().create();
		file = new File(AppStorage.getInstance().getSaveLocation());

		if (!file.exists()) {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}

			try {
				file.createNewFile();
			} catch (IOException e) {
				LogHelper.getLogger().severe(StorageConstants.ERROR_INITIALIZE_TASKSTORAGE);
			}

			writeTasks(new TaskList());
		}
	}

	public static TaskStorage getInstance() {
		if (taskStorage == null) {
			taskStorage = new TaskStorage();
		}

		return taskStorage;
	}

	public void writeTasks(TaskList taskList) {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
			gson.toJson(taskList.getTaskList(), bufferedWriter);
		} catch (IOException e) {
			LogHelper.getLogger().severe(StorageConstants.ERROR_WRITE_TASKS);
		}
	}

	public TaskList readTasks() {
		TaskList taskList = null;

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			Type type = new TypeToken<ArrayList<Task>>(){}.getType();
			ArrayList<Task> arrayList = gson.fromJson(bufferedReader, type);
			taskList = new TaskList(arrayList);
		} catch (IOException e) {
			LogHelper.getLogger().severe(StorageConstants.ERROR_READ_TASKS);
			taskList = new TaskList();
		}

		return taskList;
	}
}
