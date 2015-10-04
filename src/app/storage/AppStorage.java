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

	public void setSaveLocation(String saveLocation) {
		properties.setProperty(StorageConstants.PROPERTIES_SAVE_LOCATION, saveLocation);

		writeProperties();
	}

	public String getLogFileLocation() {
		return properties.getProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION);
	}

	public void setLogFileLocation(String logFileLocation) {
		properties.setProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION, logFileLocation);

		writeProperties();
	}

	public String getSelectedTheme() {
		return properties.getProperty(StorageConstants.PROPERTIES_SELECTED_THEME);
	}

	public void setSelectedTheme(String selectedTheme) {
		properties.setProperty(StorageConstants.PROPERTIES_SELECTED_THEME, selectedTheme);

		writeProperties();
	}

	public void setDefaultProperties() {
		properties.setProperty(StorageConstants.PROPERTIES_SAVE_LOCATION, "");
		properties.setProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION, "");
		properties.setProperty(StorageConstants.PROPERTIES_SELECTED_THEME, ViewConstants.THEME_LIGHT);

		writeProperties();
	}

	private void writeProperties() {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
			properties.store(bufferedWriter, null);
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
}
