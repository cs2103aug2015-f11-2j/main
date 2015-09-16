package tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import app.constants.TaskConstants.Priority;
import app.controller.CommandController;
import app.helper.CommandParser;
import app.model.command.Command;

public class ParserTest {

	@Test
	public void testParseDueDate() {
		Date expectedEndDate = new Date();

		String input = "add buy milk due 15/11/15 0959";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2015, 11, 15, 9, 59);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertNull(cmd.getStartDate());
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseRangeOfDates() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from 15/11/2015 to 21/11/2015";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDate(2015, 11, 15, 0, 0);
		expectedEndDate = buildDate(2015, 11, 21, 0, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseStartDateOnlyTime() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// start date only has time
		String input = "add buy milk from 3pm to 25/11/2015 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = new Date();
		expectedStartDate = buildDate(0, 15, 0);
		expectedEndDate = buildDate(2015, 11, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseEndDateRelatedToStartDate() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// end date only has time, related to start date
		String input = "add buy milk from 15/12/2015 3pm to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDate(2015, 12, 15, 15, 0);
		expectedEndDate = buildDate(2015, 12, 15, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParseMultipleStartKeywords() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// multiple `from` keywords, but first `from` is part of content
		String input = "add buy milk from store from 3pm to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDate(0, 15, 0);
		expectedEndDate = buildDate(0, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseStartDateActuallyContent() {
		Date expectedEndDate = new Date();

		/*
		 * Has a `from-to` sequence, but the 'start date' is actually part of
		 * the content.
		 */
		String input = "add buy milk from store due 25/12/15 9pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2015, 12, 25, 21, 0);
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
		Date expectedEndDate = new Date();

		// 15/15/15 is impossible, hence up to this word is considered content
		String input = "add buy milk from store from 15/15/15 to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 17, 0);
		assertNull(cmd.getStartDate());
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
		assertEquals("buy milk from store from 15/15/15", cmd.getContent());
	}

	@Test
	public void testParseInvalidDateRange() {
		Date expectedEndDate = new Date();

		// from store to 5pm is invalid because 'store' is not a date,
		// hence up to 'store' is considered content.
		String input = "add buy milk from store to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 17, 0);
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
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// after parsing keywords, content will be empty ("").
		String input = "add priority high from 3pm to 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDate(0, 15, 0);
		expectedEndDate = buildDate(0, 17, 0);
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
		Date expectedEndDate = new Date();

		String input = "add buy milk priority high due 25/12/2015 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2015, 12, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk", cmd.getContent());
	}

	@Test
	public void testParsePriorityWithRangeOfDates() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from store priority high start 21/12/15 6:30am due 25/12/2015 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDate(2015, 12, 21, 6, 30);
		expectedEndDate = buildDate(2015, 12, 25, 17, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertEquals(Priority.HIGH, cmd.getPriority());
		assertEquals("buy milk from store", cmd.getContent());
	}

	@Test
	public void testParseSupportedDateFormats() {
		Date expectedEndDate = new Date();

		String input = "add buy milk due 9/5/16";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(2016, 5, 9, 0, 0);
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
		Date expectedEndDate = new Date();

		String input = "add buy milk due sat 3pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDateWithNextDay(6, 15, 0);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseRangeOfDaysWithTime() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from monday 5:30pm to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDateWithNextDay(1, 17, 30);
		expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(areDatesSame(expectedStartDate, cmd.getStartDate()));
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseSupportedTimeFormats() {
		Date expectedEndDate = new Date();

		String input = "add buy milk due 3pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(0, 15, 0);
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
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// Generates a date equal to now. Assert the difference is within 1000
		// milliseconds due to a possible time difference.
		String input = "add buy milk from now to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = new Date();
		int difference = expectedStartDate.compareTo(cmd.getStartDate());
		expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(difference < 1000 || difference > -1000);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsToday() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// "today" generates a date equals to now.
		String input = "add buy milk from today to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = new Date();
		int difference = expectedStartDate.compareTo(cmd.getStartDate());
		expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(difference < 1000 || difference > -1000);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testParseDateIsTomorrow() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		// "tomorrow" generates a date equal to tomorrow
		String input = "add buy milk from tomorrow to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = new Date();
		expectedStartDate.setDate(expectedStartDate.getDate() + 1);
		int difference = expectedStartDate.compareTo(cmd.getStartDate());
		expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(difference < 1000 || difference > -1000);
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsTodayWithTime() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from today 5:30pm to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDate(0, 17, 30);
		expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(areDatesSame(expectedStartDate, cmd.getStartDate()));
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}

	@Test
	public void testParseDateIsTomorrowWithTime() {
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from tomorrow 5:30pm to 14/5/2050";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedStartDate = buildDate(1, 17, 30);
		expectedEndDate = buildDate(2050, 5, 14, 0, 0);
		assertTrue(areDatesSame(expectedStartDate, cmd.getStartDate()));
		assertTrue(areDatesSame(expectedEndDate, cmd.getEndDate()));
	}
	
	@Test
	public void testParseUppercaseDate() {
		Date expectedEndDate = new Date();

		String input = "add buy milk by TomorroW 5pm";
		Command cmd = CommandController.getInstance().createCommand(input);
		expectedEndDate = buildDate(1, 17, 0);
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

	@SuppressWarnings("deprecation")
	// day: 0 -> sunday, 1 -> monday, ..., 6 -> saturday
	private Date buildDateWithNextDay(int day, int hours, int minutes) {
		Date date = new Date();
		int diff = day - date.getDay();
		if (diff < 0) {
			diff += 7;
		}
		date.setDate(date.getDate()+diff);
		date.setHours(hours);
		date.setMinutes(minutes);
		return date;
	}

}
