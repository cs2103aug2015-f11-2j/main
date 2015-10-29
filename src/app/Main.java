package app;

import java.io.IOException;
import java.util.List;

import app.storage.AppStorage;
import app.util.LogHelper;
import app.view.ViewManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private ViewManager viewManager;
	
	private final int MIN_WIDTH = 600;
	private final int MIN_HEIGHT = 300;

	@Override
	public void start(Stage stage) {
		AppStorage.getInstance(); // initialize config/storage/log files
		initializeStage(stage);
		initializeViewManager();
		
		// Execute commands passed to the program
		List<String> args = getParameters().getRaw();
		for (String command : args) {
			viewManager.sendCommandToInput(command);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initializes the ViewManager
	 */
	private void initializeViewManager() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/fxml/RootView.fxml"));
			BorderPane rootLayout = loader.load();
			viewManager = loader.getController();
			viewManager.initialize(primaryStage, rootLayout);
		} catch (IOException e) {
			LogHelper.getInstance().getLogger().severe(e.getMessage());
		}
	}

	/**
	 * Initializes the stage (window) parameters, such as: title, minimum
	 * width/height.
	 * 
	 * @param stage The stage (window) to set parameters for.
	 */
	private void initializeStage(Stage stage) {
		primaryStage = stage;
		primaryStage.setTitle("Next :: your next-gen todo list");
		primaryStage.setMinWidth(MIN_WIDTH);
		primaryStage.setMinHeight(MIN_HEIGHT);
	}
}
