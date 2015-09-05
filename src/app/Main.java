package app;

import app.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		ViewManager viewManager = new ViewManager();
		viewManager.setMainApp(this);
		viewManager.initializeViews(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
