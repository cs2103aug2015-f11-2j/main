package app.view;

import java.io.IOException;

import app.Main;
import app.helper.LogHelper;
import app.model.Task;
import app.model.TaskList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TaskListViewManager {
	private ViewManager viewManager;

	@FXML
	private ListView<Task> taskListViewLayout;
	@FXML
	private Label taskListHeader;

	/**
	 * This method is implicitly called when the TaskListView is loaded from the
	 * FXMLLoader. The logic for setting our custom cell (TaskListItemView) is
	 * defined by setting the cell factory of the TaskListView.
	 */
	@FXML
	public void initialize() {
		taskListViewLayout.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
			@Override
			public ListCell<Task> call(ListView<Task> listView) {
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
		}
	}
	
	public void scrollTo(Task task) {
		taskListViewLayout.scrollTo(task);
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
		taskListViewLayout.setItems(tasks.getTaskList());
		hideHeaderIfEmpty();
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
