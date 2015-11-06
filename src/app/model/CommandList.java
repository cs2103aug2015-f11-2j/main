package app.model;

import java.util.ArrayList;

//@@author A0132764E
public class CommandList {
	ArrayList<String> commandList;
	int index;

	public CommandList() {
		commandList = new ArrayList<String>();
		index = -1;
	}
	
	public void add(String cmd) {
		commandList.add(cmd);
		reset();
	}
	
	//decrements index and returns command (String)
	public String prev() {
		index--;
		if (isValidIndex(index)) {
			return commandList.get(index);
		} else {
			index = 0;
			return "";
		}
	}
	
	public String next() {
		index++;
		if (isValidIndex(index)) {
			return commandList.get(index);
		} else {
			index = commandList.size();
			return "";
		}
	}
	
	public void reset() {
		index = commandList.size();
	}
	
	private boolean isValidIndex(int index) {
		return (index >= 0 && index < commandList.size());
	}
}
