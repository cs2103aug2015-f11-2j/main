package tests;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

import app.parser.DateParser;

//@@author A0126120B
public class DateParserTest {

	@Test
	public void testToLocalDateTime() {
		// 15/6/2016 0900hrs
		long epochDate = System.currentTimeMillis();
		Date date = new Date(epochDate);
		LocalDateTime expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochDate), ZoneOffset.systemDefault());
		assertEquals(expected, DateParser.toLocalDateTime(date));
	}

	@Test
	public void testDefaultTime() {
		String dateString = "today";
		
		LocalDateTime expected = buildDate(0, 0, 0);
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		expected = buildDate(0, 23, 59);
		date = DateParser.determineEndDate(dateString, null);
		assertEquals(expected, date);
	}
	
	@Test
	public void testDayOffset() {
		String dateString = "tomorrow";
		
		LocalDateTime expected = buildDate(1, 0, 0);
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testDatePatterns() {
		String dateString = "15/9/16";
		LocalDateTime expected = buildDate(2016, 9, 15, 0, 0);
		
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "15-9-16";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "15-9-2016";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "15-09-16";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testTimePatterns() {
		String dateString = "5pm";
		LocalDateTime expected = buildDate(0, 17, 0);
		
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "05pm";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "5:00pm";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "05:00pm";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "1700";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "1700hrs";
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testNextDay() {
		String dateString = "monday";
		LocalDateTime expected = buildDateWithNextDay(1, 0, 0);
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
		
		dateString = "friday";
		expected = buildDateWithNextDay(5, 0, 0);
		date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testReferenceDate() {
		String dateString = "5pm";
		LocalDateTime expected = buildDate(2016, 9, 15, 17, 0);
		LocalDateTime start = buildDate(2016, 9, 15, 15, 0);
		LocalDateTime date = DateParser.determineEndDate(dateString, start);
		assertEquals(expected, date);
	}
	
	@Test
	public void testDateWithTime() {
		String dateString = "15/9/16 5pm";
		LocalDateTime expected = buildDate(2016, 9, 15, 17, 0);
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testDayOffsetWithTime() {
		String dateString = "tomorrow 5pm";
		LocalDateTime expected = buildDate(1, 17, 0);
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testDayWithTime() {
		String dateString = "monday 5pm";
		LocalDateTime expected = buildDateWithNextDay(1, 17, 0);
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testTimeIsNow() {
		String dateString = "now";
		LocalDateTime expected = LocalDateTime.now();
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertTrue(approximatelyEqual(expected, date));
	}
	
	@Test
	public void testUppercaseDate() {
		String dateString = "TomoRRoW 5pm";
		LocalDateTime expected = buildDate(1, 17, 0);
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertEquals(expected, date);
	}
	
	@Test
	public void testImpossibleDate() {
		String dateString = "15/15/15";
		LocalDateTime date = DateParser.determineStartDate(dateString);
		assertNull(date);
	}
	

	private LocalDateTime buildDate(int daysOffsetFromNow, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.now();
		date = date.plusDays(daysOffsetFromNow).withHour(hours).withMinute(minutes).withSecond(0).withNano(0);
		return date;
	}

	// day: 0 -> sunday, 1 -> monday, ..., 6 -> saturday
	private LocalDateTime buildDateWithNextDay(int day, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.now();
		int diff = (day - date.getDayOfWeek().getValue());
		if (diff < 0) {
			diff += 7;
		}
		date = date.plusDays(diff).withHour(hours).withMinute(minutes).withSecond(0).withNano(0);
		return date;
	}

	private LocalDateTime buildDate(int year, int month, int day, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.of(year, month, day, hours, minutes).withSecond(0).withNano(0);
		return date;
	}
	
	private boolean approximatelyEqual(LocalDateTime date1, LocalDateTime date2) {
		int difference = date1.compareTo(date2);
		return (difference < 1000 || difference > -1000);
	}
}
