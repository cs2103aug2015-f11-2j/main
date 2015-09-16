package app.view;

import java.text.SimpleDateFormat;

import app.constants.TaskConstants.Priority;
import app.model.command.Command;
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
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
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
		// Add parsed task content
		if (cmd.getContent() != null && !cmd.getContent().isEmpty()) {
			addInfoRow(buildLabel(cmd.getContent(), STYLE_INFOVIEW_CONTENT));
		}

		// Add parsed dates
		if (cmd.getStartDate() == null && cmd.getEndDate() != null) {
			Label due = buildLabel("Due ");
			Label endDate = buildLabel(dateFormat.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
			addInfoRow(due, endDate);
		} else if (cmd.getStartDate() != null && cmd.getEndDate() != null) {
			Label from = buildLabel("From ");
			Label startDate = buildLabel(dateFormat.format(cmd.getStartDate()), STYLE_INFOVIEW_DATE);
			Label to = buildLabel(" to ");
			Label endDate = buildLabel(dateFormat.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
			addInfoRow(from, startDate, to, endDate);
		}

		// Add parsed priority
		if (cmd.getPriority() != null && cmd.getPriority() != Priority.NONE) {
			addInfoRow(buildLabel("Priority "), buildLabel(cmd.getPriority().toString(), STYLE_INFOVIEW_PRIORITY));
		}
	}

	private void addInfoRow(Label... args) {
		TextFlow textFlow = new TextFlow();
		Insets padding = new Insets(0, 5, 0, 5);
		textFlow.setPadding(padding);
		for (Label label : args) {
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

	public void clearLabels() {
		infoViewLayout.getChildren().clear();
	}

	private void setPaddingIfNoChildren() {
		Insets padding = new Insets(0);
		if (!infoViewLayout.getChildren().isEmpty()) {
			padding = new Insets(5);
		}
		infoViewLayout.setPadding(padding);
	}
}
