package app.logic.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.model.ViewState;
import app.storage.AppStorage;
import app.util.LogHelper;

public class CommandTheme extends Command {

	public CommandTheme() {
		super();
		this.setCommandType(CommandType.THEME);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		ViewState viewState = new ViewState();
		if (getContent().isEmpty()) {
			viewState.setStatus(ViewConstants.MESSAGE_AVAILABLE_THEMES);
			LogHelper.getLogger().info("No theme entered.");
			return viewState;
		}
		if (getContent().equalsIgnoreCase(ViewConstants.THEME_LIGHT)) {
			AppStorage.getInstance().setSelectedTheme(ViewConstants.THEME_LIGHT);
			viewState.setTheme(ViewConstants.THEME_LIGHT_CSS);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_LIGHT));
			LogHelper.getLogger().info(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_LIGHT));
			setExecuted(true);
		} else if (getContent().equalsIgnoreCase(ViewConstants.THEME_DARK)) {
			AppStorage.getInstance().setSelectedTheme(ViewConstants.THEME_DARK);
			
			viewState.setTheme(ViewConstants.THEME_DARK_CSS);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_DARK));
			LogHelper.getLogger().info(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_DARK));
			setExecuted(true);
		} else {
			viewState.setStatus(ViewConstants.MESSAGE_AVAILABLE_THEMES);
			LogHelper.getLogger().info("Invalid theme entered.");
		}
		
		return viewState;
	}

}
