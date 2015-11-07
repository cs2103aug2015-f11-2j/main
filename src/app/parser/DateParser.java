package app.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import app.util.Common;

// @@author A0126120B
public class DateParser {
	private static final List<String> DATE_PATTERNS = Common.getUnmodifiableList("dd/MM/yy", "d/MM/yy", "dd/M/yy", "d/M/yy",
			"dd/MM/yyyy", "d/MM/yyyy", "dd/M/yyyy", "d/M/yyyy", "dd-MM-yy", "d-MM-yy", "dd-M-yy", "d-M-yy",
			"dd-MM-yyyy", "d-MM-yyyy", "dd-M-yyyy", "d-M-yyyy");
	private static final List<String> TIME_PATTERNS = Common.getUnmodifiableList("h:mma", "hh:mma", "HHmm", "Hmm", "HHmm'hrs'",
			"Hmm'hrs'", "ha", "hha");
	private static final List<String> DAY_PATTERNS = Common.getUnmodifiableList("EEEE", "EEE");
	private static final List<String> TOMORROW_PATTERNS = Common.getUnmodifiableList("tomorrow", "tmr");
	
	/**
	 * Tries to return a LocalDateTime object from a given string representing a
	 * start date.
	 * 
	 * @param dateString The string representation of the date
	 * @return LocalDateTime if date can be parsed, else null
	 */
	public static LocalDateTime determineStartDate(String dateString) {
		return determineDate(dateString, null, false);
	}

	/**
	 * Tries to return a LocalDateTime object from a given string representing
	 * an end date.
	 * 
	 * @param dateString The string representation of the date
	 * @param reference The reference used if dateString contains only time
	 * @return LocalDateTime if date can be parsed, else null
	 */
	public static LocalDateTime determineEndDate(String dateString, LocalDateTime reference) {
		return determineDate(dateString, reference, true);
	}
	
	/**
	 * Converts a Date object to LocalDateTime object
	 * 
	 * @param date The Date object to convert
	 * @return The equivalent LocalDateTime object
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
		return dateTime;
	}

	/**
	 * Tries to return a LocalDateTime object from a given string
	 * representation. A reference date is specified. For instance, "3pm" would
	 * result in a date equal to the reference date but with a time of "3pm".
	 * 
	 * @param dateString The string representation of the date
	 * @param reference The reference used if dateString contains only time
	 * @param isEndDate Determines the default time used if dateString contains
	 *            only date
	 * @return LocalDateTime object if date can be parsed, else null
	 */
	private static LocalDateTime determineDate(String dateString, LocalDateTime reference, boolean isEndDate) {
		dateString = dateString.toLowerCase();
		LocalDateTime date = null;

		// default time is 0000 if startDate, 2359 if endDate
		LocalTime defaultTime = LocalTime.of(0, 0);
		if (isEndDate) {
			defaultTime = LocalTime.of(23, 59);
		}

		// Special cases: "now", "today", "tomorrow"
		if (dateString.equalsIgnoreCase("now")) {
			date = LocalDateTime.now();
			return processDate(date);
		} else if (dateString.equalsIgnoreCase("today")) {
			date = LocalDateTime.now();
			date = setDefaultTime(date, defaultTime);
			return processDate(date);
		} else if (TOMORROW_PATTERNS.contains(dateString)) {
			date = LocalDateTime.now().plusDays(1);
			date = setDefaultTime(date, defaultTime);
			return processDate(date);
		}

		// check date patterns only
		for (String datePattern : DATE_PATTERNS) {
			date = getDateFromPattern(dateString, datePattern);
			if (date != null) {
				date = setDefaultTime(date, defaultTime);
				return processDate(date);
			}
			// check date + time patterns
			for (String timePattern : TIME_PATTERNS) {
				date = getDateFromPattern(dateString, datePattern + " " + timePattern);
				if (date != null) {
					return processDate(date);
				}
			}
		}

		// check time patterns only
		for (String timePattern : TIME_PATTERNS) {
			date = getDateFromPattern(dateString, timePattern);

			int dayOffset = 0;
			boolean skipReference = false;

			// check "today" + time pattern
			if (date == null) {
				date = getDateFromPattern(dateString, "'today' " + timePattern);
				if (date != null) {
					skipReference = true;
				}
			}

			// check "tomorrow" + time pattern
			if (date == null) {
				for (String tomorrowPattern : TOMORROW_PATTERNS) {
					String pattern = String.format("'%s' %s", tomorrowPattern, timePattern);
					date = getDateFromPattern(dateString, pattern);
					if (date != null) {
						skipReference = true;
						dayOffset = 1;
						break;
					}
				}
			}

			if (date != null) {
				if (!skipReference && reference != null) {
					LocalDateTime newDate = reference.withHour(date.getHour());
					newDate = newDate.withMinute(date.getMinute());
					return processDate(newDate);
				} else {
					LocalDateTime todayDate = LocalDateTime.now();
					todayDate = todayDate.plusDays(dayOffset).withHour(date.getHour()).withMinute(date.getMinute());
					return processDate(todayDate);
				}
			}

			// check day + time patterns
			for (String dayPattern : DAY_PATTERNS) {
				date = getDateFromPattern(dateString, dayPattern + " " + timePattern);
				if (date != null) {
					date = buildDateWithNextDay(date.getDayOfWeek().getValue(), date.getHour(), date.getMinute());
					return processDate(date);
				}
			}
		}

		// check day pattern only
		for (String dayPattern : DAY_PATTERNS) {
			date = getDateFromPattern(dateString, dayPattern);
			if (date != null) {
				date = buildDateWithNextDay(date.getDayOfWeek().getValue(), date.getHour(), date.getMinute());
				date = setDefaultTime(date, defaultTime);
				return processDate(date);
			}
		}
		return null;
	}

