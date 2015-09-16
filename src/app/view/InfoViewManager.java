package app.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.constants.TaskConstants.Priority;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class InfoViewManager {
	private ViewManager viewManager;
	private SimpleDateFormat dateFormat;
	
	@FXML 
	VBox infoViewLayout;
	
	@FXML
	public void initialize() {
		dateFormat = new SimpleDateFormat("dd/MM/yy hh:mma");
	}
	
	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
	
	public void updateView(String content, Date start, Date end, Priority priority) {
		clearLabels();
		
		if (content != null && !content.isEmpty()) {
			Label label = new Label(content);
			infoViewLayout.getChildren().add(label);
		}
		if (start == null && end != null) {
			Label label = new Label(dateFormat.format(end));
			infoViewLayout.getChildren().add(label);
		} else if (start != null && end != null) {
			Label label = new Label(dateFormat.format(start));
			infoViewLayout.getChildren().add(label);
			label = new Label(dateFormat.format(end));
			infoViewLayout.getChildren().add(label);
		}
	}
	
	public void clearLabels() {
		infoViewLayout.getChildren().clear();
	}
}
