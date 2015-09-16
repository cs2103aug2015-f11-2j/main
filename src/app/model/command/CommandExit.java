package app.model.command;

import app.constants.CommandConstants.CommandType;

public class CommandExit extends Command {

	public CommandExit() {
		super();
		this.setCommandType(CommandType.EXIT);
	}

	@Override
	public void execute() {
		System.exit(0);
	}

}
