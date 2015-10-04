package app.logic.command;

import app.constants.ViewConstants;
import java.util.ArrayList;
import java.util.UUID;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.model.TaskList;
import app.model.ViewState;
import app.util.Common;
import app.util.LogHelper;

public class CommandDelete extends Command {

	public CommandDelete() {
		super();
		this.setCommandType(CommandType.REMOVE);
	}
	
	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getLogger().info("Executing CommandDelete object.");
		ViewState viewState = new ViewState();
		
		if (this.getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_DELETE_NO_TASK);
			return viewState;
		}
		
		try {
			// master task list contains all tasks in memory
			TaskList master = CommandController.getInstance().getMasterTaskList();
			
			// displayed task list is the task list currently being shown to the user
			// this is the task list the user is using to select the tasks he wants to delete
			TaskList display = previousViewState.getTaskList();
			
			// get the ids the user specified. 
			// example: if the command is "delete 1 3 4" , this method will give you give [1, 3, 4]
			ArrayList<Integer> ids = Common.getIdArrayList(this.getContent());
			
			// find the Task objects that these ids (1,3,4) correspond to, inside the displayed task list
			// The tasks here are identified by their UUID (unique identifier)
			ArrayList<UUID> tasksUuidList = display.getTasksUuidList(ids);
			
			
			// Use the UUIDs to find the indexes of the tasks in the master task list.
			// indexes as in, from 0 to master.size()
			ArrayList<Integer> masterIdsList = master.getTasksIdList(tasksUuidList);
			
			
			// remove task from master list
			for( Integer i : masterIdsList){
				master.getTaskList().remove(i);
			}
			
			
			// store message
			String message = display.getTaskList().get(Integer.parseInt(this.getContent())-1).getName();
			
			
			// remove task from display list
			display.getTaskList().remove(Integer.parseInt(this.getContent())-1);

			
			viewState.setTaskList(display);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DELETE, message));
			setExecuted(true);

			
		} catch (Exception e) {
			LogHelper.getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_DELETE, this.getContent()));
		}
		
		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}
	
}
