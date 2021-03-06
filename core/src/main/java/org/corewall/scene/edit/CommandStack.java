package org.corewall.scene.edit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Provides edit support.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CommandStack {
	/**
	 * The execute status property.
	 */
	public static final String EXECUTE_PROP = "execute";

	/**
	 * The undo status property.
	 */
	public static final String UNDO_PROP = "undo";

	/**
	 * The redo status property.
	 */
	public static final String REDO_PROP = "redo";

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandStack.class);
	protected final Stack<Command> commandStack;
	protected final Stack<Command> redoStack;
	protected final PropertyChangeSupport pcs;
	protected boolean editable = true;

	/**
	 * Create a new editable CommandStack.
	 */
	public CommandStack() {
		this(true);
	}

	/**
	 * Create a new CommandStack.
	 * 
	 * @param editable
	 *            the editable flag.
	 */
	public CommandStack(final boolean editable) {
		this.editable = editable;
		commandStack = new Stack<Command>();
		redoStack = new Stack<Command>();
		pcs = new PropertyChangeSupport(this);
		LOGGER.debug("initialized");
	}

	/**
	 * Adds a property change listener to listen for execute, undo, and redo
	 * status change events.
	 * 
	 * @see CommandStack#EXECUTE_PROP
	 * @see CommandStack#UNDO_PROP
	 * @see CommandStack#REDO_PROP
	 * 
	 * @param l
	 *            the listener.
	 */
	public void addPropertyChangeListener(final PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	/**
	 * Check whether this command stack can execute commands.
	 * 
	 * @return true if it can, false otherwise.
	 */
	public boolean canExecute() {
		return editable;
	}

	/**
	 * Checks whether the last command can be redone.
	 * 
	 * @return true if the last command can be redone, false otherwise.
	 */
	public boolean canRedo() {
		return editable && !redoStack.isEmpty() && redoStack.peek().canExecute();
	}

	/**
	 * Checks whether the last command can be undone.
	 * 
	 * @return true if the last command can be undone, false otherwise.
	 */
	public boolean canUndo() {
		return editable && !commandStack.isEmpty() && commandStack.peek().canUndo();
	}

	/**
	 * Executes the specified command.
	 * 
	 * @param command
	 *            the command.
	 */
	public void execute(final Command command) {
		if (editable && command.canExecute()) {
			boolean oldUndo = canUndo();
			boolean oldRedo = canRedo();
			commandStack.push(command);
			command.execute();
			LOGGER.debug("Executed command {}", command);
			pcs.firePropertyChange(UNDO_PROP, oldUndo, canUndo());
			pcs.firePropertyChange(REDO_PROP, oldRedo, canRedo());
		}
	}

	/**
	 * Gets the (possibly abbreviated) list of commands that have been executed.
	 * 
	 * @return the list of commands.
	 */
	public ImmutableList<Command> getCommands() {
		return ImmutableList.copyOf(commandStack);
	}

	/**
	 * Gets whether this command stack is editable.
	 * 
	 * @return true if is editable, false otherwise.
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Redo the last undone command.
	 */
	public void redo() {
		if (canRedo()) {
			boolean oldUndo = canUndo();
			boolean oldRedo = canRedo();
			Command command = redoStack.pop();
			commandStack.push(command);
			command.execute();
			LOGGER.debug("Redo command {}", command);
			pcs.firePropertyChange(UNDO_PROP, oldUndo, canUndo());
			pcs.firePropertyChange(REDO_PROP, oldRedo, canRedo());
		}
	}

	/**
	 * Removes a property change listener.
	 * 
	 * @param l
	 *            the property change listener.
	 */
	public void removePropertyChangeListener(final PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	/**
	 * Sets whether this edit support is editable.
	 * 
	 * @param editable
	 *            the editable flag.
	 */
	public void setEditable(final boolean editable) {
		boolean oldExecute = this.editable;
		boolean oldUndo = canUndo();
		boolean oldRedo = canRedo();
		this.editable = editable;
		pcs.firePropertyChange(EXECUTE_PROP, oldExecute, editable);
		pcs.firePropertyChange(UNDO_PROP, oldUndo, canUndo());
		pcs.firePropertyChange(REDO_PROP, oldRedo, canRedo());
	}

	/**
	 * Undo the last command.
	 */
	public void undo() {
		if (canUndo()) {
			boolean oldRedo = canRedo();
			redoStack.push(commandStack.pop());
			redoStack.peek().undo();
			LOGGER.debug("Undo command {}", redoStack.peek());
			pcs.firePropertyChange(UNDO_PROP, true, canUndo());
			pcs.firePropertyChange(REDO_PROP, oldRedo, canRedo());
		}
	}
}
