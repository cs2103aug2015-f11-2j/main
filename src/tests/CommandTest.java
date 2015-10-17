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
			List<String> prevLogFileLines = Files.readAllLines(prevLogFile.toPath());

			// no storage file location
			String input = "save";
			Command cmd = CommandController.getInstance().createCommand(input);
			ViewState viewState = cmd.execute(null);
			File currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			File currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			List<String> currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("No storage file location specified", viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevStorageFile.exists());
			assertTrue(prevLogFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));
			assertTrue(prevLogFile.equals(currLogFile));
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());

			// no log file location
			input = "save log";
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("No log file location specified", viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevStorageFile.exists());
			assertTrue(prevLogFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));
			assertTrue(prevLogFile.equals(currLogFile));
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());

			// no changes to storage file location
			input = "save " + prevStorageFileLocation;
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("Same storage file location. No changes to storage file location: " + prevStorageFileLocation,
						 viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevStorageFile.exists());
			assertTrue(prevLogFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));
			assertTrue(prevLogFile.equals(currLogFile));
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());

			// no changes to log file location
			input = "save log " + prevLogFileLocation;
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			assertEquals("Same log file location. No changes to log file location: " + prevLogFileLocation,
						 viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevStorageFile.exists());
			assertTrue(prevLogFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));
			assertTrue(prevLogFile.equals(currLogFile));
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());

			// storage file location without file extension
			input = "save testsave/storage";
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			String parentLocation = prevStorageFileLocation.replace("next.txt", "");
			assertEquals("Saved storage file location: " + parentLocation + "testsave/storage",
						 viewState.getStatusMessage());
			assertEquals(parentLocation + "testsave/storage", AppStorage.getInstance().getStorageFileLocation());
			assertEquals(prevLogFileLocation, AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertEquals(prevLogFileLines, currLogFileLines);
			assertFalse(prevStorageFile.exists());
			assertTrue(prevLogFile.exists());
			assertFalse(prevStorageFile.equals(currStorageFile));
			assertTrue(prevLogFile.equals(currLogFile));
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());

			prevStorageFileLocation = AppStorage.getInstance().getStorageFileLocation();
			prevStorageFile = new File(prevStorageFileLocation);

			// log file location without file extension
			input = "save log testsave/log";
			cmd = CommandController.getInstance().createCommand(input);
			viewState = cmd.execute(null);
			currStorageFile = new File(AppStorage.getInstance().getStorageFileLocation());
			currLogFile = new File(AppStorage.getInstance().getLogFileLocation());
			currLogFileLines = Files.readAllLines(currLogFile.toPath());
			for (int i = currLogFileLines.size(); i > prevLogFileLines.size(); i--) {
				currLogFileLines.remove(i - 1);
			}
			parentLocation = prevLogFileLocation.replace("logs/next.log", "");
			assertEquals("Saved log file location: " + parentLocation + "testsave/log", viewState.getStatusMessage());
			assertEquals(prevStorageFileLocation, AppStorage.getInstance().getStorageFileLocation());
			assertEquals(parentLocation + "testsave/log", AppStorage.getInstance().getLogFileLocation());
			assertEquals(prevStorageFileLines, Files.readAllLines(currStorageFile.toPath()));
			assertEquals(prevLogFileLines, currLogFileLines);
			assertTrue(prevStorageFile.exists());
			assertFalse(prevLogFile.exists());
			assertTrue(prevStorageFile.equals(currStorageFile));
			assertFalse(prevLogFile.equals(currLogFile));
			prevLogFileLines = Files.readAllLines(currLogFile.toPath());

			prevLogFileLocation = AppStorage.getInstance().getLogFileLocation();
			prevLogFile = new File(prevLogFileLocation);

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
