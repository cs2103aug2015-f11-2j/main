package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import app.constants.TaskConstants.Priority;
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

			// have task name
			input = "add add1";
			viewState = CommandController.getInstance().executeCommand(input);
			List<Task> tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Added task: add1", viewState.getStatusMessage());
			assertEquals(1, tasks.size());
			assertEquals("add1", viewState.getTaskList().getTaskByIndex(0).getName());

			// have task name, have parameters
			input = "add add2 priority high from 01/01/01 1pm to 02/02/02 2pm";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Added task: add2", viewState.getStatusMessage());
			assertEquals(2, tasks.size());
			assertEquals("add2", viewState.getTaskList().getTaskByIndex(1).getName());
			assertEquals(Priority.HIGH, viewState.getTaskList().getTaskByIndex(1).getPriority());
			assertEquals(LocalDateTime.of(2001, 1, 1, 13, 0), viewState.getTaskList().getTaskByIndex(1).getStartDate());
			assertEquals(LocalDateTime.of(2002, 2, 2, 14, 0), viewState.getTaskList().getTaskByIndex(1).getEndDate());

			// no task name, have parameters
			input = "add priority high from 01/01/01 1pm to 02/02/02 2pm";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified", viewState.getStatusMessage());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}
	
	//@Test
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
			ArrayList<Task> testTasks = new ArrayList<Task>();
			for (int i = 0; i < 5; i++) {
				Task task = new Task(CommandController.getInstance().createCommand("add delete" + (i + 1)));
				testTasks.add(task);
				master.addTask(task);
				display.addTask(task);
			}
			TaskStorage.getInstance().writeTasks(master);

			// no parameters
			String input = "delete";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified", viewState.getStatusMessage());

			/* id: equivalence partition: [1, display.getTaskList().size()] */

			// 1 id
			// boundary case: 0 (invalid)
			input = "delete 0";
			viewState = CommandController.getInstance().executeCommand(input);
			List<Task> tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			
			// boundary case: 5 (invalid)
			input = "delete 5";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());

			// boundary case: 1 (valid)
			input = "delete 1";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			List<String> logFileLines = Files.readAllLines(new File(AppStorage.getInstance().getLogFileLocation()).toPath());
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			assertEquals(4, tasks.size());
			assertEquals("INFO: Deleted task: " + testTasks.remove(0).getId(), logFileLines.get(logFileLines.size() - 1));
			
			// boundary case: display.getTaskList().size() [4] (valid)
			input = "delete 4";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			logFileLines = Files.readAllLines(new File(AppStorage.getInstance().getLogFileLocation()).toPath());
			assertEquals("Deleted task: 4", viewState.getStatusMessage());
			assertEquals(3, tasks.size());
			assertEquals("INFO: Deleted task: " + testTasks.remove(3).getId(), logFileLines.get(logFileLines.size() - 1));

			// multiple id
			input = "delete 1 2";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			logFileLines = Files.readAllLines(new File(AppStorage.getInstance().getLogFileLocation()).toPath());
			assertEquals("Deleted task: 1 2", viewState.getStatusMessage());
			assertEquals(1, tasks.size());
			assertEquals("INFO: Deleted task: " + testTasks.remove(1).getId() + ", " + testTasks.remove(0).getId(),
					logFileLines.get(logFileLines.size() - 1));

			// duplicate id
			input = "delete 1 1";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks().getTaskList();
			logFileLines = Files.readAllLines(new File(AppStorage.getInstance().getLogFileLocation()).toPath());
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			assertEquals(0, tasks.size());
			assertEquals("INFO: Deleted task: " + testTasks.remove(0).getId(), logFileLines.get(logFileLines.size() - 1));
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}

	@Test
	public void testCommandDisplay() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testdisplay.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();

		try {
			/* command contents: equivalence partition: [no parameters (default), "all", "completed", "uncompleted"] */

			// populate tasklist
			Task task = new Task(CommandController.getInstance().createCommand("add completed task"));
			master.addTask(task);
			task = new Task(CommandController.getInstance().createCommand("add uncompleted task"));
			master.addTask(task);
			master.markTaskByIndex(0);

			// no parameters (default)
			String input = "display";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getStatusMessage());
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getHeader());
			assertEquals(1, viewState.getTaskList().getTaskList().size());
			assertEquals("uncompleted task", viewState.getTaskList().getTaskByIndex(0).getName());

			// "all"
			input = "display all";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying ALL tasks", viewState.getStatusMessage());
			assertEquals("Displaying ALL tasks", viewState.getHeader());
			assertEquals(2, viewState.getTaskList().getTaskList().size());
			assertEquals("completed task", viewState.getTaskList().getTaskByIndex(0).getName());
			assertEquals("uncompleted task", viewState.getTaskList().getTaskByIndex(1).getName());
			
			// "completed"
			input = "display completed";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying COMPLETED tasks", viewState.getStatusMessage());
			assertEquals("Displaying COMPLETED tasks", viewState.getHeader());
			assertEquals(1, viewState.getTaskList().getTaskList().size());
			assertEquals("completed task", viewState.getTaskList().getTaskByIndex(0).getName());
			
			// "uncompleted"
			input = "display uncompleted";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getStatusMessage());
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getHeader());
			assertEquals(1, viewState.getTaskList().getTaskList().size());
			assertEquals("uncompleted task", viewState.getTaskList().getTaskByIndex(0).getName());
			
			// invalid
			input = "display abc";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid option. Available: all, completed, uncompleted (default)", viewState.getStatusMessage());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}
	
	@Test
	public void testCommandEdit() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testedit.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();

		try {
			// populate tasklist
			ArrayList<Task> testTasks = new ArrayList<Task>();
			for (int i = 0; i < 3; i++) {
				Task task = new Task(CommandController.getInstance().createCommand("add edit" + (i + 1)));
				testTasks.add(task);
				master.addTask(task);
				display.addTask(task);
			}
			TaskStorage.getInstance().writeTasks(master);

			// no parameters
			String input = "edit";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified to edit", viewState.getStatusMessage());

			/* id: equivalence partition: [1, display.getTaskList().size()] */

			// single change
			// edit name
			// boundary case: 0 (invalid)
			input = "edit 0 edited0";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());
			
			// boundary case: 4 (invalid)
			input = "edit 4 edited4";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());
			
			// boundary case: 1 (valid)
			UUID editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(0);
			input = "edit 1 edited1";
			viewState = CommandController.getInstance().executeCommand(input);
			int editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: edited1", viewState.getStatusMessage());
			assertEquals("edited1", viewState.getTaskList().getTaskByIndex(editedTaskIndex).getName());
			
			// boundary case: display.getTaskList().size() [3] (valid)
			editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(2);
			input = "edit 3 edited3";
			viewState = CommandController.getInstance().executeCommand(input);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: edited3", viewState.getStatusMessage());
			assertEquals("edited3", viewState.getTaskList().getTaskByIndex(editedTaskIndex).getName());
			
			// no changes to name
			input = "edit 3 edited3";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 3", viewState.getStatusMessage());
			
			// edit endDate
			editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(2);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			Task editedTask = viewState.getTaskList().getTaskByIndex(editedTaskIndex);
			input = "edit 3 by 01/01/01 1pm";
			viewState = CommandController.getInstance().executeCommand(input);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: " + editedTask.getName(), viewState.getStatusMessage());
			assertEquals(LocalDateTime.of(2001, 1, 1, 13, 0), viewState.getTaskList().getTaskByIndex(editedTaskIndex).getEndDate());
			
			// edit startDate and endDate
			editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(2);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			editedTask = viewState.getTaskList().getTaskByIndex(editedTaskIndex);
			input = "edit 3 from 01/01/01 1pm to 02/02/02 2pm";
			viewState = CommandController.getInstance().executeCommand(input);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: " + editedTask.getName(), viewState.getStatusMessage());
			assertEquals(LocalDateTime.of(2001, 1, 1, 13, 0), viewState.getTaskList().getTaskByIndex(editedTaskIndex).getStartDate());
			assertEquals(LocalDateTime.of(2002, 2, 2, 14, 0), viewState.getTaskList().getTaskByIndex(editedTaskIndex).getEndDate());

			// no changes to date
			input = "edit 3 from 01/01/01 1pm to 02/02/02 2pm";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 3", viewState.getStatusMessage());

			// edit priority
			editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(2);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			editedTask = viewState.getTaskList().getTaskByIndex(editedTaskIndex);
			input = "edit 3 priority high";
			viewState = CommandController.getInstance().executeCommand(input);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: " + editedTask.getName(), viewState.getStatusMessage());
			assertEquals(Priority.HIGH, viewState.getTaskList().getTaskByIndex(editedTaskIndex).getPriority());

			// no changes to priority
			input = "edit 3 priority high";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 3", viewState.getStatusMessage());
			
			// edit priority to none
			editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(2);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			editedTask = viewState.getTaskList().getTaskByIndex(editedTaskIndex);
			input = "edit 3 priority none";
			viewState = CommandController.getInstance().executeCommand(input);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: " + editedTask.getName(), viewState.getStatusMessage());
			assertEquals(Priority.NONE, viewState.getTaskList().getTaskByIndex(editedTaskIndex).getPriority());
			
			// no changes to priority none
			input = "edit 3 priority none";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 3", viewState.getStatusMessage());
			
			// edit date to none
			editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(2);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			editedTask = viewState.getTaskList().getTaskByIndex(editedTaskIndex);
			input = "edit 3 date none";
			viewState = CommandController.getInstance().executeCommand(input);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: " + editedTask.getName(), viewState.getStatusMessage());
			assertEquals(null, viewState.getTaskList().getTaskByIndex(editedTaskIndex).getStartDate());
			assertEquals(null, viewState.getTaskList().getTaskByIndex(editedTaskIndex).getEndDate());

			// no changes to date none
			input = "edit 3 date none";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 3", viewState.getStatusMessage());
			
			// multiple changes
			editedTaskUUID = viewState.getTaskList().getTaskUuidByIndex(0);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			editedTask = viewState.getTaskList().getTaskByIndex(editedTaskIndex);
			input = "edit 1 priority low from 02/02/02 2pm to 03/03/03 3pm";
			viewState = CommandController.getInstance().executeCommand(input);
			editedTaskIndex = viewState.getTaskList().getTaskIndexByUuid(editedTaskUUID);
			assertEquals("Edited task: " + editedTask.getName(), viewState.getStatusMessage());
			assertEquals(Priority.LOW, viewState.getTaskList().getTaskByIndex(editedTaskIndex).getPriority());
			assertEquals(LocalDateTime.of(2002, 2, 2, 14, 0), viewState.getTaskList().getTaskByIndex(editedTaskIndex).getStartDate());
			assertEquals(LocalDateTime.of(2003, 3, 3, 15, 0), viewState.getTaskList().getTaskByIndex(editedTaskIndex).getEndDate());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}
	@Test
	public void testCommandInvalid() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testedit.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();

		try {
			// empty input
			ViewState currentViewState = CommandController.getInstance().getCurrentViewState();
			String input = "";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals(viewState, currentViewState);
			
			// invalid command
			input = "abc";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid command: abc", viewState.getStatusMessage());
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

			File testFile = new File("test");
			if (!testFile.exists()) {
				testFile.createNewFile();
			}

			// storage file already exist
			input = "save test";
			viewState = CommandController.getInstance().executeCommand(input);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			assertEquals("File already exists in specified location: test", viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// log file already exist
			input = "save log test";
			viewState = CommandController.getInstance().executeCommand(input);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			assertEquals("File already exists in specified location: test", viewState.getStatusMessage());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());

			Files.delete(testFile.toPath());

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
