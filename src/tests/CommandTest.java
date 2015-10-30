package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import app.logic.CommandController;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;
import app.storage.AppStorage;
import app.storage.TaskStorage;

public class CommandTest {

	@Test
	public void testCommandAdd() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testadd.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();

		try {
			// no parameters
			String input = "add";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified", viewState.getStatusMessage());

			// have parameters
			input = "add add1 pri high";
			viewState = CommandController.getInstance().executeCommand(input);
			List<Task> tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Added task: add1", viewState.getStatusMessage());
			assertEquals(1, tasks.size());

			// have parameters but without task name
			input = "add pri high";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified", viewState.getStatusMessage());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}
	
	@Test
	public void testCommandDelete() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testdelete.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();
		
		try {
			// populate tasklist
			Task task = new Task(CommandController.getInstance().createCommand("add delete1"));
			master.addTask(task);
			display.addTask(task);
			task = new Task(CommandController.getInstance().createCommand("add delete2"));
			master.addTask(task);
			display.addTask(task);
			task = new Task(CommandController.getInstance().createCommand("add delete3"));
			master.addTask(task);
			display.addTask(task);
			task = new Task(CommandController.getInstance().createCommand("add delete4"));
			master.addTask(task);
			display.addTask(task);
			TaskStorage.getInstance().writeTasks(master);
			
			// no parameters
			String input = "delete";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified", viewState.getStatusMessage());

			/* invalid id */
			// equivalence partition: [1, display.getTaskList().size()]
			// boundary case: 0
			input = "delete 0";
			viewState = CommandController.getInstance().executeCommand(input);
			List<Task> tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			
			// boundary case: 4
			input = "delete 4";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());

			// 1 id
			// boundary case: 1
			input = "delete 1";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			assertEquals(3, tasks.size());

			// duplicate id
			input = "delete 1 1";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			assertEquals(2, tasks.size());

			// multiple id
			// boundary case: 2
			input = "delete 1 2";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Deleted task: 1 2", viewState.getStatusMessage());
			assertEquals(0, tasks.size());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}

