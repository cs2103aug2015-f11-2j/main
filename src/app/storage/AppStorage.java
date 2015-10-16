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
import app.util.LogHelper;

public class AppStorage {
	private static AppStorage appStorage;

	private File configFile;
	private Properties properties;

	private AppStorage() {
		configFile = new File(StorageConstants.FILE_CONFIGURATION);
		properties = new Properties();

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();

				setDefaultStorageFileLocation();
				setDefaultLogFileLocation();
				setDefaultSelectedTheme();
			} else {
				readProperties();
			}
		} catch (IOException e) {
			LogHelper.getLogger().severe(StorageConstants.ERROR_INITIALIZE_APPSTORAGE);
		}
	}

	public static AppStorage getInstance() {
		if (appStorage == null) {
			appStorage = new AppStorage();
		}

		return appStorage;
	}

	public String getStorageFileLocation() {
		return properties.getProperty(StorageConstants.PROPERTIES_STORAGE_FILE_LOCATION);
	}

	public void setStorageFileLocation(String path) {
		properties.setProperty(StorageConstants.PROPERTIES_STORAGE_FILE_LOCATION,
							   replaceBackslash(toCanonicalPath(path)));

		writeProperties();
	}

	public void setDefaultStorageFileLocation() {
		setStorageFileLocation(StorageConstants.FILE_DEFAULT_STORAGE);
	}

	public String getLogFileLocation() {
		return properties.getProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION);
	}

	public void setLogFileLocation(String path) {
		properties.setProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION,
							   replaceBackslash(toCanonicalPath(path)));

		writeProperties();
	}

	public void setDefaultLogFileLocation() {
		setLogFileLocation(StorageConstants.FILE_DEFAULT_LOG);
	}

	public String getSelectedTheme() {
		return properties.getProperty(StorageConstants.PROPERTIES_SELECTED_THEME);
	}

	public void setSelectedTheme(String theme) {
		assert (theme == ViewConstants.THEME_LIGHT || theme == ViewConstants.THEME_DARK);

		properties.setProperty(StorageConstants.PROPERTIES_SELECTED_THEME, theme);

		writeProperties();
	}

	public void setDefaultSelectedTheme() {
		setSelectedTheme(ViewConstants.THEME_LIGHT);
	}

	private void writeProperties() {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
			bufferedWriter.write(StorageConstants.PROPERTIES_STORAGE_FILE_LOCATION
								 + "=" + getStorageFileLocation());
			bufferedWriter.newLine();
			bufferedWriter.write(StorageConstants.PROPERTIES_LOG_FILE_LOCATION
								 + "=" + getLogFileLocation());
			bufferedWriter.newLine();
			bufferedWriter.write(StorageConstants.PROPERTIES_SELECTED_THEME
								 + "=" + getSelectedTheme());
		} catch (IOException e) {
			LogHelper.getLogger().severe(StorageConstants.ERROR_WRITE_PROPERTIES);
		}
	}

	/**
	 * Read properties from the file. If the selected theme is invalid, the
	 * selected theme will be set to default.
	 */
	private void readProperties() {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile))) {
			properties.load(bufferedReader);
		} catch (IOException e) {
			LogHelper.getLogger().severe(StorageConstants.ERROR_READ_PROPERTIES);
		}
	}

	private String toCanonicalPath(String path) {
		File file = new File(path);
		String canonicalPath = "";

		try {
			canonicalPath = file.getCanonicalPath();
		} catch (IOException e) {
			LogHelper.getLogger().severe(StorageConstants.ERROR_TO_CANONICAL_PATH);
		}

		return canonicalPath;
	}

	/**
	 * Replace backslashes from file path to forward slashes. This method is
	 * used to avoid using escape characters in the configuration file.
	 * 
	 * @param path 			File path
	 * @return replacedPath File path with backslashes replaced with forward
	 *         				slashes
	 */
	private String replaceBackslash(String path) {
		String replacedPath = path.replace("\\", "/");

		return replacedPath;
	}
}
