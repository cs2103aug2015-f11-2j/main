package tests.integration;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import app.Main;
import app.logic.command.CommandExit;
import app.storage.AppStorage;

public class AddTasksIntegrationTest {

	@Test
	public void testAddTasks() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/integration1.txt");

		String[] commands = Arrays.asList("add first task", 
			"add second task",
			"exit"
		).stream().toArray(String[]::new);
		
		try {
			Main.main(commands);
			// TODO: assertions here to ensure commands are successful.
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}

	}

}
