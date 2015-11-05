package app.view;

import app.constants.ViewConstants.ScrollDirection;
import app.logic.CommandController;
import app.logic.command.Command;
import app.model.ViewState;
import app.util.LogHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * This is the view manager for the command input field.
 * 
 * Listeners for user input (eg. ENTER, UP, DOWN) will be defined here.
 */
//@@author A0126120B
public class InputViewManager {

	private ViewManager viewManager;

	@FXML
	private TextField commandInput;

	/**
	 * This method is implicitly called when loaded from the FXMLLoader. A
	 * listener is bound to the text property of the input field and executed
	 * whenever the property changes.
	 */
	@FXML
	public void initialize() {
		commandInput.textProperty().addListener(change -> updateInfoView());
		
		commandInput.setOnKeyPressed(event -> {
			KeyCombination scrollUp = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);
			KeyCombination scrollDown = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN);
			if (scrollUp.match(event)) {
				viewManager.scrollTaskList(ScrollDirection.UP);
			} else if (scrollDown.match(event)) {
				viewManager.scrollTaskList(ScrollDirection.DOWN);
			} else if (event.getCode() == KeyCode.UP) {
				prevCommandFromHistory();
			} else if (event.getCode() == KeyCode.DOWN) {
				nextCommandFromHistory();
			}
		});
	}
	
	private void nextCommandFromHistory() {
		String text = CommandController.getInstance().getCommandHistory().next();
		setText(text, true);
	}
	
	private void prevCommandFromHistory() {
		String text = CommandController.getInstance().getCommandHistory().prev();
		setText(text, false);
	}
	
	private void setText(String text, boolean allowEmpty) {
		if (allowEmpty || !text.isEmpty()) {
			commandInput.setText(text);
		}		
		Platform.runLater(() -> {
			commandInput.positionCaret(commandInput.getLength());
		});
	}

	/**
	 * Updates the view showing information about the current input
	 */
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
		String input = commandInput.getText();
		LogHelper.getInstance().getLogger().info("User pressed enter key with input: " + input);
		executeUserInput(input);
	}
	
	public void executeUserInput(String input) {
		ViewState newViewState = CommandController.getInstance().executeCommand(input);
		viewManager.updateView(newViewState);
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
