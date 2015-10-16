package app.logic.command;

import app.constants.ViewConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.model.ViewState;
import app.storage.AppStorage;
import app.util.LogHelper;

public class CommandSave extends Command {
	public CommandSave() {
		super();
		this.setCommandType(CommandType.SAVE);
	}
	
	@Override
	public ViewState execute(ViewState previousViewState) {
		ViewState viewState = new ViewState();
		
		try {
			if (this.getContent().isEmpty()) {
				viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_SAVE_NO_LOCATION);
				LogHelper.getLogger().info(ViewConstants.ERROR_SAVE_NO_LOCATION);
			} else {
				boolean isLog = false;
				
				if (getFirstWord(this.getContent()).equals(ViewConstants.SAVE_LOG)) {
					isLog = true;
					this.setContent(removeFirstWord(this.getContent()));
				}
				
				String prevLocation = (isLog) ? AppStorage.getInstance().getLogFileLocation()
											  : AppStorage.getInstance().getStorageFileLocation();

				if (this.getContent().equals(prevLocation)) {
					viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_SAVE_NO_CHANGES,
							(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, this.getContent()));
					LogHelper.getLogger().info(String.format(ViewConstants.ERROR_SAVE_NO_CHANGES,
							(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, this.getContent()));
				} else if (this.getContent().equals(ViewConstants.SAVE_DEFAULT)) {
					if (isLog) {
						AppStorage.getInstance().setDefaultLogFileLocation();
					} else {
						AppStorage.getInstance().setDefaultStorageFileLocation();
					}
				} else {
					if (isLog) {
						if (this.getContent().equals(ViewConstants.SAVE_DEFAULT)) {
							AppStorage.getInstance().setDefaultLogFileLocation();
						} else {
							AppStorage.getInstance().setLogFileLocation(this.getContent());
						}
					} else {
						if (this.getContent().equals(ViewConstants.SAVE_DEFAULT)) {
							AppStorage.getInstance().setDefaultStorageFileLocation();
						} else {
							AppStorage.getInstance().setStorageFileLocation(this.getContent());
						}
					}
					
					LogHelper.getLogger().info(String.format(ViewConstants.MESSAGE_SAVE,
							(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, this.getContent()));
					viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_SAVE,
							(isLog) ? ViewConstants.SAVE_LOG : ViewConstants.SAVE_STORAGE, this.getContent()));
					setExecuted(true);
				}
			}
		} catch (Exception e) {
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_SAVE, this.getContent()));
			LogHelper.getLogger().severe(e.getMessage());
		}
		return viewState;
	}
	
	private String[] splitParameters(String commandParametersString) {
		String[] parameters = commandParametersString.trim().split("\\s+");
		
		return parameters;
	}
	
	private String getFirstWord(String keyword) {
		return splitParameters(keyword)[0];
	}
	
	private String removeFirstWord(String keyword) {
		String userCommandWithoutFirstWord = keyword.replace(getFirstWord(keyword), "").trim();
		
		return userCommandWithoutFirstWord;
	}
}
