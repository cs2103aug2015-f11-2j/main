package app.view;

import java.util.List;

import app.constants.StorageConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.ViewConstants.ScrollDirection;
import app.util.Common;
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
			} else if (event.getCode() == KeyCode.SPACE) {
				completeCommand();
			}
		});
	}
	
	private void completeCommand() {
		String text = commandInput.getText();
		if (Common.getFirstWord(text).equals(text.trim())) {
			CommandType type = viewManager.getInfoViewCurrentCommandType();
			List<String> aliases = Common.getAliasesForCommandType(type);
			if (!aliases.isEmpty() && !aliases.contains(text)) {
				setText(text.replaceFirst(Common.getFirstWord(text), aliases.get(0) + " "), true);
				positionCaretAtEnd();
			}
		}
	}
	
	private void nextCommandFromHistory() {
		String text = viewManager.getCommandHistoryNext();
		setText(text, true);
		positionCaretAtEnd();
	}
	
	private void prevCommandFromHistory() {
		String text = viewManager.getCommandHistoryPrev();
		setText(text, false);
		positionCaretAtEnd();
	}
	
	private void setText(String text, boolean allowEmpty) {
		if (allowEmpty || !text.isEmpty()) {
			Platform.runLater(() -> {
				commandInput.setText(text);
			});
		}		
	}
	
	private void positionCaretAtEnd() {
		Platform.runLater(() -> {
			commandInput.positionCaret(commandInput.getLength());
		});
	}

	/**
	 * Updates the view showing information about the current input
	 */
	private void updateInfoView() {
		String commandString = commandInput.getText();
		viewManager.updateInfoView(commandString);
	}

	/**
	 * This method will be executed when the user presses the ENTER key on the
	 * command input field.
	 */
	public void onKeypressEnter() {
		String input = commandInput.getText();
		LogHelper.getInstance().getLogger().info(String.format(StorageConstants.LOG_USER_INPUT, input));
		viewManager.executeUserInput(input);
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
