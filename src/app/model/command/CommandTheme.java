package app.model.command;

import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
import app.controller.CommandController;

public class CommandTheme extends Command {

	public CommandTheme() {
		super();
	}

	public CommandTheme(CommandType type) {
		super(type);
	}

	@Override
	public void execute() {
		if (getContent().isEmpty()) {
			setFeedback("Available themes: light, dark");
		}
		if (getContent().equalsIgnoreCase("light")) {
			CommandController.getInstance().setTheme(ViewConstants.THEME_LIGHT_CSS);
			setFeedback("Current theme: light");
			setStatusType(StatusType.SUCCESS);
		} else if (getContent().equalsIgnoreCase("dark")) {
			CommandController.getInstance().setTheme(ViewConstants.THEME_DARK_CSS);
			setFeedback("Current theme: dark");
			setStatusType(StatusType.SUCCESS);
		} else {
			setFeedback("Available themes: light, dark");
		}
	}

}
