package app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskCell {
	private Task task;
	private LocalDate labelDate;
	private int index;

	private DateTimeFormatter dateFormatter;
	private static final String DATE_PATTERN = "d MMMM ''yy";

	public TaskCell() {
		dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
	}

	public TaskCell(LocalDate labelDate) {
		this();
		this.labelDate = labelDate;
	}

	public TaskCell(Task task, int index) {
		this();
		this.task = task;
		this.index = index;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public LocalDate getLabelDate() {
		return labelDate;
	}

	public void setLabelDate(LocalDate labelDate) {
		this.labelDate = labelDate;
	}

	public String getLabel() {
		String relativeText = "";
		if (LocalDate.now().isEqual(labelDate)) {
			relativeText = " (Today)";
		} else if (LocalDate.now().plusDays(1).isEqual(labelDate)) {
			relativeText = " (Tomorrow)";
		}
		String label = labelDate.format(dateFormatter) + relativeText;
		return label;
	}

}
