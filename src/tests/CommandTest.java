package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.Test;

import app.logic.CommandController;
import app.logic.command.Command;
import app.model.ViewState;
import app.storage.AppStorage;

public class CommandTest {

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
			Command cmd = CommandController.getInstance().createCommand(input);
			ViewState viewState = cmd.execute(null);
			File currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			assertEquals("No storage file location specified", viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertTrue(prevStorageFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));

			// no log file location
			List<String> prevLogFileLines = Files.readAllLines(prevLogFile.toPath());
			input = "save log";
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			File currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			List<String> currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("No log file location specified", viewState.getStatusMessage());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevLogFile.exists());
			assertTrue(prevLogFile.equals(currLogFile));

			// no changes to storage file location
			input = "save " + prevStorageFileLocation;
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			assertEquals("Same storage file location. No changes to storage file location: "
						 + prevStorageFileLocation, viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertTrue(prevStorageFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));

			// no changes to log file location
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());
			input = "save log " + prevLogFileLocation;
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("Same log file location. No changes to log file location: "
						 + prevLogFileLocation, viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevLogFile.exists());
			assertTrue(prevLogFile.equals(currLogFile));

			// storage file location without file extension
			input = "save testsave/storage";
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
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
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());
			input = "save log testsave/log";
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
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
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
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
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
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
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
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
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
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
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
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
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
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
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			assertEquals("Same storage file location. No changes to storage file location: " + prevStorageFileLocation,
					viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertTrue(prevStorageFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));

			// no changes for default log file location
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());
			input = "save log default";
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("Same log file location. No changes to log file location: " + prevLogFileLocation,
					viewState.getStatusMessage());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevLogFile.exists());
			assertTrue(prevLogFile.equals(currLogFile));

			input = "save " + userStorageFileLocation;
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);

			input = "save log " + userLogFileLocation;
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
