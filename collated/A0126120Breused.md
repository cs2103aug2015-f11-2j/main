# A0126120Breused
###### src\app\parser\DateParser.java
``` java
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
```
###### src\tests\CommandTest.java
``` java
	@Before
	public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
	   Field instance = CommandController.class.getDeclaredField("commandController");
	   instance.setAccessible(true);
	   instance.set(null, null);
	}
	
```
