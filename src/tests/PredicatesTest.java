package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

import app.logic.CommandController;
import app.logic.command.Command;
import app.model.Task;
import app.util.Predicates;

public class PredicatesTest {
	
	@Test
	public void testPredicates() {
		//building test case
		String input = "add eat sandwich priority high";
		Command cmd = CommandController.getInstance().createCommand(input);

		List<Predicate<Task>> predicates = new ArrayList<Predicate<Task>>();
		predicates.add(Predicates.keywordMatches(cmd.getContent()));
		assertFalse(predicates.isEmpty());
		
		//more?
	}
}
