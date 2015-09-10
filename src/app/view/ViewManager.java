package app.view;

import java.io.IOException;

import app.Main;
import app.constants.ViewConstants;
import app.constants.ViewConstants.StatusType;
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
	private AnchorPane textViewLayout;
	private ListView<Task> taskListViewLayout;

	private CommandController commandController;
	private InputViewManager inputViewManager;
	private TaskListViewManager taskListViewManager;
	private TextViewManager textViewManager;

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
		commandController = CommandController.getInstance();
		commandController.setViewManager(this);
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
	private void initializeViews() {
		initializeTaskListView();
		initializeInputView();
		initializeTextView();
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

	private void initializeTextView() {
		LogHelper.getLogger().info("Initializing text view");
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/TextView.fxml");
			textViewLayout = loader.load();
			textViewManager = loader.getController();
			textViewManager.setViewManager(this);
		} catch (IOException e) {
			LogHelper.getLogger().severe(e.getMessage());
		}
	}

	/**
	 * Updates and shows the task list to the user.
	 * 
	 * @param tasks TaskList containing a list of tasks.
	 */
	public void updateTaskList(TaskList tasks) {
		taskListViewManager.updateView(tasks);
		rootLayout.setCenter(taskListViewLayout);
	}

	/**
	 * Updates the shows the text view to the user.
	 * 
	 * @param text The text to populate the text view with.
	 */
	public void updateTextView(String text) {
		textViewManager.setText(text);
		rootLayout.setCenter(textViewLayout);
	}

	/**
	 * Sets the status bar text with StatusType.INFO (Black).
	 * 
	 * @param text The status bar text to set.
	 */
	public void setStatus(String text) {
		setStatus(text, StatusType.INFO);
	}

	/**
	 * Sets the status bar text with specified StatusType and corresponding
	 * color.
	 * 
	 * @param text The status bar text to set.
	 * @param type The StatusType of the text.
	 */
	public void setStatus(String text, StatusType type) {
		statusBar.setText(text);
		statusBar.getStyleClass().removeAll(ViewConstants.STATUS_STYLE_SUCCESS, ViewConstants.STATUS_STYLE_ERROR,
				ViewConstants.STATUS_STYLE_INFO);
		statusBar.getStyleClass().add(determineStatusStyleClass(type));
	}

	/**
	 * Sets the current theme of the program.
	 * 
	 * @param themeCss The new theme to use. This parameter is either
	 *            ViewConstants.THEME_LIGHT_CSS or ViewConstants.THEME_DARK_CSS.
	 */
	public void setTheme(String themeCss) {
		rootLayout.getStylesheets().removeAll(ViewConstants.THEME_LIGHT_CSS, ViewConstants.THEME_DARK_CSS);
		rootLayout.getStylesheets().add(themeCss);
	}

	/**
	 * Determines the style class for the specified StatusType. This style class
	 * will be used by the theme CSS files to properly color the text.
	 * 
	 * @param type The StatusType of the text.
	 * @return The style class for the specified StatusType, as a string.
	 */
	private String determineStatusStyleClass(StatusType type) {
		switch (type) {
		case SUCCESS:
			return ViewConstants.STATUS_STYLE_SUCCESS;
		case ERROR:
			return ViewConstants.STATUS_STYLE_ERROR;
		case INFO:
		default:
			return ViewConstants.STATUS_STYLE_INFO;
		}
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
