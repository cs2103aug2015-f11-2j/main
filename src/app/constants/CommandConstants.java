package app.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandConstants {
	public static final List<String> ALIASES_ADD = getUnmodifiableList("add", "a");
	public static final List<String> ALIASES_REMOVE = getUnmodifiableList("remove", "delete", "rm");
	public static final List<String> ALIASES_HELP = getUnmodifiableList("help", "?");
	
	public enum CommandType {
		ADD, REMOVE, UPDATE, SEARCH, EXIT, HELP, INVALID;
	}
	
	private static List<String> getUnmodifiableList(String... args) {
		return Collections.unmodifiableList(Arrays.asList(args));
	}
}
