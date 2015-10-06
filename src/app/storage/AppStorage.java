package app.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import app.constants.StorageConstants;
import app.constants.ViewConstants;

public class AppStorage {
	private static AppStorage appStorage;

	private File configFile;
	private Properties properties;

	private AppStorage() {
		configFile = new File(StorageConstants.FILE_CONFIG_PROPERTIES);
		properties = new Properties();

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();

				setDefaultProperties();
			} else {
				readProperties();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static AppStorage getInstance() {
		if (appStorage == null) {
			appStorage = new AppStorage();
		}

		return appStorage;
	}

	public String getSaveLocation() {
		return properties.getProperty(StorageConstants.PROPERTIES_SAVE_LOCATION);
	}

	public void setSaveLocation(String directoryPath) {
		properties.setProperty(StorageConstants.PROPERTIES_SAVE_LOCATION, replaceBackslash(directoryPath));

		writeProperties();
	}

	public String getLogFileLocation() {
		return properties.getProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION);
	}

	public void setLogFileLocation(String directoryPath) {
		properties.setProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION, replaceBackslash(directoryPath));

		writeProperties();
	}

	public String getSelectedTheme() {
		return properties.getProperty(StorageConstants.PROPERTIES_SELECTED_THEME);
	}

	public void setSelectedTheme(String theme) {
		properties.setProperty(StorageConstants.PROPERTIES_SELECTED_THEME, theme);

		writeProperties();
	}

	public void setDefaultProperties() {
		String currentWorkingDirectoryPath = "";
		File currentWorkingDirectory = new File(".");

		try {
			currentWorkingDirectoryPath = replaceBackslash(currentWorkingDirectory.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		setSaveLocation(currentWorkingDirectoryPath);
		setLogFileLocation(currentWorkingDirectoryPath);
		setSelectedTheme(ViewConstants.THEME_LIGHT);
	}

	private void writeProperties() {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
			bufferedWriter.write(StorageConstants.PROPERTIES_SAVE_LOCATION + "=" + getSaveLocation());
			bufferedWriter.newLine();
			bufferedWriter.write(StorageConstants.PROPERTIES_LOG_FILE_LOCATION + "=" + getLogFileLocation());
			bufferedWriter.newLine();
			bufferedWriter.write(StorageConstants.PROPERTIES_SELECTED_THEME + "=" + getSelectedTheme());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readProperties() {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile))) {
			properties.load(bufferedReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Replace backslashes from file/directory path to forward slashes. This
	 * method is used to avoid using escape characters in the configuration
	 * file.
	 * 
	 * @param path			File/directory path
	 * @return replacedPath	File/directory path with backslashes replaced with
	 *         				forward slashes
	 */
	private String replaceBackslash(String path) {
		String replacedPath = path.replace("\\", "/");

		return replacedPath;
	}
}
