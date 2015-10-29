package app.model;

import java.util.ArrayList;

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
			return "";
		}
	}
	
	public String next() {
		index++;
		if (isValidIndex(index)) {
			return commandList.get(index);
		} else {
			return "";
		}
	}
	
	public void reset() {
		index = commandList.size() - 1;
	}
	
	private boolean isValidIndex(int index) {
		return (index >= 0 && index < commandList.size());
	}
}
