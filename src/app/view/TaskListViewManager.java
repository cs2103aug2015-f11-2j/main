package app.view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Queue;
import java.util.UUID;

import app.Main;
import app.constants.ViewConstants;
import app.helper.LogHelper;
import app.model.Task;
import app.model.TaskCell;
import app.model.TaskList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TaskListViewManager {
	private ViewManager viewManager;

	@FXML
	private ListView<TaskCell> taskListViewLayout;
	@FXML
	private Label taskListHeader;

	/**
	 * This method is implicitly called when the TaskListView is loaded from the
	 * FXMLLoader. The logic for setting our custom cell (TaskListItemView) is
	 * defined by setting the cell factory of the TaskListView.
	 */
	@FXML
	public void initialize() {
		taskListViewLayout.setCellFactory(new Callback<ListView<TaskCell>, ListCell<TaskCell>>() {
			@Override
			public ListCell<TaskCell> call(ListView<TaskCell> listView) {
				try {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(Main.class.getResource("view/fxml/TaskListItemView.fxml"));
					loader.load();
					return loader.getController();
				} catch (IOException e) {
					LogHelper.getLogger().severe(e.getMessage());
				}
				return null;
			}
		});
		hideHeaderIfEmpty();
	}

	public void hideHeaderIfEmpty() {
		if (taskListViewLayout.getChildrenUnmodifiable().isEmpty()) {
			taskListHeader.setPrefHeight(0);
		} else {
			taskListHeader.setPrefHeight(-1);
			taskListHeader.setMinHeight(-1);
		}
	}

	public void scrollTo(Task task) {
		UUID uuid = task.getId();
		for (TaskCell cell : taskListViewLayout.getItems()) {
			if (cell.getTask() != null && cell.getTask().getId().equals(uuid)) {
				taskListViewLayout.scrollTo(cell);
				break;
			}
		}
	}

	public void setHeader(String text) {
		taskListHeader.setText(text);
	}

	/**
	 * Updates the list of tasks shown to the user
	 * 
	 * @param tasks A TaskList object containing the list of tasks.
	 */
	public void updateView(TaskList tasks) {
		taskListViewLayout.setItems(buildTaskCells(tasks));
		hideHeaderIfEmpty();
	}

	private ObservableList<TaskCell> buildTaskCells(TaskList tasks) {
		// assume tasks is sorted already.
		ObservableList<TaskCell> taskCells = FXCollections.observableArrayList();
		Queue<String> colors = ViewConstants.getItemColorsQueue();
		
		LocalDate labelDate = null;
		String currentColor = "";
		int index = 1;
		
		for (Task task : tasks.getTaskList()) {
			LocalDate sortKey = null;
			if (task.getSortKey() != null) {
				sortKey = task.getSortKey().toLocalDate();
			}
			
			// If first occurrence of a new date, add a TaskCell for the date label
			if ((sortKey != null && labelDate == null)
					|| (labelDate != null && sortKey != null && !labelDate.isEqual(sortKey))) {
				currentColor = colors.poll();
				colors.offer(currentColor);
				labelDate = task.getSortKey().toLocalDate();
				TaskCell cell = new TaskCell(labelDate, currentColor);
				taskCells.add(cell);
			}

			// Add a TaskCell for the Task item
			TaskCell cell = new TaskCell(task, index++, currentColor);
			taskCells.add(cell);
		}

		return taskCells;
	}

	/**
	 * This sets a reference to the ViewManager that initialized this class.
	 * 
	 * @param viewManager The ViewManager that initialized this class.
	 */
	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
}
