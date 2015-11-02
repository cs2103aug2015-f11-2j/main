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

import org.junit.Test;

import app.constants.TaskConstants.Priority;
import app.logic.CommandController;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;
import app.storage.AppStorage;
import app.storage.TaskStorage;

public class CommandTest {

	/**
	 * Equivalence partition
	 * command content: [have task name and no parameters], [have task name and have parameters]
	 * 
	 * Boundary case
	 * command content:
	 * no task name and no parameters (invalid), have task name and no parameters (valid),
	 * have task name and have parameters (valid), no task name and have parameters (invalid)
	 * 
	 * Note: parameters include startDate, endDate and priority.
	 */
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
			// command content: no task name and no parameters (invalid)
			String input = "add";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified", viewState.getStatusMessage());

			// command content: have task name and no parameters (valid)
			input = "add add1";
			viewState = CommandController.getInstance().executeCommand(input);
			TaskList tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Added task: add1", viewState.getStatusMessage());
			assertEquals(1, tasks.getTaskList().size());
			assertEquals("add1", tasks.getTaskByIndex(0).getName());

			// command content: have task name and have parameters (valid)
			input = "add add2 priority high from 01/01/01 1pm to 02/02/02 2pm";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Added task: add2", viewState.getStatusMessage());
			assertEquals(2, tasks.getTaskList().size());
			assertEquals("add2", tasks.getTaskByIndex(1).getName());
			assertEquals(Priority.HIGH, tasks.getTaskByIndex(1).getPriority());
			assertEquals(LocalDateTime.of(2001, 1, 1, 13, 0), tasks.getTaskByIndex(1).getStartDate());
			assertEquals(LocalDateTime.of(2002, 2, 2, 14, 0), tasks.getTaskByIndex(1).getEndDate());

			// command content: no task name and have parameters (invalid)
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

	/**
	 * Equivalence partition
	 * command content: [single id], [multiple id], [duplicate id]
	 * id: [1 .. display.getTaskList().size()]
	 * 
	 * Boundary case
	 * command content:
	 * no id (invalid), single id (valid), multiple id (valid), duplicate id (valid), any other String (invalid)
	 * 
	 * id:
	 * 0 (invalid), 1 (valid), display.getTaskList().size() (valid), display.getTaskList().size() + 1 (invalid)
	 */
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
			ArrayList<Task> testTasks = new ArrayList<Task>();
			for (int i = 0; i < 5; i++) {
				Task task = new Task(CommandController.getInstance().createCommand("add delete" + (i + 1)));
				testTasks.add(task);
				master.addTask(task);
				display.addTask(task);
			}
			TaskStorage.getInstance().writeTasks(master);
			
			// command content: no id (invalid)
			String input = "delete";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified", viewState.getStatusMessage());
/*
			// command content: single id (valid)
			// id: 0 (invalid)
			input = "delete 0";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("", viewState.getStatusMessage());

			// command content: single id (valid)
			// id: display.getTaskList().size() + 1 [6] (invalid)
			input = "delete 6";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("", viewState.getStatusMessage());
*/
			// command content: single id (valid)
			// id: 1 (valid)
			input = "delete 1";
			viewState = CommandController.getInstance().executeCommand(input);
			TaskList tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			assertEquals(4, tasks.getTaskList().size());
			assertEquals(null, tasks.getTaskIndexByUuid(testTasks.remove(0).getId()));

