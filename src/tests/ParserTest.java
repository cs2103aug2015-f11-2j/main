package tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import app.helper.CommandParser;
import app.model.command.Command;

public class ParserTest {

	@Test
	public void testDueDate() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk due 15/11/15 0959";
		Command cmd = parser.parseCommand(input);
		expectedEndDate = buildDate(2015, 11, 15, 9, 59);
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
		assertNull(cmd.getStartDate());
	}

	@Test
	public void testRangeOfDates() {
		CommandParser parser = new CommandParser();
		Date expectedStartDate = new Date();
		Date expectedEndDate = new Date();

		String input = "add buy milk from 15/11/2015 to 21/11/2015";
		Command cmd = parser.parseCommand(input);
		expectedStartDate = buildDate(2015, 11, 15, 0, 0);
		expectedEndDate = buildDate(2015, 11, 21, 0, 0);
		assertTrue(areDatesSame(cmd.getStartDate(), expectedStartDate));
		assertTrue(areDatesSame(cmd.getEndDate(), expectedEndDate));
	}

	@Test
	public void testStartDateOnlyTime() {
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
	}

	@Test
	public void testEndDateRelatedToStartDate() {
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
		date.setHours(year - 1900);
		date.setMonth(month - 1);
		date.setDate(day);
		date.setHours(hours);
		date.setMinutes(minutes);
		date.setSeconds(0);
		return date;
	}

}
