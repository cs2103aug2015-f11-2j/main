package app.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import app.constants.StorageConstants;
import app.constants.ViewConstants;
import app.util.LogHelper;
import app.util.Observer;

public class AppStorage {
	private static AppStorage appStorage;

	private File configFile;
	private Properties properties;
	private ArrayList<Observer> observerList;

	private AppStorage() {
		configFile = new File(StorageConstants.FILE_CONFIGURATION);
		properties = new Properties();
		observerList = new ArrayList<Observer>();

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();

				setToDefaultStorageFileLocation();
				setToDefaultLogFileLocation();
				setDefaultSelectedTheme();
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
			appStorage.addObservers();
		}

		return appStorage;
	}

	public String getStorageFileLocation() {
		return properties.getProperty(StorageConstants.PROPERTIES_STORAGE_FILE_LOCATION);
	}

	public void setStorageFileLocation(String path) {
		properties.setProperty(StorageConstants.PROPERTIES_STORAGE_FILE_LOCATION,
							   toValidCanonicalPath(path));
		writeProperties();

		if (!observerList.isEmpty()) {
			notifyObserver(StorageConstants.PARAM_POSITION_STORAGE);
		}
	}

	public void setToDefaultStorageFileLocation() {
		setStorageFileLocation(StorageConstants.FILE_DEFAULT_STORAGE);
	}

	public String getLogFileLocation() {
		return properties.getProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION);
	}

	public void setLogFileLocation(String path) {
		properties.setProperty(StorageConstants.PROPERTIES_LOG_FILE_LOCATION,
							   toValidCanonicalPath(path));
		writeProperties();

		if (!observerList.isEmpty()) {
			notifyObserver(StorageConstants.PARAM_POSITION_LOG);
		}
	}

	public void setToDefaultLogFileLocation() {
		setLogFileLocation(StorageConstants.FILE_DEFAULT_LOG);
	}

	public String getSelectedTheme() {
		return properties.getProperty(StorageConstants.PROPERTIES_SELECTED_THEME);
	}

	public void setSelectedTheme(String theme) {
		assert(theme == ViewConstants.THEME_LIGHT || theme == ViewConstants.THEME_DARK);

		properties.setProperty(StorageConstants.PROPERTIES_SELECTED_THEME, theme);
		writeProperties();
	}

	public void setDefaultSelectedTheme() {
		setSelectedTheme(ViewConstants.THEME_LIGHT);
	}

	private void writeProperties() {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
			bufferedWriter.write(StorageConstants.PROPERTIES_STORAGE_FILE_LOCATION + "="
								 + getStorageFileLocation());
			bufferedWriter.newLine();
			bufferedWriter.write(StorageConstants.PROPERTIES_LOG_FILE_LOCATION + "="
								 + getLogFileLocation());
			bufferedWriter.newLine();
			bufferedWriter.write(StorageConstants.PROPERTIES_SELECTED_THEME + "="
								 + getSelectedTheme());
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(StorageConstants.ERROR_WRITE_PROPERTIES);
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
			LogHelper.getInstance().getLogger().severe(StorageConstants.ERROR_READ_PROPERTIES);
		}
	}

	private String toCanonicalPath(String path) {
		File file = new File(path);
		String canonicalPath = path;

		try {
			canonicalPath = file.getCanonicalPath();
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(StorageConstants.ERROR_TO_CANONICAL_PATH);
		}

		return canonicalPath;
	}

	/**
	 * Convert path to a valid canonical path by removing whitespace before slashes and
	 * replace backslashes to forward slashes. This is used to avoid using escape
	 * characters in the configuration file.
	 * 
	 * Note: The path returned may not be valid since the file system does not allow
	 * certain characters and combinations.
	 * 
	 * @param path 			File path.
	 * @return 				Valid file path with backslash replaced with forward
	 * 						slash and removed whitespace before slash.
	 */
	public String toValidCanonicalPath(String path) {
		String validPath = toCanonicalPath(path).replace("\\", "/").replaceAll("\\s*/\\s*", "/");

		return validPath;
	}

	private void addObservers() {
		observerList.add(TaskStorage.getInstance());
		observerList.add(LogHelper.getInstance());
	}

	private void notifyObserver(int i) {
		observerList.get(i).update();
	}
}
