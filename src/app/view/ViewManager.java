package app.view;

import java.io.IOException;

import app.Main;
import app.helper.LogHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * View Manager for the root of the program. This manager instantiates all
 * sub-views and provides a references to each of them.
 */
public class ViewManager {

	private Main mainApp;
	private AnchorPane rootLayout;
	private AnchorPane inputViewLayout;
	private ListView<String> taskListViewLayout;
	private ObservableList<String> taskList = FXCollections.observableArrayList();

	private InputViewManager inputViewManager;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Initializes all views for the program. Views initialized: RootView,
	 * TaskListView, InputView.
	 * 
	 * @param primaryStage The stage (window) for which the views will be
	 *            attached to
	 */
	public void initializeViews(Stage primaryStage) {
		initializeRootView(primaryStage);
		initializeTaskListView(primaryStage);
		initializeInputView(primaryStage);
	}

	/**
	 * Initializes the RootView.
	 * 
	 * @param primaryStage The main window to attach the view to.
	 */
	private void initializeRootView(Stage primaryStage) {
		LogHelper.info("Initializing root view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/RootView.fxml");
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

	/**
	 * Initializes the TaskListView.
	 * 
	 * @param primaryStage The main window to attach the view to.
	 */
	private void initializeTaskListView(Stage primaryStage) {
		LogHelper.info("Initializing tasklist view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/TaskListView.fxml");
			taskListViewLayout = loader.load();
			rootLayout.getChildren().add(0, taskListViewLayout);
		} catch (IOException e) {
			LogHelper.severe(e.getMessage());
		}
	}

	/**
	 * Initializes the InputView, which contains a TextField to take input from
	 * the user.
	 * 
	 * @param primaryStage The main window to attach the view to.
	 */
	private void initializeInputView(Stage primaryStage) {
		LogHelper.info("Initializing input view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/InputView.fxml");
			inputViewLayout = loader.load();
			inputViewManager = loader.getController();
			inputViewManager.setViewManager(this);
			rootLayout.getChildren().add(1, inputViewLayout);
		} catch (IOException e) {
			LogHelper.severe(e.getMessage());
		}
	}

	public void updateTaskList() {
		// TODO: this is just test code
		taskList.addAll("hey", "ha", "ho");
		taskListViewLayout.setItems(taskList);
	}
	
	public void updateTaskList(String task) {
		taskList.add(task);
		taskListViewLayout.setItems(taskList);
	}

	/**
	 * Builds and returns a FXMLLoader object from a given string path pointing
	 * to an .fxml file.
	 * 
	 * @param fxml The path to the .fxml file
	 * @return The constructed FXMLLoader
	 */
	private FXMLLoader buildFxmlLoader(String fxml) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource(fxml));
		return loader;
	}

}
