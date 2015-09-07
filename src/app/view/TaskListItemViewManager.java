package app.view;

import app.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
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

	/**
	 * This method is implicitly called whenever a item (TaskListItemView) is
	 * added to the ListView (TaskListView).
	 * 
	 * The components of the item is updated with the supplied Task parameter.
	 */
	@Override
	public void updateItem(Task task, boolean empty) {
		super.updateItem(task, empty);
		if (empty) {
			setGraphic(null);
		} else if (task != null) {
			this.task = task;
			setGraphic(taskListItemViewLayout);
			setLabels();
		}
	}

	/**
	 * Sets the item labels using the supplied Task.
	 */
	private void setLabels() {
		// TODO: placeholder ID
		taskId.setText("#10");
		taskName.setText(task.getName());
		taskNameTooltip.setText(task.getName());
		// TODO: placeholder data.
		taskTopDate.setText("From 10/11/2015");
		taskBottomDate.setText("To 15/11/2015 11:59pm");
		taskCheckbox.setSelected(false);
	}

}
