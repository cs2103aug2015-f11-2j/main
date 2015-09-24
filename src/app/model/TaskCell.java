package app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskCell {
	private Task task;
	private LocalDate labelDate;
	private int index;
	private String style;

	private DateTimeFormatter dateFormatter;
	private static final String DATE_PATTERN = "d MMMM ''yy";

	public TaskCell() {
		dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
	}

	public TaskCell(LocalDate labelDate, String style) {
		this();
		this.labelDate = labelDate;
		this.style = style;
	}

	public TaskCell(Task task, int index, String style) {
		this();
		this.task = task;
		this.index = index;
		this.style = style;
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

	public String getStyle() {
		if (style == null) {
			return "";
		}
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
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
