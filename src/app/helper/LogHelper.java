package app.helper;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogHelper {
	public static Logger logger;

	public static void initializeLogger() {
		logger = Logger.getLogger(LogHelper.class.getName());
		try {
			File logDir = new File("./logs");
			logDir.mkdir();
			FileHandler fileHandler = new FileHandler("./logs/next.log", true);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Logger getLogger() {
		if (logger == null) {
			initializeLogger();
		}
		return logger;
	}

}
