package app.view;

import java.io.IOException;

import app.Main;
import app.controller.CommandController;
import app.helper.LogHelper;
import app.model.Task;
import app.model.TaskList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * View Manager for the root of the program. This manager instantiates all
 * sub-views and provides a references to each of them.
 */
public class ViewManager {

	private BorderPane rootLayout;
	private AnchorPane inputViewLayout;
	private ListView<Task> taskListViewLayout;

	private CommandController commandController;
	private InputViewManager inputViewManager;
	private TaskListViewManager taskListViewManager;

	@FXML
	private Label statusBar;

	/**
	 * This is the main initialization method for the ViewManager. This method
	 * initializes all relevant components, such as the CommandController and
	 * views.
	 * 
	 * @param primaryStage The main window.
	 */
	public void initialize(Stage primaryStage, BorderPane rootLayout) {
		this.rootLayout = rootLayout;
		commandController = new CommandController(this);
		initializeViews();
		showStage(primaryStage);
	}

	/**
	 * Initializes all views for the program. Views initialized: RootView,
	 * TaskListView, InputView.
	 * 
	 * @param primaryStage The stage (window) for which the views will be
	 *            attached to.
	 */
	public void initializeViews() {
		initializeTaskListView();
		initializeInputView();
	}

	/**
	 * Applies the root view to the stage (window) before displaying the stage.
	 * 
	 * @param primaryStage The stage (window) to display.
	 */
	public void showStage(Stage primaryStage) {
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Initializes the TaskListView.
	 */
	private void initializeTaskListView() {
		LogHelper.getLogger().info("Initializing tasklist view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/TaskListView.fxml");
			taskListViewLayout = loader.load();
			taskListViewManager = loader.getController();
			taskListViewManager.setViewManager(this);
			rootLayout.setCenter(taskListViewLayout);
		} catch (IOException e) {
			LogHelper.getLogger().severe(e.getMessage());
		}
	}

	/**
	 * Initializes the InputView, which contains a TextField to take input from
	 * the user.
	 */
	private void initializeInputView() {
		LogHelper.getLogger().info("Initializing input view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/InputView.fxml");
			inputViewLayout = loader.load();
			inputViewManager = loader.getController();
			inputViewManager.setViewManager(this);
			VBox vbox = (VBox) rootLayout.getBottom();
			vbox.getChildren().add(inputViewLayout);
		} catch (IOException e) {
			LogHelper.getLogger().info(e.getMessage());
		}
	}

	/**
	 * Updates the task list shown to the user.
	 * 
	 * @param tasks TaskList containing a list of tasks.
	 */
	public void updateTaskList(TaskList tasks) {
		taskListViewManager.updateView(tasks);
	}

	/**
	 * Sets the status bar text.
	 * 
	 * @param text The status bar text to set.
	 */
	public void setStatus(String text) {
		statusBar.setText(text);
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

	public CommandController getCommandController() {
		return commandController;
	}

}
