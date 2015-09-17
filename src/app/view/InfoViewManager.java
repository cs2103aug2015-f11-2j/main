package app.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import app.constants.TaskConstants.Priority;
import app.model.command.Command;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class InfoViewManager {

	private static final String STYLE_INFOVIEW_CONTENT = "infoContent";
	private static final String STYLE_INFOVIEW_DATE = "infoDate";
	private static final String STYLE_INFOVIEW_PRIORITY = "infoPriority";

	private ViewManager viewManager;
	private SimpleDateFormat dateFormat;

	@FXML
	private VBox infoViewLayout;

	@FXML
	public void initialize() {
		dateFormat = new SimpleDateFormat("dd/MM/yy hh:mma");

		// Modify the height of the window so that the info view appears to
		// extend from the bottom
		infoViewLayout.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double difference = newValue.doubleValue() - oldValue.doubleValue();
				viewManager.getPrimaryStage().setHeight(viewManager.getPrimaryStage().getHeight() + difference);
			}
		});
	}

	public void updateView(Command cmd) {
		clearLabels();

		switch (cmd.getCommandType()) {
		case ADD:
			setCommandParamLabels(cmd);
		default:
			break;
		}

		setPaddingIfNoChildren();
	}

	private void setCommandParamLabels(Command cmd) {
		setContentLabel(cmd);
		setDateAndPriorityLabels(cmd);
	}
	
	private void setContentLabel(Command cmd) {
		ArrayList<Label> labels = new ArrayList<Label>();
		if (cmd.getContent() != null && !cmd.getContent().isEmpty()) {
			Label startQuote = buildLabel("\"");
			Label endQuote = buildLabel("\"");
			Label content = buildLabel(cmd.getContent(), STYLE_INFOVIEW_CONTENT);
			addLabelsToList(labels, startQuote, content, endQuote);
			addInfoRow(labels);
		}
	}
	
	private void setDateAndPriorityLabels(Command cmd) {
		if (cmd.getContent().isEmpty()) {
			return;
		}
		ArrayList<Label> labels = new ArrayList<Label>();

		// Add parsed dates
		if (cmd.getStartDate() == null && cmd.getEndDate() != null) {
			Label due = buildLabel(" due ");
			Label endDate = buildLabel(dateFormat.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
			addLabelsToList(labels, due, endDate);
		} else if (cmd.getStartDate() != null && cmd.getEndDate() != null) {
			Label from = buildLabel(" from ");
			Label startDate = buildLabel(dateFormat.format(cmd.getStartDate()), STYLE_INFOVIEW_DATE);
			Label to = buildLabel(" to ");
			Label endDate = buildLabel(dateFormat.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
			addLabelsToList(labels, from, startDate, to, endDate);
		}

		// Add parsed priority
		if (cmd.getPriority() != null && cmd.getPriority() != Priority.NONE) {
			Label withPriority = buildLabel(" with priority ");
			Label priorityLevel = buildLabel(cmd.getPriority().toString(), STYLE_INFOVIEW_PRIORITY);
			addLabelsToList(labels, withPriority, priorityLevel);
		}
		
		if (!labels.isEmpty()) {
			addInfoRow(labels);
		}
	}
	
	private void addLabelsToList(List<Label> list, Label... labels) {
		for (Label label : labels) {
			list.add(label);
		}
	}
	
	public void clearLabels() {
		infoViewLayout.getChildren().clear();
	}

	private void addInfoRow(List<Label> labels) {
		TextFlow textFlow = new TextFlow();
		Insets padding = new Insets(0, 5, 0, 5);
		textFlow.setPadding(padding);
		for (Label label : labels) {
			textFlow.getChildren().add(label);
		}
		infoViewLayout.getChildren().add(textFlow);
	}

	private Label buildLabel(String content) {
		return buildLabel(content, null);
	}

	private Label buildLabel(String content, String styleClass) {
		Label label = new Label(content);
		if (styleClass != null) {
			label.getStyleClass().add(styleClass);
		}
		return label;
	}

	private void setPaddingIfNoChildren() {
		Insets padding = new Insets(0);
		if (!infoViewLayout.getChildren().isEmpty()) {
			padding = new Insets(5);
		}
		infoViewLayout.setPadding(padding);
	}
	
	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
}
