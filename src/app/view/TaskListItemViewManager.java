package app.view;

import java.text.SimpleDateFormat;

import app.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class TaskListItemViewManager extends ListCell<Task> {

	private Task task;

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
	public void updateItem(Task task, boolean empty) {
		super.updateItem(task, empty);
		this.task = task;
		// Always clear the content due to a JavaFX quirk with updating cells.
		clearContent();
		if (!empty && task != null) {
			setGraphic(taskListItemViewLayout);
			setLabels();
			setPriority();
		}
	}

	/**
	 * Resets all cell content.
	 */
	private void clearContent() {
		setGraphic(null);
		taskListItemViewLayout.getStyleClass().removeAll("priorityHigh", "priorityMedium", "priorityLow");
		priorityImage.setVisible(false);
		taskCheckbox.setSelected(false);
	}

	/**
	 * Sets the item labels using the supplied Task.
	 */
	private void setLabels() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mma");
		// TODO: placeholder ID
		taskId.setText("#10");
		taskName.setText(task.getName());
		taskNameTooltip.setText(task.getName());
		
		if (task.getStartDate() != null && task.getEndDate() != null) {
			taskTopDate.setText("From " + dateFormat.format(task.getStartDate()));
			taskBottomDate.setText("To " + dateFormat.format(task.getEndDate()));
		} else if (task.getEndDate() != null) {
			taskTopDate.setText("Due " + dateFormat.format(task.getEndDate()));
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
			case NONE:	// Intentional fall-through and default case
			default:
				return;
			}
		}
	}

	/**
	 * Displays the priority image by setting the style class and making the
	 * image visible.
	 * 
	 * @param styleClass A style class of the form: .priority[High | Medium | Low]
	 */
	private void setPriorityImage(String styleClass) {
		taskListItemViewLayout.getStyleClass().add(styleClass);
		priorityImage.setVisible(true);
	}

}
