package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.model.ViewState;
import app.storage.AppStorage;
import app.util.LogHelper;

public class CommandTheme extends Command {

	private ViewState previousViewState;
	
	public CommandTheme() {
		super();
		this.setCommandType(CommandType.THEME);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		ViewState viewState = new ViewState();
		this.previousViewState = new ViewState(previousViewState);
		
		if (getContent().isEmpty()) {
			viewState.setStatus(ViewConstants.MESSAGE_AVAILABLE_THEMES);
			LogHelper.getInstance().getLogger().info("No theme entered.");
			return viewState;
		}
		
		if (getContent().equalsIgnoreCase(ViewConstants.THEME_LIGHT)) {
			AppStorage.getInstance().setSelectedTheme(ViewConstants.THEME_LIGHT);
			viewState.setTheme(ViewConstants.THEME_LIGHT_CSS);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_LIGHT));
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_LIGHT));
			setExecuted(true);
		} else if (getContent().equalsIgnoreCase(ViewConstants.THEME_DARK)) {
			AppStorage.getInstance().setSelectedTheme(ViewConstants.THEME_DARK);
			viewState.setTheme(ViewConstants.THEME_DARK_CSS);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_DARK));
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_DARK));
			setExecuted(true);
		} else {
			viewState.setStatus(ViewConstants.MESSAGE_AVAILABLE_THEMES);
			LogHelper.getInstance().getLogger().info("Invalid theme entered.");
		}
		
		return viewState;
	}
	
	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
		
		ViewState viewState = new ViewState();
		viewState.setTheme(previousViewState.getTheme());
		
		try {
		
		if (previousViewState.getTheme().equals(ViewConstants.THEME_LIGHT_CSS)) {
			AppStorage.getInstance().setSelectedTheme(ViewConstants.THEME_LIGHT);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_LIGHT));
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_LIGHT));
		} else if (previousViewState.getTheme().equals(ViewConstants.THEME_DARK_CSS)) {
			AppStorage.getInstance().setSelectedTheme(ViewConstants.THEME_DARK);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_DARK));
			LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_DARK));
		}
		
		viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_UNDO));
		setExecuted(true);
		
		} 	catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage() + String.format(ViewConstants.ERROR_UNDO));
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.MESSAGE_UNDO));
		}	
		
		return viewState;
	}

}
