# A0132764Ereused
###### src\app\parser\CommandParser.java
``` java
	/**
	 * Determine the display argument from the entered string (for search)
	 * 
	 * @param arg The specified display option
	 * @return The specified DisplayType parsed from arg
	 */
	public static DisplayType determineDisplayTypeSearch(String arg) {
		try {
			String type = arg.toLowerCase().trim();
			if (DISPLAY_COMPLETED.contains(type)) {
				return DisplayType.COMPLETED;
			} else if (DISPLAY_UNCOMPLETED.contains(type)) {
				return DisplayType.UNCOMPLETED;
			} else if (DISPLAY_ALL.contains(type)) {
				return DisplayType.ALL;
			} else if (type.isEmpty()) {
				return null;
			}else {
				return DisplayType.INVALID;
			}
		} catch (Exception e) {
			return DisplayType.INVALID;
		}
	}

```
