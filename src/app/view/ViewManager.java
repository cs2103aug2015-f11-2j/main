package app.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.Main;
import app.helper.LogHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewManager {

	private Main mainApp;
	private AnchorPane rootLayout;
	private ListView<String> taskListViewLayout;
	private ObservableList<String> taskList = FXCollections.observableArrayList();

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}
	
	public void initializeViews(Stage primaryStage) {
		initializeRootView(primaryStage);
		initializeTaskListView(primaryStage);
	}

	private void initializeRootView(Stage primaryStage) {
		LogHelper.info("Initializing root view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/RootView.fxml");
			rootLayout = loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setMinWidth(600);
			primaryStage.setMinHeight(300);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			LogHelper.severe(e.getMessage());
		}

	}
	
	private void initializeTaskListView(Stage primaryStage) {
		LogHelper.info("Initializing tasklist view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/TaskListView.fxml");
			taskListViewLayout = loader.load();
			rootLayout.getChildren().add(0, taskListViewLayout);
		} catch (IOException e) {
			LogHelper.severe(e.getMessage());
		}
	}
	
	
	public void updateTaskList() {
		taskList.addAll("hey", "ha", "ho");
		LogHelper.info(taskList.toString());
		taskListViewLayout.setItems(taskList);
	}
	
	private FXMLLoader buildFxmlLoader(String fxml) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource(fxml));
		return loader;
	}
	
	public void onKeypressEnter() {
		LogHelper.info("User pressed enter key");
		// TODO on user input
	}
}
