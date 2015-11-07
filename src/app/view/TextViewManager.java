package app.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

//@@author A0126120B
public class TextViewManager {
	
	@FXML
	private TextArea textArea;
	
	public void setText(String text) {
		textArea.setText(text);
	}
}
