package tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import app.constants.TaskConstants.Priority;
import app.helper.CommandParser;
import app.model.command.Command;

public class ParserTest {

	@Test
	public void testParseDueDate() {
		CommandParser parser = new CommandParser();
		Date expectedEndDate = new Date();

		String input = "add buy milk due 15/11/15 0959";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2015, 11, 15, 9, 59);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertNull(cmd.getStartDate());
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseRangeOfDates() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from 15/11/2015 to 21/11/2015";
		Command cmd = parser.parseCommand(input);
		expectedStartDate = buildDate(2015, 11, 15, 0, 0);
		expectedEndDate = buildDate(2015, 11, 21, 0, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseStartDateOnlyTime() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// start date only has time
		String input = "add buy milk from 3pm to 25/11/2015 5pm";
		Command cmd = parser.parseCommand(input);
		expectedStartDate = new Date();
		expectedStartDate = buildDate(0, 15, 0);
		expectedEndDate = buildDate(2015, 11, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseEndDateRelatedToStartDate() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// end date only has time, related to start date
		String input = "add buy milk from 15/12/2015 3pm to 5pm";
		Command cmd = parser.parseCommand(input);
		expectedStartDate = buildDate(2015, 12, 15, 15, 0);
		expectedEndDate = buildDate(2015, 12, 15, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseMultipleStartKeywords() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// multiple `from` keywords, but first `from` is part of content
		String input = "add buy milk from store from 3pm to 5pm";
		Command cmd = parser.parseCommand(input);
		expectedStartDate = buildDate(0, 15, 0);
		expectedEndDate = buildDate(0, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseStartDateActuallyContent() {
		CommandParser parser = new CommandParser();
		Date expectedEndDate = new Date();

		/*
		 * Has a `from-to` sequence, but the 'start date' is actually part of
		 * the content.
		 */
		String input = "add buy milk from store due 25/12/15 9pm";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2015, 12, 25, 21, 0);
		assertNull(cmd.getStartDate());
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseOnlyStartKeywordInContent() {
		CommandParser parser = new CommandParser();

		// Has a `from` keyword but no corresponding `to` keyword
		String input = "add buy milk from store";
		Command cmd = parser.parseCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseContentEndingWithStartKeyword() {
		CommandParser parser = new CommandParser();

		// Content ending with `from` keyword
		String input = "add buy milk from";
		Command cmd = parser.parseCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from", cmd.getContent());
	}

	@Test
	public void testParseImpossibleDate() {
		CommandParser parser = new CommandParser();
		Date expectedEndDate = new Date();

		// 15/15/15 is impossible, hence up to this word is considered content
		String input = "add buy milk from store from 15/15/15 to 5pm";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 17, 0);
		assertNull(cmd.getStartDate());
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
		assertEquals("buy milk from store from 15/15/15", cmd.getContent());
	}

	@Test
	public void testParseInvalidDateRange() {
		CommandParser parser = new CommandParser();
		Date expectedEndDate = new Date();

		// from store to 5pm is invalid because 'store' is not a date,
		// hence up to 'store' is considered content.
		String input = "add buy milk from store to 5pm";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 17, 0);
		assertNull(cmd.getStartDate());
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseValidStartDateWithNoEndKeyword() {
		CommandParser parser = new CommandParser();

		// Has a `from <date>` sequence without end date.
		String input = "add buy milk from 25/12/15";
		Command cmd = parser.parseCommand(input);
		assertNull(cmd.getStartDate());
		assertNull(cmd.getEndDate());
		assertEquals("buy milk from 25/12/15", cmd.getContent());
	}

	@Test
	public void testEmptyContentWithOtherKeywords() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// after parsing keywords, content will be empty ("").
		String input = "add priority high from 3pm to 5pm";
		Command cmd = parser.parseCommand(input);
		expectedStartDate = buildDate(0, 15, 0);
		expectedEndDate = buildDate(0, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("", cmd.getContent());
	}

	@Test
	public void testParsePriority() {
		CommandParser parser = new CommandParser();

		// high priority
		String input = "add buy milk priority high";
		Command cmd = parser.parseCommand(input);
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk", cmd.getContent());

		// medium priority
		input = "add buy milk priority medium";
		cmd = parser.parseCommand(input);
		assertEquals(Priority.MEDIUM, cmd.getPriority());

		// low priority
		input = "add buy milk priority low";
		cmd = parser.parseCommand(input);
		assertEquals(Priority.LOW, cmd.getPriority());
	}

	@Test
	public void testParsePriorityWithDueDate() {
		CommandParser parser = new CommandParser();
		Date expectedEndDate = new Date();

		String input = "add buy milk priority high due 25/12/2015 5pm";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2015, 12, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParsePriorityWithRangeOfDates() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from store priority high start 21/12/15 6:30am due 25/12/2015 5pm";
		Command cmd = parser.parseCommand(input);
		expectedStartDate = buildDate(2015, 12, 21, 6, 30);
		expectedEndDate = buildDate(2015, 12, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseSupportedDateFormats() {
		CommandParser parser = new CommandParser();
		Date expectedEndDate = new Date();

		String input = "add buy milk due 9/5/16";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 09/5/16";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 9/05/16";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 09/05/16";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 9/5/2016";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 9-5-16";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseSupportedTimeFormats() {
		CommandParser parser = new CommandParser();
		Date expectedEndDate = new Date();

		String input = "add buy milk due 3pm";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 15, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 03pm";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 15, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 3:30pm";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 15, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 03:30pm";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 15, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 1530";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 15, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 0930";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 930";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 0930hrs";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));

		input = "add buy milk due 930hrs";
		cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(0, 9, 30);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	private boolean areDatesSame(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal.setTime(date1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal2.setTime(date2);
		cal2.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MILLISECOND, 0);
		return cal.compareTo(cal2) == 0;
	}

	@SuppressWarnings("deprecation")
	private Date buildDate(int daysOffsetFromNow, int hours, int minutes) {
		Date date = new Date();
		date.setDate(date.getDate() + daysOffsetFromNow);
		date.setHours(hours);
		date.setMinutes(minutes);
		date.setSeconds(0);
		return date;
	}

	@SuppressWarnings("deprecation")
	private Date buildDate(int year, int month, int day, int hours, int minutes) {
		Date date = new Date();
		date.setYear(year - 1900);
		date.setMonth(month - 1);
		date.setDate(day);
		date.setHours(hours);
		date.setMinutes(minutes);
		date.setSeconds(0);
		return date;
	}

}
