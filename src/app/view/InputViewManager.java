package app.view;

import app.controller.CommandController;
import app.helper.LogHelper;
import app.model.command.Command;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * This is the view manager for the command input field.
 * 
 * Listeners for user input (eg. ENTER, UP, DOWN) will be defined here.
 */
public class InputViewManager {

	private ViewManager viewManager;

	@FXML
	private TextField commandInput;
	
	@FXML
	public void initialize() {
		commandInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {	
				updateInfoView();
			}
		});
	}
	
	private void updateInfoView() {
		String commandString = commandInput.getText();
		Command cmd = CommandController.getInstance().createCommand(commandString);
		if (cmd != null) {
			viewManager.updateInfoView(cmd);
		}
	}

	/**
	 * This method will be executed when the user presses the ENTER key on the
	 * command input field.
	 */
	public void onKeypressEnter() {
		String commandString = commandInput.getText();
		LogHelper.getLogger().info("User pressed enter key with input: " + commandString);
		CommandController.getInstance().executeCommand(commandString);
		commandInput.clear();
	}

	/**
	 * This sets a reference to the ViewManager that initialized this class.
	 * 
	 * @param viewManager The ViewManager that initialized this class.
	 */
	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
}
