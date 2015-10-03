package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import app.controller.CommandController;
import app.model.Task;
import app.model.TaskList;
import app.model.command.Command;
import app.storage.TaskStorage;

public class StorageTest {

	@Test
	public void testReadAndWriteTasks() {
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
	}

}
