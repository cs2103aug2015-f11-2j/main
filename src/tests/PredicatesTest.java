package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

import app.logic.CommandController;
import app.logic.command.Command;
import app.model.Task;
import app.model.TaskList;
import app.util.Predicates;

public class PredicatesTest {
	
	// @@author A0132764E
	@Test
	public void testPredicates() {
		//building test environment
		TaskList list = new TaskList();
		assertTrue(list.getTaskList().isEmpty());
		String input = "add eat sandwich";
		Command cmd = CommandController.getInstance().createCommand(input);
		Task task = new Task(cmd);
		list.addTask(task);
		
		input = "add eat hotdog by 6/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		task = new Task(cmd);
		list.addTask(task);
		
		input = "add eat salad from 1/11/15 to 4/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		task = new Task(cmd);
		list.addTask(task);
		
		input = "add CS2103 presentation priority high";
		cmd = CommandController.getInstance().createCommand(input);
		task = new Task(cmd);
		list.addTask(task);
		
		TaskList master = CommandController.getInstance().getMasterTaskList();
		TaskList retrievedTaskList = master.getTaskListByCompletion(false);
		
		//Start test on search function
		List<Predicate<Task>> predicates = new ArrayList<Predicate<Task>>();
		assertTrue(predicates.isEmpty());
		
		//single parameter search test cases
		input = "search eat";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.keywordMatches(cmd.getContent()));
		assertFalse(predicates.isEmpty());
		TaskList results = retrievedTaskList.search(predicates);
		assertEquals(3, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search vegetables";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.keywordMatches(cmd.getContent()));
		assertFalse(predicates.isEmpty());
		results = retrievedTaskList.search(predicates);
		assertEquals(0, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search between 1/11/15 and 7/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.betweenDates(cmd.getStartDate(), cmd.getEndDate()));
		assertFalse(predicates.isEmpty());
		results = retrievedTaskList.search(predicates);
		assertEquals(2, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search after 5/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.startDateAfter(cmd.getStartDate()));
		assertFalse(predicates.isEmpty());
		results = retrievedTaskList.search(predicates);
		assertEquals(1, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search before 5/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.endDateBefore(cmd.getEndDate()));
		assertFalse(predicates.isEmpty());
		results = retrievedTaskList.search(predicates);
		assertEquals(1, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search before 5/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.endDateBefore(cmd.getEndDate()));
		assertFalse(predicates.isEmpty());
		results = retrievedTaskList.search(predicates);
		assertEquals(1, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search priority high";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.priorityEquals(cmd.getPriority()));
		assertFalse(predicates.isEmpty());
		results = retrievedTaskList.search(predicates);
		assertEquals(1, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search priority none";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.priorityEquals(cmd.getPriority()));
		assertFalse(predicates.isEmpty());
		results = retrievedTaskList.search(predicates);
		assertEquals(3, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		//multiple paramter search test cases
		input = "search eat before 4/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.keywordMatches(cmd.getContent()));
		results = retrievedTaskList.search(predicates);
		assertFalse(results.getTaskList().size() == 1);
		predicates.add(Predicates.endDateBefore(cmd.getEndDate()));
		results = retrievedTaskList.search(predicates);
		assertTrue(results.getTaskList().size() == 1);
		results = retrievedTaskList.search(predicates);
		assertEquals(1, results.getTaskList().size());
		predicates = new ArrayList<Predicate<Task>>();
		
		input = "search eat priority none after 4/11/15";
		cmd = CommandController.getInstance().createCommand(input);
		predicates.add(Predicates.keywordMatches(cmd.getContent()));
		predicates.add(Predicates.priorityEquals(cmd.getPriority()));
		predicates.add(Predicates.startDateAfter(cmd.getStartDate()));
		results = retrievedTaskList.search(predicates);
		assertTrue(results.getTaskList().size() == 2);
		
	}
}
