package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import app.logic.CommandController;
import app.logic.command.Command;
import app.model.Task;
import app.model.TaskList;
import app.storage.AppStorage;
import app.storage.TaskStorage;

public class StorageTest {

	@Test
	public void testStorage() {
		String userStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
		String userLogFileLocation = AppStorage.getInstance().getLogFileLocation();
		String userSelectedTheme = AppStorage.getInstance().getSelectedTheme();

		/* AppStorage */
		// default properties
		AppStorage.getInstance().setDefaultSelectedTheme();
		assertEquals("light", AppStorage.getInstance().getSelectedTheme());

		// set properties
		AppStorage.getInstance().setStorageFileLocation("testStorage/next.txt");
		AppStorage.getInstance().setLogFileLocation("testLog/next.txt");
		AppStorage.getInstance().setSelectedTheme("dark");

		// get properties
		assertEquals("dark", AppStorage.getInstance().getSelectedTheme());

		/* TaskStorage */
		/* empty tasklist */
		// writing
		TaskList writeList = new TaskList();
		TaskStorage.getInstance().writeTasks(writeList);

		// reading
		TaskList readList = TaskStorage.getInstance().readTasks();
		assertTrue(readList.getTaskList().isEmpty());

		/* populated tasklist */
		// writing
		String input = "add buy milk due 15/11/15 0959";
		Command cmd = CommandController.getInstance().createCommand(input);
		Task task = new Task(cmd);
		writeList.addTask(task);

		input = "add priority high from 3pm to 5pm";
		cmd = CommandController.getInstance().createCommand(input);
		task = new Task(cmd);
		writeList.addTask(task);

		TaskStorage.getInstance().writeTasks(writeList);

		// reading
		readList = TaskStorage.getInstance().readTasks();
		assertEquals(2, readList.getTaskList().size());
		assertEquals("buy milk", readList.getTaskList().get(0).getName());
		assertEquals("", readList.getTaskList().get(1).getName());

		AppStorage.getInstance().setStorageFileLocation(userStorageFileLocation);
		AppStorage.getInstance().setLogFileLocation(userLogFileLocation);
		AppStorage.getInstance().setSelectedTheme(userSelectedTheme);
	}
}
