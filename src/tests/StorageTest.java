package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import app.logic.CommandController;
import app.logic.command.Command;
import app.model.Task;
import app.model.TaskList;
import app.storage.AppStorage;
import app.storage.TaskStorage;

public class StorageTest {

	// @@author A0125960E
	@Test
	public void testStorage() throws Exception {
		String userStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
		String userLogFileLocation = AppStorage.getInstance().getLogFileLocation();
		String userSelectedTheme = AppStorage.getInstance().getSelectedTheme();

		/* AppStorage */
		// set properties
		AppStorage.getInstance().setStorageFileLocation("testStorage/next.txt");
		AppStorage.getInstance().setLogFileLocation("testLog/next.log");
		AppStorage.getInstance().setSelectedTheme("dark");

		// get properties
		String currentDirectoryLocation = AppStorage.getInstance().toValidCanonicalPath(new File(".").getCanonicalPath());
		assertEquals(currentDirectoryLocation + "/testStorage/next.txt",
				AppStorage.getInstance().getStorageFileLocation());
		assertEquals(currentDirectoryLocation+ "/testLog/next.log",
				AppStorage.getInstance().getLogFileLocation());
		assertEquals("dark", AppStorage.getInstance().getSelectedTheme());

		File testFile = createTestFile();
		
		try {
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
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(userStorageFileLocation);
		}

		// default properties
		AppStorage.getInstance().setToDefaultStorageFileLocation();;
		AppStorage.getInstance().setToDefaultLogFileLocation();
		AppStorage.getInstance().setToDefaultSelectedTheme();
		assertEquals(currentDirectoryLocation + "/next.txt",
				AppStorage.getInstance().getStorageFileLocation());
		assertEquals(currentDirectoryLocation + "/logs/next.log",
				AppStorage.getInstance().getLogFileLocation());
		assertEquals("light", AppStorage.getInstance().getSelectedTheme());

		AppStorage.getInstance().setStorageFileLocation(userStorageFileLocation);
		AppStorage.getInstance().setLogFileLocation(userLogFileLocation);
		AppStorage.getInstance().setSelectedTheme(userSelectedTheme);
		
		removeFileAndParentsIfEmpty(new File(currentDirectoryLocation + "/testLog/next.log").toPath());
	}

	private File createTestFile() {
		File testFile = new File(AppStorage.getInstance().getStorageFileLocation());
		
		if (!testFile.exists()) {
			if (testFile.getParentFile() != null) {
				testFile.getParentFile().mkdirs();
			}

			try {
				testFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			TaskStorage.getInstance().writeTasks(new TaskList());
		}

		return testFile;
	}

	// @@author A0125960E-reused
	private void removeFileAndParentsIfEmpty(Path path) throws IOException {
		if (path == null) {
			return;
		}

		if (Files.isRegularFile(path)) {
			Files.deleteIfExists(path);
		} else if (Files.isDirectory(path)) {
			File file = path.toFile();

			if (file.list().length != 0) {
				return;
			}

			try {
				Files.delete(path);
			} catch (DirectoryNotEmptyException e) {
				return;
			}
		}

		removeFileAndParentsIfEmpty(path.getParent());
	}
}
