package tests.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import app.Main;
import app.constants.TaskConstants.Priority;
import app.model.Task;
import app.model.TaskList;
import app.storage.AppStorage;
import app.storage.TaskStorage;

public class IntegrationTest {

	@Test
	public void testAddTasks() throws Exception {
		String prevStorageLocation = AppStorage.getInstance().getStorageFileLocation();
		AppStorage.getInstance().setStorageFileLocation("testStorage/integration1.txt");
		File testFile = createTestFile();
		
		String[] commands = {
			"add 1st task", 
			"add 2nd task",
			"add 3rd task",
			"add 4th task",
			"edit 1 priority high",
			"mark 2 3",
			"display completed",
			"delete 1",
			"exit"
		};
		
		/* Final result:
		 * 1st task (priority high)
		 * 3rd task (completed)
		 * 4th task
		 */
		try {
			Main.main(commands);
			List<Task> tasks = TaskStorage.getInstance().readTasks().getTaskList();
			assertEquals(3, tasks.size());
			assertEquals(Priority.HIGH, tasks.get(0).getPriority());
			assertTrue(tasks.get(1).isCompleted());
			assertEquals("4th task", tasks.get(2).getName());
		} catch (Exception e) {
			throw e; // JUnit will handle this and report a failed assertion
		} finally {
			removeFileAndParentsIfEmpty(testFile.toPath());
			AppStorage.getInstance().setStorageFileLocation(prevStorageLocation);
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
