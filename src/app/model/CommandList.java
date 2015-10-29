package app.model;

import java.util.ArrayList;

public class CommandList {
	ArrayList<String> commandList;
	int index;

	public CommandList() {
		commandList = new ArrayList<String>();
		index = -1;
	}
	
	public void addCommand(String cmd) {
		commandList.add(cmd);
		//reset index to latest command
		index = commandList.size() - 1;
	}
	
	//decrements index and returns command (String)
	public String getPrevCommand() {
		index--;
		if (index != -1) {
			return commandList.get(index);
		} else {
			return "-1";
		}
	}
	
	public String getNextCommand() {
		index++;
		if (index < commandList.size() - 1) {
			return commandList.get(index);
		} else {
			//reset back to latest command and return -1
			index = commandList.size() - 1;
			return "-1";
		}
	}
	
	public void resetIndex() {
		index = commandList.size() - 1;
	}
}
