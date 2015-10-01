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
		TaskList inputList = new TaskList();
		String input = "add buy milk due 15/11/15 0959";
		Command cmd = CommandController.getInstance().createCommand(input);
		Task task = new Task(cmd);
		
		inputList.addTask(task);
		
		TaskStorage.getInstance().writeTasks(inputList);
		
		TaskList outputList = TaskStorage.getInstance().readTasks();
		assertEquals("buy milk", outputList.getTaskList().get(0).getName());
	}

}
