package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import app.util.Common;

public class CommonTest {
	// @@author A0125360R
	@Test
	public void testPluralize() {
		String singular = "glass";
		String plural = "glasses";
		
		// No plural partition, boundary case count = 1
		int count = 1;
		String expected = "glass";
		assertEquals(Common.pluralize(count, singular), expected);
		
		// No plural partition, boundary case count > 1
		count = 2;
		expected = "glasss";
		assertEquals(Common.pluralize(count, singular), expected);
		
		// No plural partition, boundary case count < 1
		count = 0;
		expected = "glass";
		assertEquals(Common.pluralize(count, singular), expected);			
		
		// plural partition, boundary case count > 1
		count = 2;
		expected = "glasses";
		assertEquals(Common.pluralize(count, singular, plural), expected);
		
		// plural partition, boundary case count < 1
		count = -1;
		expected = "glass";
		assertEquals(Common.pluralize(count, singular, plural), expected);
	}
	
	@Test
	public void testGetStringFromArrayIndexRange() {
		String[] array = { "one", "two", "three", "four", "five" };
		int start = 0;
		int end = 4;
		
		// Valid start & end, boundary case start = 0
		String expected = "one two three four five";
		assertEquals(Common.getStringFromArrayIndexRange(start, end, array), expected);
		
		// Invalid start & valid end, boundary case start < 0
		start = -1;
		expected = "";
		assertEquals(Common.getStringFromArrayIndexRange(start, end, array), expected);
		
		// Valid start & end, boundary case start = end
		start = 1;
		end = 1;
		expected = "two";
		assertEquals(Common.getStringFromArrayIndexRange(start, end, array), expected);
		
		// Invalid start & end, boundary case start > end
		start = 4;
		end = 1;
		expected = "";
		assertEquals(Common.getStringFromArrayIndexRange(start, end, array), expected);
	
		// Invalid end, end > array length
		start = 0;
		end = 10;
		expected = "one two three four five";
		assertEquals(Common.getStringFromArrayIndexRange(start, end, array), expected);
	}

	@Test
	public void testGetUnmodifiableList() {
		List<String> list = new ArrayList<String>();
		list.add("Test");
		list.add("324");
		list.add(null);
		List<String> output = Common.getUnmodifiableList("Test", "324", null);
		assertEquals(output, list);
	}
		
	@Test
	public void testGetIdArrayList() {
		// space and/or comma separates numbers
		// Input are valid positive integers
		String inputValidPositive = "1, 5,7 95";
		ArrayList<Integer> expectedValidPositive = new ArrayList<Integer>();
		expectedValidPositive.add(1);
		expectedValidPositive.add(5);
		expectedValidPositive.add(7);
		expectedValidPositive.add(95);
		assertEquals(Common.getIdArrayList(inputValidPositive), expectedValidPositive);
		
		// Input are valid negative integers
		String inputValidNegative = "-4, -8, -9 -10";
		ArrayList<Integer> expectedValidNegative = new ArrayList<Integer>();
		expectedValidNegative.add(-4);
		expectedValidNegative.add(-8);
		expectedValidNegative.add(-9);
		expectedValidNegative.add(-10);
		assertEquals(Common.getIdArrayList(inputValidNegative), expectedValidNegative);

		// Inputs are invalid
		String inputInvalid = "6, g7";
		assertEquals(Common.getIdArrayList(inputInvalid), null);
		inputInvalid = "45.54, 7";
		assertEquals(Common.getIdArrayList(inputInvalid), null);
	}
	
	@Test
	public void testBetweenInclusive() {
		int subject = 5;
		int lower = 5;
		int upper = 5;
		
		// true partition, boundary case subject = lower = upper
		assertTrue(Common.betweenInclusive(subject, lower, upper));
		
		// true partition, boundary case lower < subject < upper
		lower = 4;
		upper = 6;
		assertTrue(Common.betweenInclusive(subject, lower, upper));
		
		// false partition, boundary case subject < lower
		lower = 6;
		assertFalse(Common.betweenInclusive(subject, lower, upper));
		
		// false partition, boundary case subject > upper
		lower = 3;
		upper = 4;
		assertFalse(Common.betweenInclusive(subject, lower, upper));
	}
	
	@Test
	public void testGetFirstWord() {
		String str = " +An apple a day reduces an apple in the world per day";
		String expected = "+An";
		assertEquals(Common.getFirstWord(str), expected);
	}

	@Test
	public void testRemoveFirstWord() {
		String str = "+one1   hundred percent pure apple juice";
		String expected = "hundred percent pure apple juice";
		assertEquals(Common.removeFirstWord(str), expected);
	}

	@Test
	public void testRemoveDuplicatesFromArrayList() {
		// Integer ArrayList
		ArrayList<Integer> arrInt = new ArrayList<Integer>();
		arrInt.add(1);
		arrInt.add(1);
		arrInt.add(1);
		arrInt.add(-1);
		arrInt.add(0);
		arrInt.add(10);
		ArrayList<Integer> expectedArrInt = new ArrayList<Integer>();
		expectedArrInt.add(1);
		expectedArrInt.add(-1);
		expectedArrInt.add(0);
		expectedArrInt.add(10);
		assertEquals(Common.removeDuplicatesFromArrayList(arrInt), expectedArrInt);
		
		// String ArrayList
		ArrayList<String> arrStr = new ArrayList<String>();
		arrStr.add("one");
		arrStr.add("one");
		arrStr.add("two");
		arrStr.add("twoo");
		arrStr.add("onemillion4");
		ArrayList<String> expectedArrStr = new ArrayList<String>();
		expectedArrStr.add("one");
		expectedArrStr.add("two");
		expectedArrStr.add("twoo");
		expectedArrStr.add("onemillion4");
		assertEquals(Common.removeDuplicatesFromArrayList(arrStr), expectedArrStr);
	}
	
	@Test
	public void testGetIdListString() {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1);
		arr.add(-1);
		arr.add(0);
		arr.add(50);
		String expected = "1, -1, 0, 50";
		assertEquals(Common.getIdListString(arr), expected);
	}
	
	@Test
	public void testGetUuidListString() {
		ArrayList<UUID> arr = new ArrayList<UUID>();
		UUID id1 = UUID.randomUUID();
		UUID id2 = UUID.randomUUID(); 
		UUID id3 = UUID.randomUUID(); 
		arr.add(id1);
		arr.add(id2);
		arr.add(id3);
		String expected = id1 + ", " + id2 + ", " + id3;
		assertEquals(Common.getUuidListString(arr), expected);
	}

}
