package app;

import app.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	private Stage primaryStage;
	
	@Override
	public void start(Stage stage) {
		initializeStage(stage);
		ViewManager viewManager = new ViewManager();
		viewManager.initialize(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private void initializeStage(Stage stage) {
		primaryStage = stage;
		primaryStage.setTitle("Next :: your next-gen todo list");
	}
}
