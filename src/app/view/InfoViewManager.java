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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
		clearView();

		switch (cmd.getCommandType()) {
		case ADD:
			setCommandParamLabels(cmd);
		default:
			break;
		}

		setPaddingIfHasChildren();
	}

	private void setCommandParamLabels(Command cmd) {
		setContentText(cmd);
		setDateAndPriorityTexts(cmd);
	}
	
	private void setContentText(Command cmd) {
		ArrayList<Text> texts = new ArrayList<Text>();
		if (cmd.getContent() != null && !cmd.getContent().isEmpty()) {
			Text startQuote = buildText("\"");
			Text endQuote = buildText("\"");
			Text content = buildText(cmd.getContent(), STYLE_INFOVIEW_CONTENT);
			addTextsToList(texts, startQuote, content, endQuote);
			addInfoRow(texts);
		}
	}
	
	private void setDateAndPriorityTexts(Command cmd) {
		if (cmd.getContent().isEmpty()) {
			return;
		}
		ArrayList<Text> texts = new ArrayList<Text>();

		// Add parsed dates
		if (cmd.getStartDate() == null && cmd.getEndDate() != null) {
			Text due = buildText(" due ");
			Text endDate = buildText(dateFormat.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
			addTextsToList(texts, due, endDate);
		} else if (cmd.getStartDate() != null && cmd.getEndDate() != null) {
			Text from = buildText(" from ");
			Text startDate = buildText(dateFormat.format(cmd.getStartDate()), STYLE_INFOVIEW_DATE);
			Text to = buildText(" to ");
			Text endDate = buildText(dateFormat.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
			addTextsToList(texts, from, startDate, to, endDate);
		}

		// Add parsed priority
		if (cmd.getPriority() != null && cmd.getPriority() != Priority.NONE) {
			Text withPriority = buildText(" with priority ");
			Text priorityLevel = buildText(cmd.getPriority().toString(), STYLE_INFOVIEW_PRIORITY);
			addTextsToList(texts, withPriority, priorityLevel);
		}
		
		if (!texts.isEmpty()) {
			addInfoRow(texts);
		}
	}
	
	private void addTextsToList(List<Text> list, Text... texts) {
		for (Text text : texts) {
			list.add(text);
		}
	}
	
	public void clearView() {
		infoViewLayout.getChildren().clear();
	}

	private void addInfoRow(List<Text> list) {
		TextFlow textFlow = new TextFlow();
		Insets padding = new Insets(0, 5, 0, 5);
		textFlow.setPrefWidth(300);
		textFlow.setPadding(padding);
		for (Text text : list) {
			textFlow.getChildren().add(text);
		}
		infoViewLayout.getChildren().add(textFlow);
	}

	private Text buildText(String content) {
		return buildText(content, null);
	}

	private Text buildText(String content, String styleClass) {
		Text text = new Text(content);
		text.getStyleClass().add("text");
		if (styleClass != null) {
			text.getStyleClass().add(styleClass);
		}
		return text;
	}

	private void setPaddingIfHasChildren() {
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
