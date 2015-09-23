package app.view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import app.Main;
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
		// taskListViewLayout.scrollTo(task);
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

	// TODO: very hacky implementation. clean this up.
	private ObservableList<TaskCell> buildTaskCells(TaskList tasks) {
		// assume tasks is sorted already.
		ObservableList<TaskCell> taskCells = FXCollections.observableArrayList();
		LocalDate date = null;
		int index = 1;
		for (Task task : tasks.getTaskList()) {

			// add label
			LocalDate sortKey = null;
			if (task.getSortKey() != null) {
				sortKey = task.getSortKey().toLocalDate();
			}
			if ((sortKey != null && date == null) || (date != null && sortKey != null && !date.isEqual(sortKey))) {
				date = task.getEndDate().toLocalDate();
				TaskCell cell = new TaskCell();
				cell.setLabel(date.toString());
				taskCells.add(cell);
			}

			TaskCell cell = new TaskCell();
			cell.setTask(task);
			cell.setIndex(index++);
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
