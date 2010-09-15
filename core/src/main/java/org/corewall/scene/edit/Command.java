package org.corewall.scene.edit;

/**
 * Defines the interface for a command which encapsulates some action that may be undoable.
 * 
 * @author Josh Reed (jareed@psicat.org)
 */
public interface Command {

	/**
	 * Check whether this command can execute.
	 * 
	 * @return true if the command can execute, false otherwise.
	 */
	boolean canExecute();

	/**
	 * Checks whether this command can be undone.
	 * 
	 * @return true if the command can be undone, false otherwise.
	 */
	boolean canUndo();

	/**
	 * Execute this command.
	 */
	void execute();

	/**
	 * Gets the label for this command.
	 * 
	 * @return the label.
	 */
	String getLabel();

	/**
	 * Re-execute this command.
	 */
	void redo();

	/**
	 * Undo this command.
	 */
	void undo();
}
