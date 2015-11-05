package app.model;

//@@author generated
public class ParserToken {
	private int start = -1;
	private int end = -1;

	public boolean isEmpty() {
		return (start == -1 || end == -1);
	}

	public void clear() {
		start = end = -1;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
}
