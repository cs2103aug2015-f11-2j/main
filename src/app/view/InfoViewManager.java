package app.view;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import app.constants.HelpConstants;
import app.constants.TaskConstants.Priority;
import app.helper.CommandParser;
import app.model.command.Command;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class InfoViewManager {

	private static final String STYLE_INFOVIEW_DESCRIPTION = "infoDescription";
	private static final String STYLE_INFOVIEW_COMMAND = "infoCommand";
	private static final String STYLE_INFOVIEW_REQUIRED = "infoRequired";
	private static final String STYLE_INFOVIEW_OPTIONAL = "infoOptional";
	private static final String STYLE_INFOVIEW_CONTENT = "infoContent";
	private static final String STYLE_INFOVIEW_DATE = "infoDate";
	private static final String STYLE_INFOVIEW_PRIORITY = "infoPriority";

	private ViewManager viewManager;
	DateTimeFormatter dateFormatter;

	@FXML
	private VBox infoViewLayout;

	@FXML
	public void initialize() {
		dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mma");

		infoViewLayout.heightProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			double difference = newValue.doubleValue() - oldValue.doubleValue();
			viewManager.getPrimaryStage().setHeight(viewManager.getPrimaryStage().getHeight() + difference);
		});
	}

	public void updateView(Command cmd) {
		clearView();

		switch (cmd.getCommandType()) {
		case ADD:
			setHelpText(HelpConstants.HELP_ADD_OVERVIEW, HelpConstants.HELP_ADD_DESCRIPTION);
			setCommandParamLabels(cmd);
		default:
			break;
		}

		setPaddingIfHasChildren();
	}

	private void setHelpText(String overview, String description) {
		setHelpOverview(overview);
		setHelpDescription(description);
	}

	private void setHelpOverview(String overview) {
		String commandWord = CommandParser.getFirstWord(overview);
		overview = CommandParser.removeFirstWord(overview);

		String optionalParams = overview;
		String requiredParams = "";

		while (!optionalParams.startsWith("[") && !optionalParams.isEmpty()) {
			requiredParams += CommandParser.getFirstWord(optionalParams) + " ";
			optionalParams = CommandParser.removeFirstWord(optionalParams);
		}

		Text commandWordText = buildText(commandWord, STYLE_INFOVIEW_COMMAND);
		Text requiredParamsText = buildText(" " + requiredParams.trim(), STYLE_INFOVIEW_REQUIRED);
		Text optionalParamsText = buildText(" " + optionalParams.trim(), STYLE_INFOVIEW_OPTIONAL);
		ArrayList<Text> texts = new ArrayList<Text>();
		addTextsToList(texts, commandWordText, requiredParamsText, optionalParamsText);
		addInfoRow(texts);
	}

	private void setHelpDescription(String description) {
		ArrayList<Text> texts = new ArrayList<Text>();
		texts.add(buildText(description, STYLE_INFOVIEW_DESCRIPTION));
		addInfoRow(texts);
	}

	private void setCommandParamLabels(Command cmd) {
		setContentText(cmd);
		setDateAndPriorityTexts(cmd);
		addSeparator();
	}

	private void addSeparator() {
		Separator separator = new Separator();
		Insets padding = new Insets(5, 0, 2, 0);
		separator.setPadding(padding);
		if (infoViewLayout.getChildren().size() > 2) {
			infoViewLayout.getChildren().add(2, separator);
		}
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
			Text endDate = buildText(dateFormatter.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
			addTextsToList(texts, due, endDate);
		} else if (cmd.getStartDate() != null && cmd.getEndDate() != null) {
			Text from = buildText(" from ");
			Text startDate = buildText(dateFormatter.format(cmd.getStartDate()), STYLE_INFOVIEW_DATE);
			Text to = buildText(" to ");
			Text endDate = buildText(dateFormatter.format(cmd.getEndDate()), STYLE_INFOVIEW_DATE);
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
