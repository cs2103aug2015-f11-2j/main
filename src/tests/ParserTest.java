package tests;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

import app.constants.CommandConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.controller.CommandController;
import app.helper.CommandParser;
import app.model.command.Command;

public class ParserTest {

	@Test
	public void testParseDueDate() {
		String input = "add buy milk due 15/11/15 0959";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(2015, 11, 15, 9, 59);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertNull(cmd.getStartDate());
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseRangeOfDates() {
		String input = "add buy milk from 15/11/2015 to 21/11/2015";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(2015, 11, 15, 0, 0);
		LocalDateTime expectedEndDate = buildDate(2015, 11, 21, 0, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseStartDateOnlyTime() {
		// start date only has time
		String input = "add buy milk from 3pm to 25/11/2015 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(0, 15, 0);
		LocalDateTime expectedEndDate = buildDate(2015, 11, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseDefaultTimes() {
		// Default start time = 0000, end time = 2359
		String input = "add buy milk from 15/12/2015 to 16/12/2015";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(2015, 12, 15, 0, 0);
		LocalDateTime expectedEndDate = buildDate(2015, 12, 15, 23, 59);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
	}

	@Test
	public void testParseEndDateRelatedToStartDate() {
		// end date only has time, related to start date
		String input = "add buy milk from 15/12/2015 3pm to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(2015, 12, 15, 15, 0);
		LocalDateTime expectedEndDate = buildDate(2015, 12, 15, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseMultipleStartKeywords() {
		// multiple `from` keywords, but first `from` is part of content
		String input = "add buy milk from store from 3pm to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(0, 15, 0);
		LocalDateTime expectedEndDate = buildDate(0, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseStartDateActuallyContent() {
		/*
		 * Has a `from-to` sequence, but the 'start date' is actually part of
		 * the content.
		 */
		String input = "add buy milk from store due 25/12/15 9pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(2015, 12, 25, 21, 0);
		assertNull(cmd.getStartDate());
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseOnlyStartKeywordInContent() {
		// Has a `from` keyword but no corresponding `to` keyword
		String input = "add buy milk from store";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseContentEndingWithStartKeyword() {
		// Content ending with `from` keyword
		String input = "add buy milk from";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from", cmd.getContent());
	}

	@Test
	public void testParseImpossibleDate() {
		// 15/15/15 is impossible, hence up to this word is considered content
		String input = "add buy milk from store from 15/15/15 to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(0, 17, 0);
		assertNull(cmd.getStartDate());
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
		assertEquals("buy milk from store from 15/15/15", cmd.getContent());
	}

	@Test
	public void testParseInvalidDateRange() {
		// from store to 5pm is invalid because 'store' is not a date,
		// hence up to 'store' is considered content.
		String input = "add buy milk from store to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(0, 17, 0);
		assertNull(cmd.getStartDate());
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseValidStartDateWithNoEndKeyword() {
		// Has a `from <date>` sequence without end date.
		String input = "add buy milk from 25/12/15";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from 25/12/15", cmd.getContent());
	}

	@Test
	public void testParseEmptyContentWithKeywords() {
		// after parsing keywords, content will be empty ("").
		String input = "add priority high from 3pm to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(0, 15, 0);
		LocalDateTime expectedEndDate = buildDate(0, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("", cmd.getContent());
	}

	@Test
	public void testParsePriority() {
		// high priority
		String input = "add buy milk priority high";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk", cmd.getContent());

		// medium priority
		input = "add buy milk priority medium";
		cmd = CommandController.getInstance().createCommand(input);
		assertEquals(Priority.MEDIUM, cmd.getPriority());

		// low priority
		input = "add buy milk priority low";
		cmd = CommandController.getInstance().createCommand(input);
		assertEquals(Priority.LOW, cmd.getPriority());
	}

	@Test
	public void testParsePriorityWithDueDate() {
		String input = "add buy milk priority high due 25/12/2015 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(2015, 12, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParsePriorityWithRangeOfDates() {
		String input = "add buy milk from store priority high start 21/12/15 6:30am due 25/12/2015 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(2015, 12, 21, 6, 30);
		LocalDateTime expectedEndDate = buildDate(2015, 12, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseSupportedDateFormats() {
		String input = "add buy milk due 9/5/16";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 09/5/16";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 9/05/16";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 09/05/16";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 9/5/2016";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 9-5-16";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDayWithTime() {
		String input = "add buy milk due sat 3pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDateWithNextDay(6, 15, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseRangeOfDaysWithTime() {
		String input = "add buy milk from monday 5:30pm to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDateWithNextDay(1, 17, 30);
		LocalDateTime expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(areDatesSame(expectedStartDate, cmd.getStartDate()));
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseSupportedTimeFormats() {
		String input = "add buy milk due 3pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(0, 15, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 03pm";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 15, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 3:30pm";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 15, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 03:30pm";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 15, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 1530";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 15, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 0930";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 930";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 0930hrs";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 930hrs";
		cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsNow() {
		// Generates a date equal to now. Assert the difference is within 1000
		// milliseconds due to a possible time difference.
		String input = "add buy milk from now to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = LocalDateTime.now();
		int difference = expectedStartDate.compareTo(cmd.getStartDate());
		LocalDateTime expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(difference < 1000 || difference > -1000);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsToday() {
		// "today" generates a date equals to now.
		String input = "add buy milk from today to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = LocalDateTime.now();
		int difference = expectedStartDate.compareTo(cmd.getStartDate());
		LocalDateTime expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(difference < 1000 || difference > -1000);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsTomorrow() {
		// "tomorrow" generates a date equal to tomorrow
		String input = "add buy milk from tomorrow to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = LocalDateTime.now().plusDays(1);
		int difference = expectedStartDate.compareTo(cmd.getStartDate());
		LocalDateTime expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(difference < 1000 || difference > -1000);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsTodayWithTime() {
		String input = "add buy milk from today 5:30pm to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(0, 17, 30);
		LocalDateTime expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(areDatesSame(expectedStartDate, cmd.getStartDate()));
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsTomorrowWithTime() {
		String input = "add buy milk from tomorrow 5:30pm to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDate(1, 17, 30);
		LocalDateTime expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(areDatesSame(expectedStartDate, cmd.getStartDate()));
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseUppercaseDate() {
		String input = "add buy milk by TomorroW 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(1, 17, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseStartAfterEndDate() {
		String input = "add buy milk from 5pm to 3pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from 5pm to 3pm", cmd.getContent());
	}
	
	@Test
	public void testGetIdArrayList() {
		String inputValid = "1, 5,7 9";
		ArrayList<Integer> expectedValid = new ArrayList<Integer>();
		expectedValid.add(1);
		expectedValid.add(5);
		expectedValid.add(7);
		expectedValid.add(9);
		CommandParser parser = new CommandParser();
		assertEquals(parser.getIdArrayList(inputValid), expectedValid);
		
		String inputInvalid = "6, g7";
		assertEquals(parser.getIdArrayList(inputInvalid), null);
	}
	
	@Test
	public void testGetCommandDisplayArg() {
		CommandParser parser = new CommandParser();
		String[] inputCompleted = {"c", "comp", "complete", "completed"};
		for (int i = 0; i < inputCompleted.length; i++) {
			assertEquals(parser.determineDisplayType(inputCompleted[i]), DisplayType.COMPLETED);
		}
		
		String[] inputUncompleted = {"p", "pend", "pending", "i", "incomp", "incomplete", "u", "uncomp", "uncompleted"};
		for (int i = 0; i < inputUncompleted.length; i++) {
			assertEquals(parser.determineDisplayType(inputUncompleted[i]), DisplayType.UNCOMPLETED);
		}
		
		String[] inputAll = {"a", "al", "all"};
		for (int i = 0; i < inputAll.length; i++) {
			assertEquals(parser.determineDisplayType(inputAll[i]), DisplayType.ALL);
		}
		
		String[] inputInvalid = {"every", "cmplt", "com", "error"};
		for (int i = 0; i < inputInvalid.length; i++) {
			assertEquals(parser.determineDisplayType(inputInvalid[i]), DisplayType.INVALID);
		}
	}

	private boolean areDatesSame(LocalDateTime date1, LocalDateTime date2) {
		int difference = date1.compareTo(date2);
		return (difference < 1000 || difference > -1000);
	}

	private LocalDateTime buildDate(int daysOffsetFromNow, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.now();
		date = date.plusDays(daysOffsetFromNow).withHour(hours).withMinute(minutes).withSecond(0);
		return date;
	}

	private LocalDateTime buildDate(int year, int month, int day, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.of(year, month, day, hours, minutes).withSecond(0);
		return date;
	}

	// day: 0 -> sunday, 1 -> monday, ..., 6 -> saturday
	private LocalDateTime buildDateWithNextDay(int day, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.now();
		int diff = (day - date.getDayOfWeek().getValue());
		if (diff < 0) {
			diff += 7;
		}
		date = date.plusDays(diff).withHour(hours).withMinute(minutes).withSecond(0);
		return date;
	}

}
