package org.corewall.scene.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.TestCase;

import org.corewall.scene.edit.CommandStack;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link CommandStack}
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CommandStackTest extends TestCase {

	/**
	 * A property change listener.
	 */
	public static class TestListener implements PropertyChangeListener {
		/**
		 * The last event.
		 */
		public PropertyChangeEvent last;

		public void propertyChange(final PropertyChangeEvent evt) {
			last = evt;
		}
	}

	private CommandStack edit;

	@Override
	@Before
	public void setUp() {
		edit = new CommandStack();
	}

	/**
	 * Tests executing commands.
	 */
	@Test
	public void testExecute() {
		TestListener listener = new TestListener();
		edit.addPropertyChangeListener(listener);

		TestCommand command = new TestCommand();
		edit.execute(command);
		assertTrue(edit.canExecute());
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		assertEquals(1, edit.getCommands().size());
		assertEquals(command, edit.getCommands().get(0));
		assertEquals("execute", command.last);
		assertEquals("undo", listener.last.getPropertyName());

		// undo
		edit.undo();
		assertTrue(edit.canExecute());
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		assertEquals(0, edit.getCommands().size());
		assertEquals("undo", command.last);
		assertEquals("redo", listener.last.getPropertyName());

		// redo
		edit.removePropertyChangeListener(listener);
		listener.last = null;
		edit.redo();
		assertTrue(edit.canExecute());
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		assertEquals(1, edit.getCommands().size());
		assertEquals(command, edit.getCommands().get(0));
		assertEquals("execute", command.last);
		assertNull(listener.last);
	}

	/**
	 * Tests the initial state.
	 */
	@Test
	public void testInitial() {
		assertTrue(edit.isEditable());
		assertTrue(edit.canExecute());
		assertFalse(edit.canUndo());
		assertFalse(edit.canRedo());
		assertEquals(0, edit.getCommands().size());
	}

	/**
	 * Tests the initial state of a read-only command stack.
	 */
	@Test
	public void testInitialReadOnly() {
		edit.setEditable(false);
		assertFalse(edit.isEditable());
		assertFalse(edit.canExecute());
		assertFalse(edit.canUndo());
		assertFalse(edit.canRedo());
		assertEquals(0, edit.getCommands().size());
	}
}
