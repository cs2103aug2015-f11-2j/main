package app.logic.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import app.constants.CommandConstants.CommandType;
import app.constants.StorageConstants;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.model.ViewState;
import app.storage.AppStorage;
import app.util.Common;
import app.util.LogHelper;

public class CommandSave extends Command {
	private boolean isLog;

	public CommandSave() {
		super();
		this.setCommandType(CommandType.SAVE);

		isLog = false;
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		ViewState viewState = new ViewState();

		try {
			if (this.getContent().isEmpty()) {
				String errorMsg = String.format(ViewConstants.ERROR_SAVE_NO_LOCATION, ViewConstants.SAVE_STORAGE);
				viewState.setStatus(StatusType.ERROR, errorMsg);
				LogHelper.getInstance().getLogger().info(errorMsg);

				return viewState;
			}

			if (Common.getFirstWord(this.getContent()).equalsIgnoreCase(ViewConstants.SAVE_LOG)) {
				isLog = true;
				this.setContent(Common.removeFirstWord(this.getContent()));
			}

			if (this.getContent().isEmpty()) {
				String errorMsg = String.format(ViewConstants.ERROR_SAVE_NO_LOCATION, ViewConstants.SAVE_LOG);
				viewState.setStatus(StatusType.ERROR, errorMsg);
				LogHelper.getInstance().getLogger().info(errorMsg);

				return viewState;
			}

			String prevFileLocation = (isLog) ? AppStorage.getInstance().getLogFileLocation()
					: AppStorage.getInstance().getStorageFileLocation();

			if (this.getContent().equals(prevFileLocation)) {
				String errorMsg = String.format(ViewConstants.ERROR_SAVE_NO_CHANGES,
						(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, prevFileLocation);
				viewState.setStatus(StatusType.ERROR, errorMsg);
				LogHelper.getInstance().getLogger().info(errorMsg);

				return viewState;
			}

			String errorMsg = changeFileLocation(prevFileLocation, this.getContent());

			if (errorMsg == null) {
				removeFileAndParentsIfEmpty(prevFileLocation);

				String successMsg = String.format(ViewConstants.MESSAGE_SAVE,
						(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE,
						(isLog) ? AppStorage.getInstance().getLogFileLocation()
								: AppStorage.getInstance().getStorageFileLocation());
				viewState.setStatus(successMsg);
				LogHelper.getInstance().getLogger().info(successMsg);
				setExecuted(true);
			} else {
				viewState.setStatus(StatusType.ERROR, errorMsg);
				LogHelper.getInstance().getLogger().info(errorMsg);
			}

			return viewState;
		} catch (Exception e) {
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_SAVE, this.getContent()));
			LogHelper.getInstance().getLogger().severe(e.getMessage());
		}

		return viewState;
	}

	/**
	 * Change the storage/log file location in the configuration file after
	 * successfully copying of the storage/log file to the new file location.
	 * 
	 * @param prevFileLocation 	Previous storage/log file location.
	 * @param newFileLocation 	New storage/log file location.
	 * @return 					Error messages encountered when copying the file.
	 */
	private String changeFileLocation(String prevFileLocation, String newFileLocation) {
		String errorMsg;

		if (newFileLocation.equalsIgnoreCase(ViewConstants.SAVE_DEFAULT)) {
			if (isLog) {
				errorMsg = copyFile(prevFileLocation, StorageConstants.FILE_DEFAULT_LOG);

				if (errorMsg == null) {
					AppStorage.getInstance().setToDefaultLogFileLocation();
				}
			} else {
				errorMsg = copyFile(prevFileLocation, StorageConstants.FILE_DEFAULT_STORAGE);

				if (errorMsg == null) {
					AppStorage.getInstance().setToDefaultStorageFileLocation();
				}
			}
		} else {
			errorMsg = copyFile(prevFileLocation, newFileLocation);

			if (errorMsg == null) {
				if (isLog) {
					AppStorage.getInstance().setLogFileLocation(newFileLocation);
				} else {
					AppStorage.getInstance().setStorageFileLocation(newFileLocation);
				}
			}
		}

		return errorMsg;
	}

	/**
	 * Copy a file from source to destination.
	 * 
	 * @param sourceLocation 	Source file path.
	 * @param destLocation 		Destination file path.
	 * @return 					Error messages that occurred when copying the file.
	 * 							Returns null if it successfully copied the file.
	 */
	private String copyFile(String sourceLocation, String destLocation) {
		String errorMsg = null;
		File sourceFile = new File(sourceLocation);
		File destFile = new File(destLocation);

		if (destFile.getParentFile() != null) {
			destFile.getParentFile().mkdirs();
		}

		try {
			Files.copy(sourceFile.toPath(), destFile.toPath());
		} catch (FileAlreadyExistsException e) {
			errorMsg = String.format(ViewConstants.ERROR_SAVE_FILE_ALREADY_EXISTS, this.getContent());
		} catch (IOException e) {
			errorMsg = String.format(ViewConstants.ERROR_SAVE_COPY_FILE,
					(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, this.getContent());
		}

		return errorMsg;
	}

	private void removeFileAndParentsIfEmpty(String pathStr) throws IOException {
		Path path = new File(pathStr).toPath();
		File baseFile = new File(".");

		if (path == null || path.equals(baseFile.getCanonicalFile().toPath())) {
			return;
		}

		if (Files.isRegularFile(path)) {
			Files.deleteIfExists(path);
		} else if (Files.isDirectory(path)) {
			try {
				Files.delete(path);
			} catch (DirectoryNotEmptyException e) {
				return;
			}
		}

		removeFileAndParentsIfEmpty(path.getParent().toString());
	}
}
