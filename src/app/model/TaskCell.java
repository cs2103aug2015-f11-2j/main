package app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskCell {
	private Task task;
	private LocalDate labelDate;
	private int index;
	private String style;

	private static final String DATE_PATTERN = "d MMMM";
	private static final String DATE_PATTERN_WITH_YEAR = "d MMMM yyyy";

	public TaskCell(LocalDate labelDate, String style) {
		this.labelDate = labelDate;
		this.style = style;
	}

	public TaskCell(Task task, int index, String style) {
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
		// if date is today or tomorrow, append the relevant string
		String relativeText = "";
		if (LocalDate.now().isEqual(labelDate)) {
			relativeText = " (Today)";
		} else if (LocalDate.now().plusDays(1).isEqual(labelDate)) {
			relativeText = " (Tomorrow)";
		}
		
		// If date.year == current year, omit the year from the label
		DateTimeFormatter dateFormatter = null;
		if (labelDate.getYear() == LocalDate.now().getYear()) {
			dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
		} else {
			dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN_WITH_YEAR);
		}
		
		String label = labelDate.format(dateFormatter) + relativeText;
		return label;
	}

}
