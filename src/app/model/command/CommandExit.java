package app.model.command;

import app.constants.CommandConstants.CommandType;

public class CommandExit extends Command {

	public CommandExit() {
		super();
	}

	public CommandExit(CommandType type) {
		super(type);
	}

	@Override
	public void execute() {
		System.exit(0);
	}

}