	@Test
	public void testCommandSave() {
		try {
			String userStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
			String userLogFileLocation = AppStorage.getInstance().getLogFileLocation();

			String prevStorageFileLocation = userStorageFileLocation;
			String prevLogFileLocation = userLogFileLocation;
			File prevStorageFile = new File(prevStorageFileLocation);
			File prevLogFile = new File(prevLogFileLocation);
			List<String> prevStorageFileLines = Files.readAllLines(prevStorageFile.toPath());

			// no storage file location
			String input = "save";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No storage file location specified", viewState.getStatusMessage());

			// no log file location
			input = "save log";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No log file location specified", viewState.getStatusMessage());

			// no changes to storage file location
			input = "save " + prevStorageFileLocation;
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Same storage file location. No changes to storage file location: "
						 + prevStorageFileLocation, viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// no changes to log file location
			input = "save log " + prevLogFileLocation;
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Same log file location. No changes to log file location: "
						 + prevLogFileLocation, viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// storage file location without file extension
			input = "save testsave/storage";
			viewState = CommandController.getInstance().executeCommand(input);
			File currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			String parentLocation = AppStorage.getInstance().toValidCanonicalPath(prevStorageFile.getParent());
			assertEquals("Saved storage file location: " + parentLocation + "/testsave/storage",
						 viewState.getStatusMessage());
			assertEquals(parentLocation + "/testsave/storage", AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertFalse(prevStorageFile.exists());
			assertFalse(prevStorageFile.equals(currStorageFile));

			prevStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
			prevStorageFile = new File(prevStorageFileLocation);

			// log file location without file extension
			List<String> prevLogFileLines = Files.readAllLines(prevLogFile.toPath());
			input = "save log testsave/log";
			viewState = CommandController.getInstance().executeCommand(input);
			File currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			List<String> currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			parentLocation = AppStorage.getInstance().toValidCanonicalPath(prevLogFile.getParentFile().getParent());
			assertEquals("Saved log file location: " + parentLocation + "/testsave/log", viewState.getStatusMessage());
			assertEquals(parentLocation + "/testsave/log", AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevLogFileLines, currLogFileLines);
			assertFalse(prevLogFile.exists());
			assertFalse(prevLogFile.equals(currLogFile));

			prevLogFileLocation = AppStorage.getInstance().getLogFileLocation();
			prevLogFile = new File(prevLogFileLocation);

			// storage file location with spaces
			input = "save test  save  /  storage  test";
			viewState = CommandController.getInstance().executeCommand(input);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			parentLocation = AppStorage.getInstance().toValidCanonicalPath(prevStorageFile.getParentFile().getParent());
			assertEquals("Saved storage file location: " + parentLocation + "/test  save/storage  test",
					viewState.getStatusMessage());
			assertEquals(parentLocation + "/test  save/storage  test",
					AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertFalse(prevStorageFile.exists());
			assertFalse(prevStorageFile.equals(currStorageFile));

			prevStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
			prevStorageFile = new File(prevStorageFileLocation);

			// log file location with spaces
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());
			input = "save log test  save  /  log  test";
			viewState = CommandController.getInstance().executeCommand(input);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			parentLocation = AppStorage.getInstance().toValidCanonicalPath(prevLogFile.getParentFile().getParent());
			assertEquals("Saved log file location: " + parentLocation + "/test  save/log  test",
					viewState.getStatusMessage());
			assertEquals(parentLocation + "/test  save/log  test", AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevLogFileLines, currLogFileLines);
			assertFalse(prevLogFile.exists());
			assertFalse(prevLogFile.equals(currLogFile));

			prevLogFileLocation = AppStorage.getInstance().getLogFileLocation();
			prevLogFile = new File(prevLogFileLocation);

			// storage file location at different directory
			input = "save ../../testsave/storage";
			viewState = CommandController.getInstance().executeCommand(input);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			parentLocation = AppStorage.getInstance()
					.toValidCanonicalPath(prevStorageFile.getParentFile().getParentFile().getParentFile().getParent());
			assertEquals("Saved storage file location: " + parentLocation + "/testsave/storage",
					viewState.getStatusMessage());
			assertEquals(parentLocation + "/testsave/storage", AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertFalse(prevStorageFile.exists());
			assertFalse(prevStorageFile.equals(currStorageFile));

			prevStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
			prevStorageFile = new File(prevStorageFileLocation);

			// log file location at different directory
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());
			input = "save log ../../testsave/log";
			viewState = CommandController.getInstance().executeCommand(input);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			parentLocation = AppStorage.getInstance()
					.toValidCanonicalPath(prevLogFile.getParentFile().getParentFile().getParentFile().getParent());
			assertEquals("Saved log file location: " + parentLocation + "/testsave/log", viewState.getStatusMessage());
			assertEquals(parentLocation + "/testsave/log", AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevLogFileLines, currLogFileLines);
			assertFalse(prevLogFile.exists());
			assertFalse(prevLogFile.equals(currLogFile));

			prevLogFileLocation = AppStorage.getInstance().getLogFileLocation();
			prevLogFile = new File(prevLogFileLocation);

			// default storage file location
			input = "save default";
			viewState = CommandController.getInstance().executeCommand(input);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			parentLocation = AppStorage.getInstance().toValidCanonicalPath(".");
			assertEquals("Saved storage file location: " + parentLocation + "/next.txt", viewState.getStatusMessage());
			assertEquals(parentLocation + "/next.txt", AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertFalse(prevStorageFile.exists());
			assertFalse(prevStorageFile.equals(currStorageFile));

			prevStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
			prevStorageFile = new File(prevStorageFileLocation);

			// default log file location
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());
			input = "save log default";
			viewState = CommandController.getInstance().executeCommand(input);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			parentLocation = AppStorage.getInstance().toValidCanonicalPath(".");
			assertEquals("Saved log file location: " + parentLocation + "/logs/next.log", viewState.getStatusMessage());
			assertEquals(parentLocation + "/logs/next.log", AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevLogFileLines, currLogFileLines);
			assertFalse(prevLogFile.exists());
			assertFalse(prevLogFile.equals(currLogFile));

			prevLogFileLocation = AppStorage.getInstance().getLogFileLocation();
			prevLogFile = new File(prevLogFileLocation);

			// no changes for default storage file location
			input = "save default";
			viewState = CommandController.getInstance().executeCommand(input);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			assertEquals("Same storage file location. No changes to storage file location: " + prevStorageFileLocation,
					viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// no changes for default log file location
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());
			input = "save log default";
			viewState = CommandController.getInstance().executeCommand(input);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("Same log file location. No changes to log file location: " + prevLogFileLocation,
					viewState.getStatusMessage());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());

			input = "save " + userStorageFileLocation;
			viewState = CommandController.getInstance().executeCommand(input);

			input = "save log " + userLogFileLocation;
			viewState = CommandController.getInstance().executeCommand(input);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
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
