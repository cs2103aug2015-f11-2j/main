package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import app.helper.CommandParser;
import app.model.command.Command;

public class ParserTest {

	@Test
	public void test() {
		CommandParser parser = new CommandParser();
		String input = "add buy milk due 15/11/15 0959";
		Command cmd = parser.parseCommand(input);
		
		input = "add buy milk from tomorrow to 3pm due 15/11/15 0959";
		cmd = parser.parseCommand(input);
	}

}
