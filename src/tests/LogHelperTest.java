package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import app.storage.AppStorage;
import app.util.LogHelper;

public class LogHelperTest {
	// @@author A0125360R
	@Test
	public void testLogHelper() {		
		// check if file exist before logging
		File file = new File("testlog/next.log");
		assertFalse(file.exists());
		
		// Check if file exist after logging
		String userLogFileLocation = AppStorage.getInstance().getLogFileLocation();
		AppStorage.getInstance().setLogFileLocation("testLog/next.log");
		LogHelper.getInstance().getLogger().info("A log message");
		LogHelper.getInstance().getLogger().severe("A severe log message");
		assertTrue(file.exists());
		
		// Check if logged content exist in log file
		assertTrue(isLineInFile("INFO: A log message", "testLog/next.log"));
		assertTrue(isLineInFile("SEVERE: A severe log message", "testLog/next.log"));
		
		// Set back original log location and delete temp log files
		AppStorage.getInstance().setLogFileLocation(userLogFileLocation);
		file.delete();
		file = new File("testlog");
		file.delete();
	}
	
	private boolean isLineInFile(String text, String filePath) {
		boolean exist = false;
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while((line = br.readLine()) != null){
				if(line.equals(text)){
					exist = true;
			        break;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exist;
	}
}
