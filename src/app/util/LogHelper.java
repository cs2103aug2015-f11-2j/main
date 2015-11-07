package app.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import app.storage.AppStorage;

//@@author A0125960E
public class LogHelper extends Observer {

	private static LogHelper logHelper;
	private Logger logger;

	/**
	 * Initializes the Logger.
	 */
	private LogHelper() {
		logger = Logger.getLogger(LogHelper.class.getName());

		update();
	}

	/**
	 * @return The LogHelper instance
	 */
	public static LogHelper getInstance() {
		if (logHelper == null) {
			logHelper = new LogHelper();
		}
		return logHelper;
	}

	/**
	 * @return The Logger instance
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Updates the log file location.
	 */
	@Override
	public void update() {
		File logFile = new File(AppStorage.getInstance().getLogFileLocation());
		logFile.getParentFile().mkdirs();

		try {
			FileHandler fileHandler = new FileHandler(AppStorage.getInstance().getLogFileLocation(), true);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);

			while (logger.getHandlers().length > 0) {
				FileHandler prevFileHandler = (FileHandler)logger.getHandlers()[0];
				logger.removeHandler(prevFileHandler);
				prevFileHandler.close();
			}

			logger.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
