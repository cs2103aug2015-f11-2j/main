package app.view;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import app.constants.HelpConstants;
import app.constants.CommandConstants.CommandType;
import app.constants.TaskConstants.Priority;
import app.constants.TaskConstants.RemovableField;
import app.logic.command.Command;
import app.logic.command.CommandEdit;
import app.util.Common;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

//@@author A0126120B
public class InfoViewManager {

	private static final String STYLE_INFOVIEW_DESCRIPTION = "infoDescription";
	private static final String STYLE_INFOVIEW_COMMAND = "infoCommand";
	private static final String STYLE_INFOVIEW_REQUIRED = "infoRequired";
	private static final String STYLE_INFOVIEW_OPTIONAL = "infoOptional";
	private static final String STYLE_INFOVIEW_CONTENT = "infoContent";
	private static final String STYLE_INFOVIEW_DATE = "infoDate";
	private static final String STYLE_INFOVIEW_PRIORITY = "infoPriority";

	private ViewManager viewManager;
	private DateTimeFormatter dateFormatter;
	private CommandType currentCommandType;

	@FXML
	private VBox infoViewLayout;

	/**
	 * This method is implicitly called when loaded from the FXMLLoader. A
	 * listener is bound to the height property of the layout and updates the size
	 * of the window accordingly.
	 */
	@FXML
	public void initialize() {
		dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mma");
		currentCommandType = CommandType.INVALID;

		infoViewLayout.heightProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			double difference = newValue.doubleValue() - oldValue.doubleValue();
			viewManager.getPrimaryStage().setHeight(viewManager.getPrimaryStage().getHeight() + difference);
		});
	}

	/**
	 * Updates the info view
	 * 
	 * @param cmd The command object to specify info for
	 */
	public void updateView(Command cmd) {
		CommandType type = cmd.getCommandType();
		// Don't refresh info view if input is a ^substring of an alias for the currently displayed
		// command type.
		if (type == CommandType.INVALID && !cmd.getCommandString().isEmpty()) {
			List<String> aliases = Common.getAliasesForCommandType(currentCommandType);
			for (String alias : aliases) {
				if (alias.startsWith(cmd.getCommandString())) {
					return;
				}
			}
		}
		
		clearView();
		currentCommandType = cmd.getCommandType();

		switch (cmd.getCommandType()) {
		case ADD:
			setHelpText(HelpConstants.HELP_ADD_OVERVIEW, HelpConstants.HELP_ADD_DESCRIPTION);
			setCommandLabels(cmd);
			break;
		case EDIT:
			setHelpText(HelpConstants.HELP_EDIT_OVERVIEW, HelpConstants.HELP_EDIT_DESCRIPTION);
			setCommandEditLabels(cmd);
			break;
		case SEARCH:
			setHelpText(HelpConstants.HELP_SEARCH_OVERVIEW, HelpConstants.HELP_SEARCH_DESCRIPTION);
			setCommandLabels(cmd);
			break;
		case DELETE:
			setHelpText(HelpConstants.HELP_DELETE_OVERVIEW, HelpConstants.HELP_DELETE_DESCRIPTION);
			break;
		case DISPLAY:
			setHelpText(HelpConstants.HELP_DISPLAY_OVERVIEW, HelpConstants.HELP_DISPLAY_DESCRIPTION);
			break;
		case MARK:
			setHelpText(HelpConstants.HELP_MARK_OVERVIEW, HelpConstants.HELP_MARK_DESCRIPTION);
			break;
		case THEME:
			setHelpText(HelpConstants.HELP_THEME_OVERVIEW, HelpConstants.HELP_THEME_DESCRIPTION);
			break;
		case EXIT:
			setHelpText(HelpConstants.HELP_EXIT_OVERVIEW, HelpConstants.HELP_EXIT_DESCRIPTION);
			break;
		case SAVE:
			setHelpText(HelpConstants.HELP_SAVE_OVERVIEW, HelpConstants.HELP_SAVE_DESCRIPTION);
			break;
		case HELP:
			setHelpText(HelpConstants.HELP_HELP_OVERVIEW, HelpConstants.HELP_HELP_DESCRIPTION);
			break;
		case UNDO:
			setHelpText(HelpConstants.HELP_UNDO_OVERVIEW, HelpConstants.HELP_UNDO_DESCRIPTION);
			break;
		default:
			break;
		}

		setPaddingIfHasChildren();
	}

	/**
	 * Sets the help text in the info view.
	 * 
	 * @param overview The command overview
	 * @param description The command description
	 */
	private void setHelpText(String overview, String description) {
		setHelpOverview(overview);
		setHelpDescription(description);
	}

	/**
	 * Sets the command overview in the info view.
	 * 
	 * @param overview The command overview
	 */
	private void setHelpOverview(String overview) {
		String commandWord = Common.getFirstWord(overview);
		overview = Common.removeFirstWord(overview);

		String optionalParams = overview;
		String requiredParams = "";

		while (!optionalParams.startsWith("[") && !optionalParams.isEmpty()) {
			requiredParams += Common.getFirstWord(optionalParams) + " ";
			optionalParams = Common.removeFirstWord(optionalParams);
		}

		Text commandWordText = buildText(commandWord, STYLE_INFOVIEW_COMMAND);
		Text requiredParamsText = buildText(" " + requiredParams.trim(), STYLE_INFOVIEW_REQUIRED);
		Text optionalParamsText = buildText(" " + optionalParams.trim(), STYLE_INFOVIEW_OPTIONAL);
		ArrayList<Text> texts = new ArrayList<Text>();
		addTextsToList(texts, commandWordText, requiredParamsText, optionalParamsText);
		addInfoRow(texts);
	}

	/**
	 * Sets the command description in the info view.
	 * 
	 * @param description The command overview
	 */
	private void setHelpDescription(String description) {
		ArrayList<Text> texts = new ArrayList<Text>();
		texts.add(buildText(description, STYLE_INFOVIEW_DESCRIPTION));
		addInfoRow(texts);
	}

	/**
	 * Sets labels specific to the edit command.
	 * 
	 * @param cmd The edit command
	 */
	private void setCommandEditLabels(Command cmd) {
		CommandEdit editCmd = (CommandEdit) cmd;
		Integer id = editCmd.getDisplayId();
		if (id != null) {
			setEditText(id.toString());
		}
		setContentText(cmd.getContent());
		setDateAndPriorityTexts(cmd);
		addSeparator();
	}

	/**
	 * Sets labels for the specified command object. This method is command-agnostic.
	 * 
	 * @param cmd The command object
	 */
	private void setCommandLabels(Command cmd) {
		setContentText(cmd.getContent());
		setDateAndPriorityTexts(cmd);
		addSeparator();
	}
	
	/**
	 * Sets the edit text for the edit command.
	 * 
	 * @param id ID of the task to be edited.
	 */
	private void setEditText(String id) {
		ArrayList<Text> texts = new ArrayList<Text>();
		if (!id.isEmpty()) {
			Text editingTaskId = buildText("Editing task with ID: ");
			Text idText = buildText(id);
			addTextsToList(texts, editingTaskId, idText);
			addInfoRow(texts);
		}
	}

	/**
	 * Adds a separator between the command overview/description and the rest of the command labels.
	 */
	private void addSeparator() {
		Separator separator = new Separator();
		Insets padding = new Insets(5, 0, 2, 0);
		separator.setPadding(padding);
		if (infoViewLayout.getChildren().size() > 2) {
			infoViewLayout.getChildren().add(2, separator);
		}
	}

	/**
	 * Sets the content text for the command.
	 * 
	 * @param content The content of the command
	 */
	private void setContentText(String content) {
		ArrayList<Text> texts = new ArrayList<Text>();
		if (content != null && !content.isEmpty()) {
			Text startQuote = buildText("\"");
			Text endQuote = buildText("\"");
			Text contentString = buildText(content, STYLE_INFOVIEW_CONTENT);
			addTextsToList(texts, startQuote, contentString, endQuote);
			addInfoRow(texts);
		}
	}

	/**
	 * Sets the labels for parsed date and priority level of the specified command.
	 * 
	 * @param cmd The command to set date and priority labels for
	 */
	private void setDateAndPriorityTexts(Command cmd) {
		ArrayList<Text> texts = new ArrayList<Text>();

		// Add parsed dates
		if (cmd.getRemoveField().contains(RemovableField.DATE)) {
			Text withDate = buildText(" with date ");
			Text dateNone = buildText("NONE", STYLE_INFOVIEW_DATE);
			addTextsToList(texts, withDate, dateNone);
		} else if (cmd.getStartDate() == null && cmd.getEndDate() != null) {
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
		if (cmd.getPriority() != null
				&& (cmd.getRemoveField().contains(RemovableField.PRIORITY) || cmd.getPriority() != Priority.NONE)) {
			Text withPriority = buildText(" with priority ");
			Text priorityLevel = buildText(cmd.getPriority().toString(), STYLE_INFOVIEW_PRIORITY);
			addTextsToList(texts, withPriority, priorityLevel);
		}

		if (!texts.isEmpty()) {
			addInfoRow(texts);
		}
	}

	/**
	 * Utility method to add a series of text objects to a list.
	 * 
	 * @param list The list to add to
	 * @param texts The texts
	 */
	private void addTextsToList(List<Text> list, Text... texts) {
		for (Text text : texts) {
			list.add(text);
		}
	}

	/**
	 * Clears the info view.
	 */
	public void clearView() {
		infoViewLayout.getChildren().clear();
	}

	/**
	 * Builds and adds a row in the info view.
	 * 
	 * @param list A list of Text objects to construct the row with
	 */
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

	/**
	 * Builds a Text object from a String object.
	 * @param content The content of the text object
	 * @return A Text object
	 */
	private Text buildText(String content) {
		return buildText(content, null);
	}

	/**
	 * Builds a Text object from a String object. A style class can be specified.
	 * @param content The content of the text object
	 * @return A Text object
	 */
	private Text buildText(String content, String styleClass) {
		Text text = new Text(content);
		text.getStyleClass().add("text");
		if (styleClass != null) {
			text.getStyleClass().add(styleClass);
		}
		return text;
	}

	/**
	 * Sets the padding of the info view if not empty.
	 */
	private void setPaddingIfHasChildren() {
		Insets padding = new Insets(0);
		if (!infoViewLayout.getChildren().isEmpty()) {
			padding = new Insets(5);
		}
		infoViewLayout.setPadding(padding);
	}

	public CommandType getCurrentCommandType() {
		return currentCommandType;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
}
