package app.view;

import app.helper.LogHelper;
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

	/**
	 * This method will be executed when the user presses the ENTER key on the
	 * command input field.
	 */
	public void onKeypressEnter() {
		String commandString = commandInput.getText();
		LogHelper.info("User pressed enter key with input: " + commandString);
		// TODO: this is just test code
		viewManager.updateTaskList(commandString);
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
