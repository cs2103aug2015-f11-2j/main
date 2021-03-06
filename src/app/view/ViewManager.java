package app.view;

import java.io.IOException;
import java.util.List;

import app.Main;
import app.constants.CommandConstants.CommandType;
import app.constants.StorageConstants;
import app.constants.ViewConstants;
import app.constants.ViewConstants.ScrollDirection;
import app.constants.ViewConstants.StatusType;
import app.constants.ViewConstants.ViewType;
import app.logic.CommandController;
import app.logic.command.Command;
import app.model.Action;
import app.model.Task;
import app.model.TaskList;
import app.model.ViewState;
import app.util.LogHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * View Manager for the root of the program. This class instantiates all
 * sub-views and provides a references to each of them. This manager this is the
 * only one to interact with its child views.
 */
// @@author A0126120B
public class ViewManager {

	private Stage primaryStage;

	private BorderPane rootLayout;
	private AnchorPane inputViewLayout;
	private AnchorPane textViewLayout;
	private VBox infoViewLayout;
	private VBox taskListViewLayout;

	private InputViewManager inputViewManager;
	private TaskListViewManager taskListViewManager;
	private TextViewManager textViewManager;
	private InfoViewManager infoViewManager;

	@FXML
	private Label header;
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
		this.primaryStage = primaryStage;
		this.rootLayout = rootLayout;
		initializeViews();
		updateView(CommandController.getInstance().getCurrentViewState());
		showStage(primaryStage);
	}

	/**
	 * Initializes all views for the program. Views initialized: TaskList,
	 * Input, Text, Info.
	 * 
	 * @param primaryStage The stage (window) for which the views will be
	 *            attached to.
	 */
	private void initializeViews() {
		initializeTaskListView();
		initializeInputView();
		initializeTextView();
		initializeInfoView();
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
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_INITIALIZE_VIEW, "tasklist view"));
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/TaskListView.fxml");
			taskListViewLayout = loader.load();
			taskListViewManager = loader.getController();
			taskListViewManager.setViewManager(this);
			showTaskList();
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
		}
	}

	/**
	 * Initializes the InputView, which contains a TextField to take input from
	 * the user.
	 */
	private void initializeInputView() {
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_INITIALIZE_VIEW, "input view"));
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/InputView.fxml");
			inputViewLayout = loader.load();
			inputViewManager = loader.getController();
			inputViewManager.setViewManager(this);
			VBox vbox = (VBox) rootLayout.getBottom();
			vbox.getChildren().add(inputViewLayout);
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().info(e.getMessage());
		}
	}

	/**
	 * Initializes the TextView, which is simply a text area to show information
	 * (such as help).
	 */
	private void initializeTextView() {
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_INITIALIZE_VIEW, "text view"));
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/TextView.fxml");
			textViewLayout = loader.load();
			textViewManager = loader.getController();
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
		}
	}

	/**
	 * Initializes the InfoView, which contains information about the current
	 * user input.
	 */
	private void initializeInfoView() {
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_INITIALIZE_VIEW, "info view"));
		try {
			FXMLLoader loader = buildFxmlLoader("view/fxml/InfoView.fxml");
			infoViewLayout = loader.load();
			infoViewManager = loader.getController();
			infoViewManager.setViewManager(this);
			VBox vbox = (VBox) rootLayout.getBottom();
			vbox.getChildren().add(infoViewLayout);
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
		}
	}
	
	public void updateView(ViewState viewState) {
		if (viewState != null) {
			setHeader(viewState.getHeader());
			setStatus(viewState.getStatusMessage(), viewState.getStatusType());
			updateTaskList(viewState.getTaskList());
			updateTextView(viewState.getTextArea());
			setTheme(viewState.getTheme());
			showView(viewState.getActiveView());
			executeActions(viewState.getActions());
		}
	}
	
	private void showView(ViewType viewType) {
		if (viewType == ViewType.TASK_LIST) {
			showTaskList();
		} else if (viewType == ViewType.TEXT_VIEW) {
			showTextView();
		}
	}
	
	private void executeActions(List<Action> actions) {
		for (Action action : actions) {
			switch (action.getActionType()) {
			case SCROLL_TASK_LIST_TO:
				scrollTaskListTo(action.getActionObject());
				break;
			case SCROLL_TASK_LIST_TO_TOP:
				scrollTaskListToTop();
				break;
			case EXIT:	// exit the application
				Platform.exit();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Updates text of the task list header
	 * 
	 * @param text The text for the header to read
	 */
	public void setHeader(String text) {
		if (text != null) {
			header.setText(text);
		}
	}

	/**
	 * Sets the visibility of the header.
	 * 
	 * @param visible True to make header visible
	 */
	public void setHeaderVisible(boolean visible) {
		if (visible) {
			header.setPrefHeight(-1);
			header.setMinHeight(-1);
		} else {
			header.setPrefHeight(0);
			header.setMinHeight(0);
		}
	}

	public void scrollTaskListToTop() {
		taskListViewManager.scrollToTop();
	}

	/**
	 * Scrolls the task list to show the specified task.
	 * 
	 * @param task The task to scroll to
	 */
	public void scrollTaskListTo(Object object) {
		if (object instanceof Task) {
			taskListViewManager.scrollTo((Task) object);
		}
	}

	/**
	 * Updates and shows the task list to the user.
	 * 
	 * @param tasks TaskList containing a list of tasks.
	 */
	public void updateTaskList(TaskList tasks) {
		taskListViewManager.updateView(tasks);
	}

	/**
	 * Shows the task list to the user.
	 */
	public void showTaskList() {
		rootLayout.setCenter(taskListViewLayout);
	}

	/**
	 * Updates the shows the text view to the user.
	 * 
	 * @param text The text to populate the text view with.
	 */
	public void updateTextView(String text) {
		if (text != null) {
			textViewManager.setText(text);
		}
	}

	/**
	 * Shows the text area to the user.
	 */
	public void showTextView() {
		rootLayout.setCenter(textViewLayout);
	}

	/**
	 * Sets the status bar text with specified StatusType and corresponding
	 * color.
	 * 
	 * @param text The status bar text to set.
	 * @param type The StatusType of the text.
	 */
	public void setStatus(String text, StatusType type) {
		if (text != null && type != null) {
			statusBar.setText(text);
			statusBar.getStyleClass().removeAll(ViewConstants.STATUS_STYLE_SUCCESS, ViewConstants.STATUS_STYLE_ERROR,
					ViewConstants.STATUS_STYLE_INFO);
			statusBar.getStyleClass().add(determineStatusStyleClass(type));
		}
	}

	/**
	 * Sets the current theme of the program.
	 * 
	 * @param themeCss The new theme to use. This parameter is either
	 *            ViewConstants.THEME_LIGHT_CSS or ViewConstants.THEME_DARK_CSS.
	 */
	public void setTheme(String themeCss) {
		if (themeCss != null) {
			rootLayout.getStylesheets().removeAll(ViewConstants.THEME_LIGHT_CSS, ViewConstants.THEME_DARK_CSS);
			rootLayout.getStylesheets().add(themeCss);
		}
	}

	/**
	 * Updates the info view using parameters from the specified Command object
	 * parsed from the user's input
	 * 
	 * @param cmd The Command object parsed from user input
	 */
	public void updateInfoView(String commandString) {
		Command cmd = CommandController.getInstance().createCommand(commandString);
		if (cmd != null) {
			infoViewManager.updateView(cmd);
		}
	}
	
	public CommandType getInfoViewCurrentCommandType() {
		return infoViewManager.getCurrentCommandType();
	}
	
	public void executeUserInput(String input) {
		ViewState newViewState = CommandController.getInstance().executeCommand(input);
		updateView(newViewState);
	}

	public void scrollTaskList(ScrollDirection direction) {
		taskListViewManager.scrollTaskList(direction);
	}
	
	public void sendCommandToInput(String command) {
		executeUserInput(command);
	}
	
	public String getCommandHistoryNext() {
		String text = CommandController.getInstance().getCommandHistory().next();
		return text;
	}
	public String getCommandHistoryPrev() {
		String text = CommandController.getInstance().getCommandHistory().prev();
		return text;
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

	/**
	 * @return The primary stage (window)
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
