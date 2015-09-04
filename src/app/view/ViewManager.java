package app.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.Main;
import app.helper.LogHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewManager {

	private Main mainApp;
	private AnchorPane rootLayout;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	public void initializeRootView(Stage primaryStage) {
		LogHelper.info("Initializing root view");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootView.fxml"));
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

	public void onKeypressEnter() {
		LogHelper.info("User pressed enter key");
		// TODO on user input
	}
}
