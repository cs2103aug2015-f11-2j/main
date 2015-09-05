package app.view;

import java.io.IOException;

import app.Main;
import app.helper.LogHelper;
import app.model.Task;
import app.model.TaskList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TaskListViewManager {
	private ViewManager viewManager;
	private ObservableList<Task> taskList = FXCollections.observableArrayList();

	@FXML
	private ListView<Task> taskListViewLayout;

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
	}

	public void updateView(TaskList tasks) {
		taskList = FXCollections.observableArrayList(tasks.getTaskList());
		taskListViewLayout.setItems(taskList);
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
}
