package app.view;

import java.time.format.DateTimeFormatter;

import app.model.Task;
import app.model.TaskCell;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class TaskListItemViewManager extends ListCell<TaskCell> {

	private Task task;
	private int index;

	@FXML
	private AnchorPane taskListItemViewLayout;
	@FXML
	private Label taskId;
	@FXML
	private Label taskName;
	@FXML
	private Label taskTopDate;
	@FXML
	private Label taskBottomDate;
	@FXML
	private CheckBox taskCheckbox;
	@FXML
	private Tooltip taskNameTooltip;
	@FXML
	private ImageView priorityImage;

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
			if (taskCell.getTask() != null) {
				this.task = taskCell.getTask();
				this.index = taskCell.getIndex();
				setGraphic(taskListItemViewLayout);
				setLabels();
				setPriority();
			} else {
				Label label = buildDateLabel(taskCell.getLabel());
				setGraphic(label);
			}
		}
	}
	
	private Label buildDateLabel(String labelText) {
		Label label = new Label(labelText);
		label.setMaxWidth(Double.MAX_VALUE);
		label.getStyleClass().addAll("taskItem", "taskDateLabel");
		return label;
	}

	/**
	 * Resets all cell content.
	 */
	private void clearContent() {
		setGraphic(null);
		taskTopDate.setText("");
		taskBottomDate.setText("");
		taskListItemViewLayout.getStyleClass().removeAll("priorityHigh", "priorityMedium", "priorityLow");
		taskListItemViewLayout.getStyleClass().remove("completed");
		priorityImage.setVisible(false);
		taskCheckbox.setSelected(false);
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mma");
		if (task.getStartDate() != null && task.getEndDate() != null) {
			taskTopDate.setText("From " + formatter.format(task.getStartDate()));
			taskBottomDate.setText("To " + formatter.format(task.getEndDate()));
		} else if (task.getEndDate() != null) {
			taskTopDate.setText("Due " + formatter.format(task.getEndDate()));
		}
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
