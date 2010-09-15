package org.corewall.scene.edit;

import junit.framework.TestCase;

import org.corewall.scene.edit.AbstractCommand;
import org.corewall.scene.edit.Command;
import org.corewall.scene.edit.CompositeCommand;
import org.junit.Test;

/**
 * Tests for {@link AbstractCommand} and {@link CompositeCommand}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CommandsTest extends TestCase {

	/**
	 * Tests initial state, executing, undoing, and redoing of a single command.
	 */
	@Test
	public void testCommand() {
		TestCommand command = new TestCommand();

		// test initial state
		assertTrue(command.canExecute());
		assertFalse(command.canUndo());
		assertEquals("TestCommand", command.getLabel());
		assertNull(command.last);

		// test execute
		command.execute();
		assertEquals("execute", command.last);
		assertFalse(command.canExecute());
		assertTrue(command.canUndo());

		// test undo
		command.undo();
		assertEquals("undo", command.last);
		assertTrue(command.canExecute());
		assertFalse(command.canUndo());

		// test redo
		command.redo();
		assertEquals("execute", command.last);
		assertFalse(command.canExecute());
		assertTrue(command.canUndo());
	}

	/**
	 * Tests initial state, executing, undoing, and redoing of a composite
	 * command.
	 */
	public void testCompositeCommand() {
		TestCommand c1 = new TestCommand();
		TestCommand c2 = new TestCommand();

		// test initial state
		Command command = new CompositeCommand("Composite", c1, c2);
		assertEquals("Composite", command.getLabel());
		assertNull(c1.last);
		assertNull(c2.last);

		// test execute
		assert command.canExecute();
		command.execute();
		assertEquals("execute", c1.last);
		assertEquals("execute", c2.last);

		// test undo
		assert command.canUndo();
		command.undo();
		assertEquals("undo", c1.last);
		assertEquals("undo", c2.last);

		// test redo
		assert command.canExecute();
		command.redo();
		assertEquals("execute", c1.last);
		assertEquals("execute", c2.last);
	}
}
