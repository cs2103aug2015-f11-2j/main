# A0125360Rreused
###### src\app\util\Common.java
``` java
	/**
	 * Removes the duplicated elements in the ArrayList
	 * 
	 * @param <T>
	 * @param listWithDuplicates The ArrayList with duplicated elements
	 * @return An ArrayList with no duplicate elements
	 */
	public static <T> ArrayList<T> removeDuplicatesFromArrayList(ArrayList<T> listWithDuplicates) {
		Set<T> noDuplicates = new LinkedHashSet<T>(listWithDuplicates);
		return new ArrayList<T>(noDuplicates);
	}
	
```