			// command content: single id (valid)
			// id: display.getTaskList().size() [4] (valid)
			input = "delete 4";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Deleted task: 4", viewState.getStatusMessage());
			assertEquals(3, tasks.getTaskList().size());
			assertEquals(null, tasks.getTaskIndexByUuid(testTasks.remove(3).getId()));
/*
			// command content: any other String (invalid)
			// id: any valid value
			input = "delete abc";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());
*/
			// command content: multiple id (valid)
			// id: any valid value
			input = "delete 1 3";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Deleted task: 1 3", viewState.getStatusMessage());
			assertEquals(1, tasks.getTaskList().size());
			assertEquals(null, tasks.getTaskIndexByUuid(testTasks.remove(2).getId()));
			assertEquals(null, tasks.getTaskIndexByUuid(testTasks.remove(0).getId()));
/*
			// command content: duplicate id (valid)
			// id: any valid value
			input = "delete 1 1";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Deleted task: 1", viewState.getStatusMessage());
			assertEquals(0, tasks.getTaskList().size());
			assertEquals(null, tasks.getTaskIndexByUuid(testTasks.remove(0).getId()));*/
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}

	/**
	 * Equivalence partition
	 * command content: [""], ["all"], ["completed"], ["uncompleted"]
	 * 
	 * Boundary case
	 * command content:
	 * "" (valid), "all" (valid), "completed" (valid), "uncompleted" (valid), any other String (invalid)
	 */
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
			// populate tasklist
			Task task = new Task(CommandController.getInstance().createCommand("add completed task"));
			master.addTask(task);
			task = new Task(CommandController.getInstance().createCommand("add uncompleted task"));
			master.addTask(task);
			master.markTaskByIndex(0);

			// command content: "" (valid)
			String input = "display";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getStatusMessage());
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getHeader());
			assertEquals(1, viewState.getTaskList().getTaskList().size());
			assertEquals("uncompleted task", viewState.getTaskList().getTaskByIndex(0).getName());

			// command content: "all" (valid)
			input = "display all";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying ALL tasks", viewState.getStatusMessage());
			assertEquals("Displaying ALL tasks", viewState.getHeader());
			assertEquals(2, viewState.getTaskList().getTaskList().size());
			assertEquals("completed task", viewState.getTaskList().getTaskByIndex(0).getName());
			assertEquals("uncompleted task", viewState.getTaskList().getTaskByIndex(1).getName());
			
			// command content: "completed" (valid)
			input = "display completed";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying COMPLETED tasks", viewState.getStatusMessage());
			assertEquals("Displaying COMPLETED tasks", viewState.getHeader());
			assertEquals(1, viewState.getTaskList().getTaskList().size());
			assertEquals("completed task", viewState.getTaskList().getTaskByIndex(0).getName());
			
			// command content: "uncompleted" (valid)
			input = "display uncompleted";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getStatusMessage());
			assertEquals("Displaying UNCOMPLETED tasks", viewState.getHeader());
			assertEquals(1, viewState.getTaskList().getTaskList().size());
			assertEquals("uncompleted task", viewState.getTaskList().getTaskByIndex(0).getName());
			
			// command content: any other String (invalid)
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

	/**
	 * Equivalence partition
	 * command content: [have id and have parameters with changes]
	 * id: [1 .. display.getTaskList().size()]
	 * date: ["none"], [valid date from DateParser]
	 * priority: ["none"], [valid priority from CommandParser]
	 * 
	 * Boundary case
	 * command content:
	 * no id and no parameters (invalid), no id and have parameters (invalid),
	 * have id and no parameters (invalid), have id and have parameters without changes (invalid),
	 * have id and have parameters with changes (valid)
	 * 
	 * id:
	 * 0 (invalid), 1 (valid), display.getTaskList().size() (valid), display.getTaskList().size() + 1 (invalid)
	 * 
	 * date:
	 * "none" (valid), valid values from DateParser (valid)
	 * 
	 * priority:
	 * "none" (valid), valid values from CommandParser (valid)
	 * 
	 * Note: parameters include name, startDate, endDate and priority. Invalid values for
	 * date and priority are tested in DateParserTest and CommandParserTest respectively.
	 */
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

			// command content: no id and no parameters (invalid)
			String input = "edit";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified to edit", viewState.getStatusMessage());

			// command content: no id and have parameters (invalid)
			input = "edit priority high";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified to edit", viewState.getStatusMessage());

			// command content: have id and have parameters with changes (valid)
			// id: 0 (invalid)
			// changes to name
			input = "edit 0 edited0";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());

			// command content: have id and have parameters with changes (valid)
			// id: 4 (invalid)
			// changes to name
			input = "edit 4 edited4";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());

			// command content: have id and have parameters with changes (valid)
			// id: 1 (valid)
			// changes to name
			input = "edit 1 edited1";
			viewState = CommandController.getInstance().executeCommand(input);
			TaskList tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edited1", viewState.getStatusMessage());
			assertEquals("edited1", tasks.getTaskByIndex(0).getName());

			// command content: have id and have parameters with changes (valid)
			// id: display.getTaskList().size() [3] (valid)
			// changes to name
			input = "edit 3 edited3";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edited3", viewState.getStatusMessage());
			assertEquals("edited3", tasks.getTaskByIndex(0).getName());
			
			// command content: have id and have parameters without changes (invalid)
			// id: any valid values
			// no changes to name
			input = "edit 3 edited3";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 3", viewState.getStatusMessage());
			
			// command content: have id and have parameters with changes (valid)
			// id: any valid values
			// date: valid values from DateParser
			// changes to startDate and endDate
			input = "edit 3 from 01/01/01 1pm to 03/03/03 3pm";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edited3", viewState.getStatusMessage());
			assertEquals(LocalDateTime.of(2001, 1, 1, 13, 0), tasks.getTaskByIndex(0).getStartDate());
			assertEquals(LocalDateTime.of(2003, 3, 3, 15, 0), tasks.getTaskByIndex(0).getEndDate());

			// command content: have id and have parameters without changes (invalid)
			// id: any valid values
			// date: valid values from DateParser
			// no changes to startDate and endDate
			input = "edit 3 from 01/01/01 1pm to 03/03/03 3pm";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("No changes made for task 3", viewState.getStatusMessage());

			// command content: have id and have parameters with changes (valid)
			// id: any valid values
			// date: valid values from DateParser
			// changes to startDate
			input = "edit 3 by 03/03/03 3pm";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edited3", viewState.getStatusMessage());
			assertEquals(null, tasks.getTaskByIndex(0).getStartDate());

			// command content: have id and have parameters with changes (valid)
			// id: any valid values
			// date: valid values from DateParser
			// changes to endDate
			input = "edit 3 by 02/02/02 2pm";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edited3", viewState.getStatusMessage());
			assertEquals(LocalDateTime.of(2002, 2, 2, 14, 0), tasks.getTaskByIndex(0).getEndDate());

			// command content: have id and have parameters with changes (valid)
			// id: any valid values
			// date: "none"
			// changes to dates
			input = "edit 3 date none";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edited3", viewState.getStatusMessage());
			assertEquals(null, tasks.getTaskByIndex(0).getEndDate());

			// command content: have id and have parameters without changes (invalid)
			// id: any valid values
			// date: "none"
			// no changes to startDate and endDate
			input = "edit 3 date none";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("No changes made for task 3", viewState.getStatusMessage());

			// command content: have id and have parameters with changes (valid)
			// id: any valid values
			// priority: valid values from CommandParser
			// changes to priority
			input = "edit 1 priority high";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edit2", viewState.getStatusMessage());
			assertEquals(Priority.HIGH, tasks.getTaskByIndex(1).getPriority());

			// command content: have id and have parameters without changes (invalid)
			// id: any valid values
			// priority: valid values from CommandParser
			// no changes to priority
			input = "edit 1 priority high";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 1", viewState.getStatusMessage());

			// command content: have id and have parameters with changes (valid)
			// id: any valid values
			// priority: "none"
			// changes to priority
			input = "edit 1 priority none";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Edited task: edit2", viewState.getStatusMessage());
			assertEquals(Priority.NONE, tasks.getTaskByIndex(1).getPriority());

			// command content: have id and have parameters without changes (invalid)
			// id: any valid values
			// priority: "none"
			// no changes to priority
			input = "edit 1 priority none";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 1", viewState.getStatusMessage());

			// command content: have id and no parameters (invalid)
			// id: any valid values
			input = "edit 1";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No changes made for task 1", viewState.getStatusMessage());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}
	
	/**
	 * Equivalence partition
	 * command content: [""], [invalid command]
	 * 
	 * Boundary case
	 * command content:
	 * "" (valid), invalid command (valid)
	 * 
	 * Note: These cases are considered valid because the commands are tested to see if
	 * invalid commands are passed here. Valid commands are tested in CommandTest.
	 */
	@Test
	public void testCommandInvalid() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testinvalid.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();

		try {
			// command content: "" (valid)
			ViewState currentViewState = CommandController.getInstance().getCurrentViewState();
			String input = "";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals(viewState, currentViewState);
			
			// command content: invalid command (valid)
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

	/**
	 * Equivalence partition
	 * command content: [single id], [multiple id], [duplicate id], ["all"]
	 * id: [1 .. display.getTaskList().size()]
	 * 
	 * Boundary case
	 * command content:
	 * no id (invalid), single id (valid), multiple id (valid), duplicate id (valid),
	 * "all" (valid), any other String (invalid)
	 * 
	 * id:
	 * 0 (invalid), 1 (valid), display.getTaskList().size() (valid), display.getTaskList().size() + 1 (invalid)
	 */
	@Test
	public void testCommandMark() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testMark.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();
		
		try {
			// populate tasklist
			ArrayList<Task> testTasks = new ArrayList<Task>();
			for (int i = 0; i < 3; i++) {
				Task task = new Task(CommandController.getInstance().createCommand("add mark" + (i + 1)));
				testTasks.add(task);
				master.addTask(task);
				display.addTask(task);
			}
			TaskStorage.getInstance().writeTasks(master);
			
			// command content: no id (invalid)
			String input = "mark";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No task specified to mark", viewState.getStatusMessage());

			// command content: single id (valid)
			// id: 0 (invalid)
			input = "mark 0";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());

			// command content: single id (valid)
			// id: display.getTaskList().size() + 1 [4] (invalid)
			input = "mark 4";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());

			// command content: single id (valid)
			// id: 1 (valid)
			input = "mark 1";
			viewState = CommandController.getInstance().executeCommand(input);
			TaskList tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Marked task completed: 1", viewState.getStatusMessage());
			assertTrue(tasks.getTaskByIndex(0).isCompleted());

			// command content: single id (valid)
			// id: display.getTaskList().size() [3] (valid)
			input = "mark 3";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Marked task completed: 3", viewState.getStatusMessage());
			assertTrue(tasks.getTaskByIndex(2).isCompleted());

			// command content: duplicate id (valid)
			// id: any valid value
			input = "mark 1 1";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Marked task uncompleted: 1", viewState.getStatusMessage());
			assertFalse(tasks.getTaskByIndex(0).isCompleted());

			// command content: multiple id (valid)
			// id: any valid value
			input = "mark 1 3";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Marked task completed: 1; Marked task uncompleted: 3", viewState.getStatusMessage());
			assertTrue(tasks.getTaskByIndex(0).isCompleted());
			assertFalse(tasks.getTaskByIndex(2).isCompleted());

			// command content: "all" (valid)
			// id: any valid value
			input = "mark all";
			viewState = CommandController.getInstance().executeCommand(input);
			tasks = TaskStorage.getInstance().readTasks();
			assertEquals("Marked tasks completed: 2, 3; Marked task uncompleted: 1", viewState.getStatusMessage());
			assertFalse(tasks.getTaskByIndex(0).isCompleted());
			assertTrue(tasks.getTaskByIndex(1).isCompleted());
			assertTrue(tasks.getTaskByIndex(2).isCompleted());

			// command content: any other String (invalid)
			// id: any valid value
			input = "mark abc";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Invalid task ID entered", viewState.getStatusMessage());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}

	/**
	 * Equivalence partition
	 * command content: [no log keyword and have location with changes], [have log keyword and have location with changes]
	 * location: ["default"], [valid location]
	 * file: [does not exist]
	 * 
	 * Boundary case
	 * command content:
	 * no log keyword and no location (invalid), have log keyword and no location (invalid),
	 * no log keyword and have location without changes (invalid),
	 * have log keyword and have location without changes (invalid),
	 * no log keyword and have location with changes (valid), have log keyword and have location with changes (valid),
	 * 
	 * location:
	 * "default" (valid), valid location (valid), valid location with spaces (valid)
	 * 
	 * file:
	 * exist (invalid), does not exist (valid)
	 */
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

			// command content: no log keyword and no location (invalid)
			String input = "save";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No storage file location specified", viewState.getStatusMessage());

			// command content: have log keyword and no location (invalid)
			input = "save log";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("No log file location specified", viewState.getStatusMessage());

			// command content: no log keyword and have location without changes (invalid)
			input = "save " + prevStorageFileLocation;
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Same storage file location. No changes to storage file location: "
						 + prevStorageFileLocation, viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// command content: have log keyword and have location without changes (invalid)
			input = "save log " + prevLogFileLocation;
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Same log file location. No changes to log file location: "
						 + prevLogFileLocation, viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// command content: no log keyword and have location with changes (valid)
			// location: valid location (valid)
			// file: does not exist (valid)
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

			// command content: have log keyword and have location with changes (valid)
			// location: valid location (valid)
			// file: does not exist (valid)
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

			// command content: no log keyword and have location with changes (valid)
			// location: valid location with spaces (valid)
			// file: does not exist (valid)
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

			// command content: have log keyword and have location with changes (valid)
			// location: valid location with spaces (valid)
			// file: does not exist (valid)
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

			// command content: no log keyword and have location with changes (valid)
			// location: "default" (valid)
			// file: does not exist (valid)
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

			// command content: have log keyword and have location with changes (valid)
			// location: "default" (valid)
			// file: does not exist (valid)
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

			// command content: no log keyword and have location without changes (invalid)
			// location: "default" (valid)
			// file: does not exist (valid)
			input = "save default";
			viewState = CommandController.getInstance().executeCommand(input);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			assertEquals("Same storage file location. No changes to storage file location: " + prevStorageFileLocation,
					viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// command content: have log keyword and have location without changes (invalid)
			// location: "default" (valid)
			// file: does not exist (valid)
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

			// command content: no log keyword and have location with changes (valid)
			// location: any valid values
			// file: exist (invalid)
			input = "save test";
			viewState = CommandController.getInstance().executeCommand(input);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			assertEquals("File already exists in specified location: test", viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());

			// command content: have log keyword and have location with changes (valid)
			// location: any valid values
			// file: exist (invalid)
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

	/**
	 * Equivalence partition
	 * command content: [have parameters]
	 * 
	 * Boundary case
	 * command content:
	 * no parameters (invalid), have parameters (valid)
	 * 
	 * name:
	 * whole word match (valid), partial word match (valid),
	 * multiple whole word match (valid), multiple partial word match
	 * combination of partial and whole word match
	 * 
	 * date:
	 * "between [startDate] and [endDate]" (valid), "after [date]" (valid), "before [date]" (valid) 
	 * 
	 * Note: parameters include name, startDate, endDate, priority and type. Invalid
	 * values for date and priority are tested in DateParserTest, CommandParserTest and PredicatesTest.
	 */
	@Test
	public void testCommandSearch() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/testsearch.txt");
		File testFile = createTestFile();
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList display = CommandController.getInstance().getCurrentViewState().getTaskList();
		master.getTaskList().clear();
		display.getTaskList().clear();
		
		try {
			// populate tasklist
			ArrayList<Task> testTasks = new ArrayList<Task>();
			Task task = new Task(CommandController.getInstance().createCommand("add CS2103 tutorial"));
			task.setStartDate(LocalDateTime.of(2001, 1, 1, 13, 0));
			task.setEndDate(LocalDateTime.of(2003, 3, 3, 15, 0));
			task.setPriority(Priority.HIGH);
			testTasks.add(task);
			master.addTask(task);
			display.addTask(task);
			task = new Task(CommandController.getInstance().createCommand("add CS2103 lecture"));
			task.setEndDate(LocalDateTime.of(2002, 2, 2, 14, 0));
			testTasks.add(task);
			master.addTask(task);
			display.addTask(task);
			task = new Task(CommandController.getInstance().createCommand("add CS2010 lecture"));
			task.setCompleted(true);
			testTasks.add(task);
			master.addTask(task);
			display.addTask(task);
			TaskStorage.getInstance().writeTasks(master);
/*
			// command content: no parameters (invalid)
			String input = "search";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("", viewState.getStatusMessage());
*/
			// command content: have parameters (valid)
			// name: whole word match (valid)
			String input = "search lecture";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("1 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 lecture", viewState.getTaskList().getTaskByIndex(0).getName());

			// command content: have parameters (valid)
			// name: partial word match (valid)
			input = "search cs";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("2 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 tutorial", viewState.getTaskList().getTaskByIndex(0).getName());
			assertEquals("CS2103 lecture", viewState.getTaskList().getTaskByIndex(1).getName());

			// command content: have parameters (valid)
			// name: multiple whole word match (valid)
			input = "search cs2103 tutorial";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("1 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 tutorial", viewState.getTaskList().getTaskByIndex(0).getName());

			// command content: have parameters (valid)
			// date: "between [startDate] and [endDate]" (valid)
			input = "search between 01/01/01 and 03/03/03";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("2 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 tutorial", viewState.getTaskList().getTaskByIndex(0).getName());
			assertEquals("CS2103 lecture", viewState.getTaskList().getTaskByIndex(1).getName());

			// command content: have parameters (valid)
			// date: "between [startDate] and [endDate]" (valid)
			input = "search between 01/01/01 and 03/03/03";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("2 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 tutorial", viewState.getTaskList().getTaskByIndex(0).getName());
			assertEquals("CS2103 lecture", viewState.getTaskList().getTaskByIndex(1).getName());

			// command content: have parameters (valid)
			// date: "after [date]" (valid)
			input = "search after 02/02/02";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("2 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 tutorial", viewState.getTaskList().getTaskByIndex(0).getName());
			assertEquals("CS2103 lecture", viewState.getTaskList().getTaskByIndex(1).getName());

			// command content: have parameters (valid)
			// date: "before [date]" (valid)
			input = "search before 02/02/02";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("1 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 lecture", viewState.getTaskList().getTaskByIndex(0).getName());

			// command content: have parameters (valid)
			// priority
			input = "search priority high";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("1 match(es)", viewState.getStatusMessage());
			assertEquals("CS2103 tutorial", viewState.getTaskList().getTaskByIndex(0).getName());

			// command content: have parameters (valid)
			// type
			input = "search type completed";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("1 match(es)", viewState.getStatusMessage());
			assertEquals("CS2010 lecture", viewState.getTaskList().getTaskByIndex(0).getName());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}
	}

	/**
	 * Equivalence partition
	 * command content: ["light"], ["dark"]
	 * 
	 * Boundary case
	 * command content:
	 * "" (invalid), "light" (valid), "dark" (valid), any other String (invalid)
	 */
	@Test
	public void testCommandTheme() throws Exception {
		String prevSelectedTheme = AppStorage.getInstance().getSelectedTheme();

		try {
			// command content: "" (invalid)
			String input = "theme";
			ViewState viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Available themes: light, dark", viewState.getStatusMessage());

			// command content: "light" (valid)
			input = "theme light";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Current theme: light", viewState.getStatusMessage());
			assertTrue("light", viewState.getTheme().endsWith("theme_light.css"));
			assertEquals("light", AppStorage.getInstance().getSelectedTheme());

			// command content: "dark" (valid)
			input = "theme dark";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Current theme: dark", viewState.getStatusMessage());
			assertTrue("dark", viewState.getTheme().endsWith("theme_dark.css"));
			assertEquals("dark", AppStorage.getInstance().getSelectedTheme());

			// command content: any other String (invalid)
			input = "theme abc";
			viewState = CommandController.getInstance().executeCommand(input);
			assertEquals("Available themes: light, dark", viewState.getStatusMessage());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			AppStorage.getInstance().setSelectedTheme(prevSelectedTheme);
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
