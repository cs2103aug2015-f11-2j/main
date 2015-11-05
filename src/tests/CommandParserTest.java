package tests;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import app.constants.TaskConstants.DisplayType;
import app.constants.TaskConstants.Priority;
import app.constants.TaskConstants.RemovableField;
import app.logic.CommandController;
import app.logic.command.Command;
import app.parser.CommandParser;

public class CommandParserTest {
	
	// @@author A0126120B
	@Test
	public void testParseDueDate() {
		String input = "add buy milk due 15/11/15 0959";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(2015, 11, 15, 9, 59);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertNull(cmd.getStartDate());
		assertEquals("buy milk", cmd.getContent());
	}

	// @@author A0126120B
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

	// @@author A0126120B
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

	// @@author A0126120B
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

	// @@author A0126120B
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

	// @@author A0126120B
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

	// @@author A0126120B
	@Test
	public void testParseOnlyStartKeywordInContent() {
		// Has a `from` keyword but no corresponding `to` keyword
		String input = "add buy milk from store";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from store", cmd.getContent());
	}

	// @@author A0126120B
	@Test
	public void testParseContentEndingWithStartKeyword() {
		// Content ending with `from` keyword
		String input = "add buy milk from";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from", cmd.getContent());
	}

	// @@author A0126120B
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

	// @@author A0126120B
	@Test
	public void testParseValidStartDateWithNoEndKeyword() {
		// Has a `from <date>` sequence without end date.
		String input = "add buy milk from 25/12/15";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from 25/12/15", cmd.getContent());
	}

	// @@author A0126120B
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

	// @@author A0126120B
	@Test
	public void testParsePriority() {
		// high priority
		String input = "add buy milk priority high";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk", cmd.getContent());

		// medium priority
		input = "add buy milk pri medium";
		cmd = CommandController.getInstance().createCommand(input);
		assertEquals(Priority.MEDIUM, cmd.getPriority());

		// low priority
		input = "add buy milk p low";
		cmd = CommandController.getInstance().createCommand(input);
		assertEquals(Priority.LOW, cmd.getPriority());
	}

