package app.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class TextViewManager {
	private ViewManager viewManager;
	
	@FXML
	private TextArea textArea;
	
	public void setText(String text) {
		textArea.setText(text);
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
}
