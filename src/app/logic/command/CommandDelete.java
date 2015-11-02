package app.logic.command;

import app.constants.ViewConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;
import app.storage.TaskStorage;
import app.util.Common;
import app.util.LogHelper;

public class CommandDelete extends Command {
	
	private ArrayList<Task> previousTaskList = new ArrayList<Task>();
	private ViewState previousViewState;
	
	public CommandDelete() {
		super();
		this.setCommandType(CommandType.DELETE);
	}

	@Override
	public ViewState execute(ViewState previousViewState) {
		LogHelper.getInstance().getLogger().info("Executing CommandDelete object.");
		ViewState viewState = new ViewState();
		this.previousViewState = new ViewState(previousViewState); 

		if (this.getContent().isEmpty()) {
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_DELETE_NO_TASK);
			return viewState;
		}
		
		try {
			TaskList master = CommandController.getInstance().getMasterTaskList();
			TaskList display = previousViewState.getTaskList();
			
			ArrayList<Integer> ids = Common.getIdArrayList(this.getContent()); 
			ArrayList<UUID> tasksUuidList = display.getTasksUuidList(ids);
			ArrayList<Integer> masterIdsList = master.getTasksIdList(tasksUuidList);

			Collections.sort(ids, Collections.reverseOrder());
			Collections.sort(masterIdsList, Collections.reverseOrder());
			
			
			// remove identical id's 
			for (int i = ids.size() - 1; i > 0; i--) {
				if (ids.get(i).intValue() == ids.get(i - 1).intValue()) {
					ids.remove(i);	
					masterIdsList.remove(i);
				}
			}
			
			ArrayList<UUID> deletedTask = new ArrayList<UUID>();
			// remove task from display list
			for (int i : ids) {
				deletedTask.add(display.getTaskUuidByIndex(i-1));
				display.getTaskList().remove(i - 1);
			}

		
			// remove task from master list
			for (int i : masterIdsList) {
				previousTaskList.add(master.getTaskList().remove(i));
			}
			
			
			TaskStorage.getInstance().writeTasks(master);
			viewState.setTaskList(display);
			viewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_DELETE, this.getContent()));
			logDeletedTaskUuid(deletedTask);
			setExecuted(true);

		} catch (IndexOutOfBoundsException e) {
			LogHelper.getInstance().getLogger().info("IndexOutOfBoundsException:" + e.getMessage() +
					"; " + ViewConstants.ERROR_MARK_INVALID_ID);
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_DELETE_INVALID_ID);
		} catch (NullPointerException e) {
			LogHelper.getInstance().getLogger().info("NullPointerException:" + e.getMessage() + 
					"; " + ViewConstants.ERROR_DELETE_INVALID_ID);
			viewState.setStatus(StatusType.ERROR, ViewConstants.ERROR_DELETE_INVALID_ID);
		} catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
			viewState.setStatus(StatusType.ERROR, String.format(ViewConstants.ERROR_DELETE, this.getContent()));
		} 

		viewState.setActiveView(ViewType.TASK_LIST);
		return viewState;
	}
	
	@Override
	public ViewState undo() {
		if (!isExecuted()) {
			return new ViewState();
		}
		
		try {
		
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList displayed = previousViewState.getTaskList();
		
		for (Task i : previousTaskList){
			master.addTask(i);	
			displayed.addTask(i);
		}
		
		TaskStorage.getInstance().writeTasks(master);		
		previousViewState.setTaskList(displayed);
		previousViewState.setStatus(StatusType.SUCCESS, String.format(ViewConstants.MESSAGE_UNDO));
		LogHelper.getInstance().getLogger().info(String.format("UNDO_DELETE:" + ViewConstants.MESSAGE_UNDO));
		setExecuted(true);
		
		} 	catch (Exception e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage() + String.format(ViewConstants.ERROR_UNDO));
			previousViewState.setStatus(StatusType.ERROR, String.format(ViewConstants.MESSAGE_UNDO));
		}	
	
		return previousViewState;
	
	}

	private void logDeletedTaskUuid(ArrayList<UUID> arr) {
		String uuidFeedback = Common.getUuidListString(arr);
		LogHelper.getInstance().getLogger().info(String.format(ViewConstants.MESSAGE_DELETE, uuidFeedback));
	}

}
