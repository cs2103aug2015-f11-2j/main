package tests.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import app.Main;
import app.model.TaskList;
import app.storage.AppStorage;
import app.storage.TaskStorage;

public class AddTasksIntegrationTest {

	@Test
	public void testAddTasks() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/integration1.txt");
		createTestFile();
		
		String[] commands = {
			"add first task", 
			"add second task",
			"exit"
		};
		
		try {
			Main.main(commands);
			// TODO: assertions here to ensure commands are successful.
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(AppStorage.getInstance().getStorageFileLocation());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
		}

	}

	private void createTestFile() {
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
	}

	private void removeFileAndParentsIfEmpty(String pathStr) throws IOException {
		if (pathStr == null) {
			return;
		}

		File file = new File(pathStr);
		Path path = file.toPath();

		if (Files.isRegularFile(path)) {
			Files.deleteIfExists(path);
		} else if (Files.isDirectory(path)) {
			if (file.list().length != 0) {
				return;
			}

			try {
				Files.delete(path);
			} catch (DirectoryNotEmptyException e) {
				return;
			}
		}

		removeFileAndParentsIfEmpty(path.getParent().toString());
	}
}