	// @@author A0126120B
	@Test
	public void testParsePriorityWithDueDate() {
		String input = "add buy milk priority high due 25/12/2015 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedEndDate = buildDate(2015, 12, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk", cmd.getContent());
	}

	// @@author A0126120B
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

	// @@author A0126120B
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

	// @@author A0126120B
	@Test
	public void testParseRangeOfDaysWithTime() {
		String input = "add buy milk from monday 5:30pm to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		LocalDateTime expectedStartDate = buildDateWithNextDay(1, 17, 30);
		LocalDateTime expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(areDatesSame(expectedStartDate, cmd.getStartDate()));
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	// @@author A0126120B
	@Test
	public void testParseStartAfterEndDate() {
		String input = "add buy milk from 5pm to 3pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from 5pm to 3pm", cmd.getContent());
	}
	
	// TODO: collate from wj
	@Test
	public void testParseRemovePriority() {
		String input = "edit 4 drink milk priority none";
		Command cmd = CommandController.getInstance().createCommand(input);
		CommandParser.parseDatesAndPriority(cmd, true);
		assertEquals(RemovableField.PRIORITY, cmd.getRemoveField().get(0));
		
		input = "edit 4 drink milk priority high";
		cmd = CommandController.getInstance().createCommand(input);
		CommandParser.parseDatesAndPriority(cmd, true);
		assertTrue(cmd.getRemoveField().isEmpty());
	}
	
	@Test
	public void testParseRemoveDate() {
		String input = "edit 7 destroy milk date none";
		Command cmd = CommandController.getInstance().createCommand(input);
		CommandParser.parseDatesAndPriority(cmd, true);
		assertEquals(RemovableField.DATE, cmd.getRemoveField().get(0));
		
		input = "edit 4 drink milk date empty";
		cmd = CommandController.getInstance().createCommand(input);
		CommandParser.parseDatesAndPriority(cmd, true);
		assertTrue(cmd.getRemoveField().isEmpty());
	}
	
	@Test
	public void testParseRemoveDateAndPriority() {
		String input = "edit 7 destroy milk date none priority none";
		Command cmd = CommandController.getInstance().createCommand(input);
		CommandParser.parseDatesAndPriority(cmd, true);
		assertEquals(RemovableField.DATE, cmd.getRemoveField().get(0));
		assertEquals(RemovableField.PRIORITY, cmd.getRemoveField().get(1));
	}

	@Test
	public void testDetermineDisplayType() {
		// Split into partitions, input is not case sensitive and leading/trailing spaces do not matter
		// Aliases for "completed"
		String[] completed = { "c", "comp ", "complete", "completed", "CoMpleTed" };
		for (String input : completed) {
			assertEquals(CommandParser.determineDisplayType(input), DisplayType.COMPLETED);
		}
		
		// Aliases for "uncompleted"
		String[] inputUncompleted = { " ", "pend", "pending", "i", "incomp", "incomplete", "u", "uncomp", "uncompleted"};
		for (int i = 0; i < inputUncompleted.length; i++) {
			assertEquals(CommandParser.determineDisplayType(inputUncompleted[i]), DisplayType.UNCOMPLETED);
		}

		// Aliases for "all"
		String[] all = { "a", "al", "all ", " A", "AL", "ALl" };
		for (String input : all) {
			assertEquals(CommandParser.determineDisplayType(input), DisplayType.ALL);
		}

		// Any other string that are invalid or cause exception
		String[] invalid = { " cmplt", "c o m p l e t e", "-1", null };
		for (String input : invalid) {
			assertEquals(CommandParser.determineDisplayType(input), DisplayType.INVALID);
		}
	}

	@Test
	public void testGetTaskDisplayedIdFromContent() {
		// With number, positive
		String input = " 1 this is the task";
		Integer expected = 1;
		assertEquals(CommandParser.getTaskDisplayedIdFromContent(input), expected);
		
		// With number, negative
		input = "-5    this is not the task";
		expected = -5;
		assertEquals(CommandParser.getTaskDisplayedIdFromContent(input), expected);
		
		// With number, no String
		input = "5";
		expected = 5;
		assertEquals(CommandParser.getTaskDisplayedIdFromContent(input), expected);
		
		// Without number
		input = " hello";
		assertNull(CommandParser.getTaskDisplayedIdFromContent(input));
	}
	
	@Test
	public void testGetTaskDescFromContent() {
		// normal input
		String input = "4  do cs2103 ";
		String expected = "do cs2103";
		assertEquals(CommandParser.getTaskDescFromContent(input), expected);
		
		// no String
		input = "7";
		expected = "";
		assertEquals(CommandParser.getTaskDescFromContent(input), expected);
	}
	
	@Test
	public void testDetermineMarkAll() {
		String content = "a";
		String expected = "ALL";
		assertEquals(CommandParser.determineMarkAll(content), expected);
		content = "al";
		assertEquals(CommandParser.determineMarkAll(content), expected);
		content = "all";
		assertEquals(CommandParser.determineMarkAll(content), expected);
		content = "ag";
		expected = "ag";
		assertEquals(CommandParser.determineMarkAll(content), expected);
		content = "1, 3 4 ,7, 50,50";
		expected = "1, 3 4 ,7, 50,50";
		assertEquals(CommandParser.determineMarkAll(content), expected);
	}

	// @@author A0126120B
	private boolean areDatesSame(LocalDateTime date1, LocalDateTime date2) {
		int difference = date1.compareTo(date2);
		return (difference < 1000 || difference > -1000);
	}

	// @@author A0126120B
	private LocalDateTime buildDate(int daysOffsetFromNow, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.now();
		date = date.plusDays(daysOffsetFromNow).withHour(hours).withMinute(minutes).withSecond(0).withNano(0);
		return date;
	}

	// @@author A0126120B
	private LocalDateTime buildDate(int year, int month, int day, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.of(year, month, day, hours, minutes).withSecond(0).withNano(0);
		return date;
	}

	// @@author A0126120B
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
