package app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogHelper {

	private static final String LOG_DIR = "./logs";
	private static final String LOG_FILE = "next.log";

	private static Logger logger;

	/**
	 * Initializes the Logger.
	 */
	private static void initializeLogger() {
		logger = Logger.getLogger(LogHelper.class.getName());
		try {
			File logDir = new File(LOG_DIR);
			logDir.mkdir();
			String path = Paths.get(LOG_DIR, LOG_FILE).toString();
			FileHandler fileHandler = new FileHandler(path, true);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return The Logger instance
	 */
	public static Logger getLogger() {
		if (logger == null) {
			initializeLogger();
		}
		return logger;
	}

}
