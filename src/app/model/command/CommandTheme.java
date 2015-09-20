package app.model.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.controller.CommandController;

public class CommandTheme extends Command {

	public CommandTheme() {
		super();
		this.setCommandType(CommandType.THEME);
	}

	@Override
	public void execute() {
		if (getContent().isEmpty()) {
			setFeedback(ViewConstants.MESSAGE_AVAILABLE_THEMES);
		}
		if (getContent().equalsIgnoreCase(ViewConstants.THEME_LIGHT)) {
			CommandController.getInstance().setTheme(ViewConstants.THEME_LIGHT_CSS);
			setFeedback(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_LIGHT));
			setStatusType(StatusType.SUCCESS);
		} else if (getContent().equalsIgnoreCase(ViewConstants.THEME_DARK)) {
			CommandController.getInstance().setTheme(ViewConstants.THEME_DARK_CSS);
			setFeedback(String.format(ViewConstants.MESSAGE_CURRENT_THEME, ViewConstants.THEME_DARK));
			setStatusType(StatusType.SUCCESS);
		} else {
			setFeedback(ViewConstants.MESSAGE_AVAILABLE_THEMES);
		}
	}

}
