package app.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import app.model.Task;
import app.model.TaskCell;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TaskListItemViewManager extends ListCell<TaskCell> {

	private Task task;
	private int index;
	private String style;

	@FXML
	private AnchorPane taskListItemViewLayout;
	@FXML
	private Label taskId;
	@FXML
	private Label taskName;
	@FXML
	private CheckBox taskCheckbox;
	@FXML
	private Tooltip taskNameTooltip;
	@FXML
	private ImageView priorityImage;
	@FXML
	private VBox taskItemDateVbox;

	/**
	 * This method is implicitly called whenever a item (TaskListItemView) is
	 * added to the ListView (TaskListView).
	 * 
	 * The components of the item is updated with the supplied Task parameter.
	 */
	@Override
	public void updateItem(TaskCell taskCell, boolean empty) {
		super.updateItem(taskCell, empty);
		// Always clear the content due to a JavaFX quirk with updating cells.
		clearContent();
		if (!empty && taskCell != null) {
			style = taskCell.getStyle();
			if (taskCell.getTask() != null) {
				task = taskCell.getTask();
				index = taskCell.getIndex();
				setGraphic(taskListItemViewLayout);
				setStyle();
				setLabels();
				setPriority();
			} else {
				Label label = buildDateHeader(taskCell.getLabel());
				setGraphic(label);
			}
		}
	}

	private void setStyle() {
		taskListItemViewLayout.getStyleClass().add(style);
	}

	private Label buildDateHeader(String labelText) {
		Label label = new Label(labelText);
		label.setMaxWidth(Double.MAX_VALUE);
		label.getStyleClass().addAll("taskItem", "taskDateLabel", style);
		return label;
	}

	/**
	 * Resets all cell content.
	 */
	private void clearContent() {
		setGraphic(null);
		taskItemDateVbox.getChildren().clear();
		priorityImage.setVisible(false);
		taskCheckbox.setSelected(false);
		clearStyleClasses();
	}

	private void clearStyleClasses() {
		taskListItemViewLayout.getStyleClass().clear();
		taskListItemViewLayout.getStyleClass().add("taskItem");
	}

	/**
	 * Sets the item labels using the supplied Task.
	 */
	private void setLabels() {
		setIndex();
		setContent();
		setDates();
		setCompleted();
	}

	/**
	 * Sets strike-through effect for completed task
	 */
	private void setCompleted() {
		if (this.task.isCompleted()) {
			taskListItemViewLayout.getStyleClass().add("completed");
		} else if (!this.task.isCompleted()) {
			taskListItemViewLayout.getStyleClass().remove("completed");
		}
	}

	/**
	 * Sets the 1-index of this task cell.
	 */
	private void setIndex() {
		taskId.setText(String.valueOf(index));
	}

	/**
	 * Sets the main content of this task cell as well as its tooltip.
	 */
	private void setContent() {
		taskName.setText(task.getName());
		taskNameTooltip.setText(task.getName());
	}

	/**
	 * Sets the dates of this task cell.
	 */
	public void setDates() {
		// Duration
		if (task.getStartDate() != null && task.getEndDate() != null) {
			Label startTime = buildTimeLabel(task.getStartDate());
			Label to = new Label("to");
			Label endTime = buildTimeLabel(task.getEndDate());
			Label endDate = null;

			// if endDate is on another day, display the endDate in addition to
			// the default startTime and endTime
			if (task.getEndDate().toLocalDate() != task.getStartDate().toLocalDate()) {
				endDate = buildDateLabel(task.getEndDate());
				addDateRow(startTime);
				addDateRow(to, endDate, endTime);
			} else {
				addDateRow(startTime, to, endTime);
			}

			// Deadline
		} else if (task.getEndDate() != null) {
			Label to = new Label("by");
			Label endTime = buildTimeLabel(task.getEndDate());
			addDateRow(to, endTime);
		}
	}

	/**
	 * Adds a row to the date section of the task item
	 * 
	 * @param labels The labels the row should contain
	 */
	private void addDateRow(Label... labels) {
		HBox row = new HBox();
		row.setAlignment(Pos.CENTER_RIGHT);
		row.setSpacing(4);
		for (Label label : labels) {
			if (label != null) {
				row.getChildren().add(label);
			}
		}
		taskItemDateVbox.getChildren().add(row);
	}

	/**
	 * Builds the label representing the date
	 * 
	 * @param dateTime LocalDateTime to get the date from
	 * @return The built label
	 */
	private Label buildDateLabel(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
		Label label = new Label(dateTime.toLocalDate().format(formatter));
		label.setAlignment(Pos.CENTER_RIGHT);
		label.getStyleClass().add("taskItemDate");
		return label;
	}

	/**
	 * Builds the label representing the time
	 * 
	 * @param dateTime LocalDateTime to get the time from
	 * @return The built label
	 */
	private Label buildTimeLabel(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
		Label label = new Label(dateTime.toLocalTime().format(formatter));
		label.setAlignment(Pos.CENTER_RIGHT);
		label.getStyleClass().add("taskItemTime");
		return label;
	}

	/**
	 * Sets the item to display the appropriate priority level, if applicable.
	 */
	private void setPriority() {
		if (task.getPriority() != null) {
			switch (task.getPriority()) {
			case HIGH:
				setPriorityImage("priorityHigh");
				return;
			case MEDIUM:
				setPriorityImage("priorityMedium");
				return;
			case LOW:
				setPriorityImage("priorityLow");
				return;
			case NONE: // Intentional fall-through and default case
			default:
				return;
			}
		}
	}

	/**
	 * Displays the priority image by setting the style class and making the
	 * image visible.
	 * 
	 * @param styleClass A style class of the form: .priority[High | Medium |
	 *            Low]
	 */
	private void setPriorityImage(String styleClass) {
		taskListItemViewLayout.getStyleClass().add(styleClass);
		priorityImage.setVisible(true);
	}

}