	/**
	 * Sets date with the default time specified.
	 * @param date The date to modify
	 * @param defaultTime The default time to use
	 * @return The date with the default time applied
	 */
	private static LocalDateTime setDefaultTime(LocalDateTime date, LocalTime defaultTime) {
		if (date != null && defaultTime != null) {
			return date.withHour(defaultTime.getHour()).withMinute(defaultTime.getMinute());
		}
		return null;
	}

	/**
	 * Strips seconds and nanoseconds from the date (set to zero).
	 * @param date The date to strip seconds/nanoseconds from
	 * @return The date without seconds/nanoseconds
	 */
	private static LocalDateTime processDate(LocalDateTime date) {
		if (date != null) {
			return date.withSecond(0).withNano(0);
		}
		return null;
	}

	/**
	 * Builds a Date object with the day set to the nearest day specified by the
	 * 'day' parameter. The hours and minutes of the Date object are also set as
	 * specified.
	 * 
	 * For example, if 'day' is 2, the returned date is the first Tuesday from
	 * now.
	 * 
	 * @param day An integer indicating the day: 1 -> monday, 2 -> tuesday, ...,
	 *            7 -> sunday
	 * @param hours The hours to set the date to
	 * @param minutes The minutes to set the date to
	 * @return The constructed date with the day, hours, and minutes set.
	 */
	private static LocalDateTime buildDateWithNextDay(int day, int hours, int minutes) {
		LocalDateTime date = LocalDateTime.now();
		int diff = (day - date.getDayOfWeek().getValue());
		if (diff < 0) {
			diff += 7;
		}
		date = date.plusDays(diff).withHour(hours).withMinute(minutes);
		return date;
	}

	/**
	 * Tries to parse a string representation of a date using the given pattern.
	 * 
	 * @param dateString The string representation of the date
	 * @param pattern The pattern to try parsing with
	 * @return LocalDateTime object if date can be parsed, else null
	 */
	private static LocalDateTime getDateFromPattern(String dateString, String pattern) {
		// Use SimpleDateFormat because it's much more flexible for datetimes
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			Date date = sdf.parse(dateString);
			String dateTimeFormat = sdf.format(date);
			if (dateTimeFormat.equalsIgnoreCase(dateString)) {
				return toLocalDateTime(date);
			}
		} catch (ParseException e) {
			// cannot be parsed
		}

		return null;
	}
}
