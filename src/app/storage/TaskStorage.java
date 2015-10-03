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

import app.model.Task;
import app.model.TaskList;

public class TaskStorage {
	private static TaskStorage taskStorage;

	private File file;
	private Gson gson;

	private TaskStorage() {
		gson = new GsonBuilder().setPrettyPrinting().create();
		file = new File("next.txt");

		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				readTasks();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static TaskStorage getInstance() {
		if (taskStorage == null) {
			taskStorage = new TaskStorage();
		}

		return taskStorage;
	}

	public void writeTasks(TaskList taskList) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

			gson.toJson(taskList.getTaskList(), bufferedWriter);

			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TaskList readTasks() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

			Type type = new TypeToken<ArrayList<Task>>(){}.getType();
			ArrayList<Task> arrayList = gson.fromJson(bufferedReader, type);
			TaskList taskList = new TaskList(arrayList);

			bufferedReader.close();

			return taskList;
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
	}
}
