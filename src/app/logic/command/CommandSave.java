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
	private String prevFileLocation;

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

			prevFileLocation = (isLog) ? AppStorage.getInstance().getLogFileLocation()
									   : AppStorage.getInstance().getStorageFileLocation();

			String errorMsg = changeFileLocation(prevFileLocation, this.getContent());

			if (errorMsg == null) {
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
		} catch (Exception e) {
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_SAVE, this.getContent()));
			LogHelper.getInstance().getLogger().severe(e.getMessage());
		}

		return viewState;
	}

	public ViewState undo(ViewState previousViewState) {
		ViewState viewState = new ViewState();

		try {
			String errorMsg = changeFileLocation(this.getContent(), prevFileLocation);

			if (errorMsg == null) {
				String successMsg = String.format(ViewConstants.MESSAGE_SAVE,
						(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE,
						(isLog) ? AppStorage.getInstance().getLogFileLocation()
								: AppStorage.getInstance().getStorageFileLocation());
				viewState.setStatus(ViewConstants.MESSAGE_UNDO + successMsg);
				LogHelper.getInstance().getLogger().info(ViewConstants.MESSAGE_UNDO + successMsg);
				setExecuted(false);
			} else {
				viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_UNDO + errorMsg);
				LogHelper.getInstance().getLogger().info(ViewConstants.ERROR_UNDO + errorMsg);
			}
		} catch (Exception e) {
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_UNDO + ViewConstants.ERROR_SAVE, this.getContent()));
			LogHelper.getInstance().getLogger().severe(e.getMessage());
		}

		return viewState;
	}

	/**
	 * Change the storage/log file location in the configuration file after
	 * successfully copying the storage/log file to the new file location and deleting
	 * the previous storage/log file.
	 * 
	 * @param prevPath 	Previous storage/log file location.
	 * @param newPath 	New storage/log file location.
	 * @return 			Error messages encountered when copying the file.
	 */
	private String changeFileLocation(String prevPath, String newPath) {
		String errorMsg;

		if (newPath.equalsIgnoreCase(ViewConstants.SAVE_DEFAULT)) {
			if (isLog) {
				errorMsg = copyFile(prevPath, StorageConstants.FILE_DEFAULT_LOG);

				if (errorMsg == null) {
					AppStorage.getInstance().setToDefaultLogFileLocation();
				}
			} else {
				errorMsg = copyFile(prevPath, StorageConstants.FILE_DEFAULT_STORAGE);

				if (errorMsg == null) {
					AppStorage.getInstance().setToDefaultStorageFileLocation();
				}
			}
		} else {
			errorMsg = copyFile(prevPath, newPath);

			if (errorMsg == null) {
				if (isLog) {
					AppStorage.getInstance().setLogFileLocation(newPath);
				} else {
					AppStorage.getInstance().setStorageFileLocation(newPath);
				}
			}
		}

		if (errorMsg == null) {
			File prevFile = new File(prevPath);

			try {
				removeFileAndParentsIfEmpty(prevFile.toPath());
			} catch (IOException e) {
				errorMsg = String.format(ViewConstants.ERROR_SAVE_DELETE_FILE,
						(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, prevPath);
			}
		}

		return errorMsg;
	}

	/**
	 * Copy a file from source to destination.
	 * 
	 * @param sourcePath 	Source file path.
	 * @param destPath 		Destination file path.
	 * @return 				Error messages that occurred when copying the file.
	 * 						Returns null if it successfully copied the file.
	 */
	private String copyFile(String sourcePath, String destPath) {
		String errorMsg = null;
		File sourceFile = new File(sourcePath);
		File destFile = new File(AppStorage.getInstance().toValidCanonicalPath(destPath));

		if (destFile.getParentFile() != null) {
			destFile.getParentFile().mkdirs();
		}

		try {
			if (destFile.exists() && Files.isSameFile(sourceFile.toPath(), destFile.toPath())) {
				errorMsg = String.format(ViewConstants.ERROR_SAVE_NO_CHANGES,
						(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, sourcePath);
			} else {
				Files.copy(sourceFile.toPath(), destFile.toPath());
			}
		} catch (FileAlreadyExistsException e) {
			errorMsg = String.format(ViewConstants.ERROR_SAVE_FILE_ALREADY_EXISTS, this.getContent());
		} catch (IOException e) {
			errorMsg = String.format(ViewConstants.ERROR_SAVE_COPY_FILE,
					(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, this.getContent());
		}

		return errorMsg;
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
